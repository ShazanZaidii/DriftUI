package com.example.driftui

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Collections
import java.util.WeakHashMap
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

// =============================================================================================
// OPTIMIZATION: SCHEMA CACHE
// =============================================================================================

private object SchemaCache {
    private val cache = Collections.synchronizedMap(mutableMapOf<KClass<*>, List<KProperty1<*, *>>>())

    fun <T : Any> get(kClass: KClass<T>): List<KProperty1<T, *>> {
        @Suppress("UNCHECKED_CAST")
        return cache.getOrPut(kClass) {
            kClass.declaredMemberProperties.toList().onEach { it.isAccessible = true }
        } as List<KProperty1<T, *>>
    }
}

// =============================================================================================
// TYPES
// =============================================================================================

enum class Order { Ascending, Descending }
data class SortRule(val property: String, val order: Order)

fun <T> Sort(prop: KProperty1<T, *>, order: Order = Order.Ascending): SortRule {
    return SortRule(prop.name, order)
}

// =============================================================================================
// THE REGISTRY
// =============================================================================================

object DriftRegistry {
    val sourceMap = WeakHashMap<Any, String>()
    val idMap = WeakHashMap<Any, Long>()
    private val dbCache = mutableMapOf<String, DriftDatabaseHelper>()
    var context: Context? = null

    fun getDb(name: String): DriftDatabaseHelper {
        val ctx = context ?: throw IllegalStateException("DriftContext not initialized! Call DriftStore inside a Composable first.")
        return dbCache.getOrPut(name) { DriftDatabaseHelper(ctx, name) }
    }
}

// =============================================================================================
// API 1: SINGLETON MODE
// =============================================================================================

@Composable
inline fun <reified T : Any> DriftStore(fileName: String, default: T): MutableState<T> {
    val context = LocalContext.current
    if (DriftRegistry.context == null) DriftRegistry.context = context.applicationContext

    val state = remember { mutableStateOf(default, policy = neverEqualPolicy()) }

    LaunchedEffect(fileName) {
        withContext(Dispatchers.IO) {
            val db = DriftRegistry.getDb(fileName)
            db.ensureTable(default, isList = false)
            val persisted = db.readOne(T::class.simpleName ?: "Data", T::class)

            withContext(Dispatchers.Main) {
                if (persisted != null) state.value = persisted
                else withContext(Dispatchers.IO) { db.saveOne(T::class.simpleName ?: "Data", default) }
            }
        }
    }
    DriftRegistry.sourceMap[state] = fileName
    return state
}

fun <T : Any> MutableState<T>.edit(block: T.() -> Unit) {
    // LOOP BREAKER: Snapshot -> Run -> Compare
    val props = SchemaCache.get(this.value::class)
    val oldValues = props.map { it.getter.call(this.value) }

    this.value.block()

    val newValues = props.map { it.getter.call(this.value) }
    if (oldValues == newValues) return // Abort if unchanged

    val fileName = DriftRegistry.sourceMap[this] ?: return
    // Async save
    val db = DriftRegistry.getDb(fileName)
    db.saveOne(this.value::class.simpleName ?: "Data", this.value)
    this.value = this.value // Trigger Refresh
}

fun <T : Any> MutableState<T>.remove() {
    val fileName = DriftRegistry.sourceMap[this] ?: return
    DriftRegistry.getDb(fileName).nukeTable(this.value::class.simpleName ?: "Data")
}

// =============================================================================================
// API 2: COLLECTION MODE
// =============================================================================================

@Composable
fun <T : Any> DriftStore(fileName: String, kClass: KClass<T>): DriftListController<T> {
    val context = LocalContext.current
    if (DriftRegistry.context == null) DriftRegistry.context = context.applicationContext

    val controller = remember { DriftListController(fileName, kClass) }

    LaunchedEffect(fileName) {
        withContext(Dispatchers.IO) {
            val db = DriftRegistry.getDb(fileName)
            db.ensureTableFromClass(kClass, isList = true)
            controller.refreshInternal()
        }
    }
    return controller
}

class DriftListController<T : Any>(val fileName: String, val kClass: KClass<T>) {
    private val _items = mutableStateOf<List<T>>(emptyList(), policy = neverEqualPolicy())
    val items: List<T> get() = _items.value

    suspend fun refreshInternal() {
        val db = DriftRegistry.getDb(fileName)
        val data = db.readList(kClass.simpleName ?: "Data", kClass)
        withContext(Dispatchers.Main) {
            _items.value = data
        }
    }

    fun refresh() {
        val db = DriftRegistry.getDb(fileName)
        _items.value = db.readList(kClass.simpleName ?: "Data", kClass)
    }

    fun add(item: T) {
        val db = DriftRegistry.getDb(fileName)
        db.insertListItem(kClass.simpleName ?: "Data", item)
        refresh()
    }

    // NEW: Batch Add (Transactions)
    fun addAll(items: List<T>) {
        if (items.isEmpty()) return
        val db = DriftRegistry.getDb(fileName)
        db.batchInsert(kClass.simpleName ?: "Data", items)
        refresh()
    }

    // SAFE EDIT + LOOP BREAKER
    fun edit(item: T?, block: T.() -> Unit) {
        if (item == null) return
        val id = DriftRegistry.idMap[item] ?: return

        val props = SchemaCache.get(item::class)
        val oldValues = props.map { it.getter.call(item) }

        item.block()

        val newValues = props.map { it.getter.call(item) }
        if (oldValues == newValues) return // Abort

        val db = DriftRegistry.getDb(fileName)
        db.updateListItem(kClass.simpleName ?: "Data", item, id)
        refresh()
    }

    fun remove(item: T) {
        val id = DriftRegistry.idMap[item] ?: return
        val db = DriftRegistry.getDb(fileName)
        db.deleteListItem(kClass.simpleName ?: "Data", id)
        refresh()
    }

    fun removeAll() {
        val db = DriftRegistry.getDb(fileName)
        db.nukeTable(kClass.simpleName ?: "Data")
        refresh()
    }

    fun removeById(id: Long) {
        val db = DriftRegistry.getDb(fileName)
        db.deleteListItem(kClass.simpleName ?: "Data", id)
        refresh()
    }

    // --- REACTIVE QUERIES ---
    @Composable
    fun query(where: String? = null, orderBy: String? = null): List<T> {
        val trigger = _items.value
        val result = produceState(initialValue = emptyList<T>(), key1 = trigger, key2 = where, key3 = orderBy) {
            withContext(Dispatchers.IO) {
                val db = DriftRegistry.getDb(fileName)
                value = db.readList(kClass.simpleName ?: "Data", kClass, where, orderBy)
            }
        }
        return result.value
    }

    @Composable
    fun sort(by: KProperty1<T, *>, order: Order = Order.Ascending): List<T> {
        val direction = if (order == Order.Ascending) "ASC" else "DESC"
        return query(orderBy = "${by.name} $direction")
    }

    @Composable
    fun sort(vararg rules: SortRule): List<T> {
        val clause = rules.joinToString(", ") { "${it.property} ${if (it.order == Order.Ascending) "ASC" else "DESC"}" }
        return query(orderBy = clause)
    }

    // --- STREAMING FILTER (Supports exact syntax: it.age > 5) ---
    @Composable
    fun filter(predicate: (T) -> Boolean): List<T> {
        val trigger = _items.value
        val result = produceState(initialValue = emptyList<T>(), key1 = trigger) {
            withContext(Dispatchers.IO) {
                val db = DriftRegistry.getDb(fileName)
                value = db.readAndFilter(kClass.simpleName ?: "Data", kClass, predicate)
            }
        }
        return result.value
    }
}

// =============================================================================================
// THE UNIFIED SQLITE BACKEND
// =============================================================================================

class DriftDatabaseHelper(context: Context, name: String) :
    SQLiteOpenHelper(context, "$name.sqlite", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {}
    override fun onUpgrade(db: SQLiteDatabase, o: Int, n: Int) {}

    fun ensureTable(obj: Any, isList: Boolean) {
        ensureTableFromClass(obj::class, isList)
    }

    // UPDATED: With Schema Migration (ALTER TABLE)
    fun ensureTableFromClass(kClass: KClass<*>, isList: Boolean) {
        val tableName = kClass.simpleName ?: "Data"
        val db = writableDatabase

        // 1. Create if missing
        val idType = if (isList) "INTEGER PRIMARY KEY AUTOINCREMENT" else "INTEGER PRIMARY KEY DEFAULT 0"
        val sb = StringBuilder("CREATE TABLE IF NOT EXISTS $tableName (id $idType")
        val props = SchemaCache.get(kClass)

        props.forEach { prop ->
            // FIX: Cast KClassifier to KClass
            val type = getSqlType(prop.returnType.classifier as? KClass<*>)
            sb.append(", ${prop.name} $type")
        }
        sb.append(")")
        db.execSQL(sb.toString())

        // 2. Auto-Migration (Check for missing columns)
        try {
            val cursor = db.rawQuery("PRAGMA table_info($tableName)", null)
            val existingColumns = mutableSetOf<String>()
            val nameIdx = cursor.getColumnIndex("name")
            while (cursor.moveToNext()) {
                existingColumns.add(cursor.getString(nameIdx))
            }
            cursor.close()

            props.forEach { prop ->
                if (!existingColumns.contains(prop.name)) {
                    // FIX: Cast KClassifier to KClass
                    val type = getSqlType(prop.returnType.classifier as? KClass<*>)
                    // Safe default for new columns is NULL or 0/"" based on type logic if needed
                    // Here we let SQLite handle default NULLs
                    db.execSQL("ALTER TABLE $tableName ADD COLUMN ${prop.name} $type")
                }
            }
        } catch (e: Exception) { e.printStackTrace() }
    }

    private fun getSqlType(classifier: KClass<*>?): String {
        return when(classifier) {
            String::class -> "TEXT"
            Int::class -> "INTEGER"
            Boolean::class -> "INTEGER"
            Float::class -> "REAL"
            Double::class -> "REAL"
            else -> "TEXT"
        }
    }

    // --- WRITE OPS ---

    fun saveOne(table: String, obj: Any) {
        val cv = objectToContentValues(obj)
        cv.put("id", 0)
        writableDatabase.insertWithOnConflict(table, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun insertListItem(table: String, obj: Any) {
        val cv = objectToContentValues(obj)
        cv.remove("id")
        writableDatabase.insert(table, null, cv)
    }

    // NEW: Batch Transaction
    fun batchInsert(table: String, items: List<Any>) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            for (item in items) {
                val cv = objectToContentValues(item)
                cv.remove("id")
                db.insert(table, null, cv)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun updateListItem(table: String, obj: Any, id: Long) {
        val cv = objectToContentValues(obj)
        writableDatabase.update(table, cv, "id = ?", arrayOf(id.toString()))
    }

    fun deleteListItem(table: String, id: Long) {
        writableDatabase.delete(table, "id = ?", arrayOf(id.toString()))
    }

    fun deleteWhere(table: String, whereClause: String, args: Array<String>?) {
        writableDatabase.delete(table, whereClause, args)
    }

    fun nukeTable(table: String) {
        writableDatabase.execSQL("DELETE FROM $table")
    }

    // --- READ ENGINE (Constructor-Safe) ---

    fun <T : Any> readOne(table: String, kClass: KClass<T>): T? {
        val list = readList(table, kClass, "id = 0", null)
        return list.firstOrNull()
    }

    fun <T : Any> readList(table: String, kClass: KClass<T>, selection: String? = null, orderBy: String? = null): List<T> {
        val db = readableDatabase
        try {
            val check = db.rawQuery("SELECT 1 FROM $table LIMIT 1", null)
            check.close()
        } catch (e: Exception) { return emptyList() }

        val cursor = db.query(table, null, selection, null, null, null, orderBy)
        val results = mutableListOf<T>()

        val ctor = kClass.primaryConstructor ?: kClass.constructors.first()
        val parameters = ctor.parameters
        val colIndices = parameters.map { cursor.getColumnIndex(it.name) }
        val classifiers = parameters.map { it.type.classifier }
        val idIdx = cursor.getColumnIndex("id")

        while (cursor.moveToNext()) {
            val params = arrayOfNulls<Any?>(parameters.size)
            for (i in colIndices.indices) {
                val idx = colIndices[i]
                if (idx == -1 || cursor.isNull(idx)) {
                    params[i] = null
                } else {
                    params[i] = when (classifiers[i]) {
                        String::class -> cursor.getString(idx)
                        Int::class -> cursor.getInt(idx)
                        Boolean::class -> cursor.getInt(idx) == 1
                        Float::class -> cursor.getFloat(idx)
                        Double::class -> cursor.getDouble(idx)
                        else -> cursor.getString(idx)
                    }
                }
            }
            try {
                val instance = ctor.call(*params)
                if (idIdx != -1) DriftRegistry.idMap[instance] = cursor.getLong(idIdx)
                results.add(instance)
            } catch (e: Exception) { e.printStackTrace() }
        }
        cursor.close()
        return results
    }

    // --- STREAMING FILTER ENGINE (Memory-Safe) ---
    fun <T : Any> readAndFilter(table: String, kClass: KClass<T>, predicate: (T) -> Boolean): List<T> {
        val db = readableDatabase
        try {
            val check = db.rawQuery("SELECT 1 FROM $table LIMIT 1", null)
            check.close()
        } catch (e: Exception) { return emptyList() }

        val cursor = db.query(table, null, null, null, null, null, null)
        val results = mutableListOf<T>()
        val ctor = kClass.primaryConstructor ?: kClass.constructors.first()
        val parameters = ctor.parameters
        val colIndices = parameters.map { cursor.getColumnIndex(it.name) }
        val classifiers = parameters.map { it.type.classifier }
        val idIdx = cursor.getColumnIndex("id")

        while (cursor.moveToNext()) {
            val params = arrayOfNulls<Any?>(parameters.size)
            for (i in colIndices.indices) {
                val idx = colIndices[i]
                if (idx == -1 || cursor.isNull(idx)) {
                    params[i] = null
                } else {
                    params[i] = when (classifiers[i]) {
                        String::class -> cursor.getString(idx)
                        Int::class -> cursor.getInt(idx)
                        Boolean::class -> cursor.getInt(idx) == 1
                        Float::class -> cursor.getFloat(idx)
                        Double::class -> cursor.getDouble(idx)
                        else -> cursor.getString(idx)
                    }
                }
            }
            try {
                val instance = ctor.call(*params)
                // Filter immediately, discard if false
                if (predicate(instance)) {
                    if (idIdx != -1) DriftRegistry.idMap[instance] = cursor.getLong(idIdx)
                    results.add(instance)
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
        cursor.close()
        return results
    }

    private fun objectToContentValues(obj: Any): ContentValues {
        val cv = ContentValues()
        SchemaCache.get(obj::class).forEach { prop ->
            val value = prop.getter.call(obj)
            when(value) {
                is String -> cv.put(prop.name, value)
                is Int -> cv.put(prop.name, value)
                is Boolean -> cv.put(prop.name, if(value) 1 else 0)
                is Float -> cv.put(prop.name, value)
                is Double -> cv.put(prop.name, value)
                null -> cv.putNull(prop.name)
            }
        }
        return cv
    }
}

val Any.driftId: Long
    get() = DriftRegistry.idMap[this] ?: -1L