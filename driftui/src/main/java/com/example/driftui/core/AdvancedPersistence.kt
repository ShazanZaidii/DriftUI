package com.example.driftui.core

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Collections
import java.util.WeakHashMap
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.json.JSONArray

// =============================================================================================
// OPTIMIZATION: SCHEMA CACHE & IDENTITY REFLECTION
// =============================================================================================

private object SchemaCache {
    private val cache = Collections.synchronizedMap(mutableMapOf<KClass<*>, SchemaData>())

    data class SchemaData(
        val allProps: List<KProperty1<*, *>>,
        val idProp: KMutableProperty1<Any, *>? // Mutable ID property if it exists
    )

    fun <T : Any> get(kClass: KClass<T>): SchemaData {
        @Suppress("UNCHECKED_CAST")
        return cache.getOrPut(kClass) {
            val props = kClass.declaredMemberProperties.toList().onEach { it.isAccessible = true }
            val id = props.find { it.name.equals("id", ignoreCase = true) && it is KMutableProperty1 }
                    as? KMutableProperty1<Any, *>
            SchemaData(props, id)
        }
    }
}

// =============================================================================================
// TYPES & ENUMS
// =============================================================================================

enum class Order { Ascending, Descending }
data class SortRule(val property: String, val order: Order)
enum class SyncAction { Insert, Update, Delete }

fun <T> Sort(prop: KProperty1<T, *>, order: Order = Order.Ascending) = SortRule(prop.name, order)

// =============================================================================================
// THE REGISTRY (HYBRID STORAGE)
// =============================================================================================

object DriftRegistry {
    val sourceMap = WeakHashMap<Any, String>()
    val idMap = WeakHashMap<Any, Long>()

    val ioScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val dbCache = mutableMapOf<String, DriftDatabaseHelper>()
    var context: Context? = null

    // FIX 1: Initialize function for MainActivity
    fun initialize(ctx: Context) {
        this.context = ctx.applicationContext
    }

    fun getDb(name: String): DriftDatabaseHelper {
        val ctx = context ?: throw IllegalStateException("DriftContext not initialized! Call DriftRegistry.initialize(context) in MainActivity.")
        return dbCache.getOrPut(name) { DriftDatabaseHelper(ctx, name) }
    }

    fun getId(item: Any): Long {
        val schema = SchemaCache.get(item::class)
        if (schema.idProp != null) {
            val idVal = schema.idProp.getter.call(item)
            if (idVal is Number) return idVal.toLong()
        }
        return idMap[item] ?: -1L
    }

    fun setId(item: Any, id: Long) {
        val schema = SchemaCache.get(item::class)
        if (schema.idProp != null) {
            try {
                val setter = schema.idProp.setter
                when (schema.idProp.returnType.classifier) {
                    Int::class -> setter.call(item, id.toInt())
                    Long::class -> setter.call(item, id)
                    else -> {}
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
        idMap[item] = id
    }
}

// =============================================================================================
// API 1: SINGLETON MODE
// =============================================================================================

// 1. COMPOSABLE VERSION (Global)
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

// 2. VIEWMODEL EXTENSION (Scoped to ViewModel - Fixes Ambiguity)
inline fun <reified T : Any> ViewModel.DriftStore(fileName: String, default: T): MutableState<T> {
    if (DriftRegistry.context == null) throw IllegalStateException("Call DriftRegistry.initialize(context) in MainActivity first.")

    val state = mutableStateOf(default, policy = neverEqualPolicy())
    DriftRegistry.sourceMap[state] = fileName

    DriftRegistry.ioScope.launch {
        val db = DriftRegistry.getDb(fileName)
        db.ensureTable(default, isList = false)
        val persisted = db.readOne(T::class.simpleName ?: "Data", T::class)

        withContext(Dispatchers.Main) {
            if (persisted != null) state.value = persisted
            else withContext(Dispatchers.IO) { db.saveOne(T::class.simpleName ?: "Data", default) }
        }
    }
    return state
}

fun <T : Any> MutableState<T>.edit(block: T.() -> Unit) {
    val schema = SchemaCache.get(this.value::class)
    val oldValues = schema.allProps.map { it.getter.call(this.value) }

    this.value.block()

    val newValues = schema.allProps.map { it.getter.call(this.value) }
    if (oldValues == newValues) return

    val fileName = DriftRegistry.sourceMap[this] ?: return
    DriftRegistry.getDb(fileName).saveOne(this.value::class.simpleName ?: "Data", this.value)
    this.value = this.value
}

fun <T : Any> MutableState<T>.remove() {
    val fileName = DriftRegistry.sourceMap[this] ?: return
    DriftRegistry.getDb(fileName).nukeTable(this.value::class.simpleName ?: "Data")
}

fun <T : Any> MutableState<T>.set(newValue: T) {
    this.value = newValue
    DriftRegistry.ioScope.launch {
        val fileName = DriftRegistry.sourceMap[this@set] ?: return@launch
        val db = DriftRegistry.getDb(fileName)
        db.saveOne(newValue::class.simpleName ?: "Data", newValue)
    }
}

// =============================================================================================
// API 2: COLLECTION MODE
// =============================================================================================

// 1. COMPOSABLE VERSION (Global)
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

// 2. VIEWMODEL EXTENSION (Scoped to ViewModel - Fixes Ambiguity)
fun <T : Any> ViewModel.DriftStore(fileName: String, kClass: KClass<T>): DriftListController<T> {
    if (DriftRegistry.context == null) throw IllegalStateException("Call DriftRegistry.initialize(context) in MainActivity first.")

    val controller = DriftListController(fileName, kClass)

    DriftRegistry.ioScope.launch {
        val db = DriftRegistry.getDb(fileName)
        db.ensureTableFromClass(kClass, isList = true)
        controller.refreshInternal()
    }
    return controller
}

class DriftListController<T : Any>(val fileName: String, val kClass: KClass<T>) {
    private val _items = mutableStateOf<List<T>>(emptyList(), policy = neverEqualPolicy())
    val items: List<T> get() = _items.value

    private var syncCallback: ((SyncAction, T) -> Unit)? = null

    fun onSync(callback: (SyncAction, T) -> Unit) {
        this.syncCallback = callback
    }

    suspend fun refreshInternal() {
        val db = DriftRegistry.getDb(fileName)
        val data = db.readList(kClass.simpleName ?: "Data", kClass)
        withContext(Dispatchers.Main) { _items.value = data }
    }

    fun refresh() {
        DriftRegistry.ioScope.launch {
            refreshInternal()
        }
    }

    // --- NEW: THE MAGIC SEEDER ---
    // Automatically ensures DB exists, checks duplicates, and inserts in one go.
    fun seed(vararg items: T, uniqueBy: (T) -> Any) {
        DriftRegistry.ioScope.launch {
            val db = DriftRegistry.getDb(fileName)

            // 1. Safety Check: Ensure table exists (Blocking call in background thread)
            db.ensureTableFromClass(kClass, isList = true)

            // 2. Read Existing
            val current = db.readList(kClass.simpleName ?: "Data", kClass)
            val existingKeys = current.map(uniqueBy).toSet()

            // 3. Filter out items that already exist
            val toAdd = items.filter { uniqueBy(it) !in existingKeys }

            // 4. Batch Insert
            if (toAdd.isNotEmpty()) {
                db.batchInsert(kClass.simpleName ?: "Data", toAdd)
                refreshInternal()
            }
        }
    }

    fun add(item: T) {
        DriftRegistry.ioScope.launch {
            val db = DriftRegistry.getDb(fileName)
            db.insertListItem(kClass.simpleName ?: "Data", item)
            withContext(Dispatchers.Main) {
                syncCallback?.invoke(SyncAction.Insert, item)
            }
            refreshInternal()
        }
    }

    fun addAll(items: List<T>) {
        if (items.isEmpty()) return
        DriftRegistry.ioScope.launch {
            val db = DriftRegistry.getDb(fileName)
            db.batchInsert(kClass.simpleName ?: "Data", items)
            withContext(Dispatchers.Main) {
                items.forEach { syncCallback?.invoke(SyncAction.Insert, it) }
            }
            refreshInternal()
        }
    }

    fun edit(item: T?, block: T.() -> Unit) {
        if (item == null) return
        val id = DriftRegistry.getId(item)
        if (id == -1L) return

        val schema = SchemaCache.get(item::class)
        val oldValues = schema.allProps.map { it.getter.call(item) }

        item.block()

        val newValues = schema.allProps.map { it.getter.call(item) }
        if (oldValues == newValues) return

        DriftRegistry.ioScope.launch {
            val db = DriftRegistry.getDb(fileName)
            db.updateListItem(kClass.simpleName ?: "Data", item, id)
            withContext(Dispatchers.Main) {
                syncCallback?.invoke(SyncAction.Update, item)
            }
            refreshInternal()
        }
    }

    fun remove(item: T) {
        val id = DriftRegistry.getId(item)
        if (id == -1L) return

        DriftRegistry.ioScope.launch {
            val db = DriftRegistry.getDb(fileName)
            db.deleteListItem(kClass.simpleName ?: "Data", id)
            withContext(Dispatchers.Main) {
                syncCallback?.invoke(SyncAction.Delete, item)
            }
            refreshInternal()
        }
    }

    fun removeAll() {
        DriftRegistry.ioScope.launch {
            val db = DriftRegistry.getDb(fileName)
            db.nukeTable(kClass.simpleName ?: "Data")
            refreshInternal()
        }
    }

    fun removeById(id: Long) {
        DriftRegistry.ioScope.launch {
            val db = DriftRegistry.getDb(fileName)
            db.deleteListItem(kClass.simpleName ?: "Data", id)
            refreshInternal()
        }
    }

    // --- QUERY HELPERS ---

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
        return query(orderBy = "${by.name} ${if (order == Order.Ascending) "ASC" else "DESC"}")
    }

    @Composable
    fun sort(vararg rules: SortRule): List<T> {
        val clause = rules.joinToString(", ") { "${it.property} ${if (it.order == Order.Ascending) "ASC" else "DESC"}" }
        return query(orderBy = clause)
    }

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
// DATABASE HELPER
// =============================================================================================

class DriftDatabaseHelper(context: Context, name: String) :
    SQLiteOpenHelper(context, "$name.sqlite", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {}
    override fun onUpgrade(db: SQLiteDatabase, o: Int, n: Int) {}

    fun ensureTable(obj: Any, isList: Boolean) { ensureTableFromClass(obj::class, isList) }

    fun ensureTableFromClass(kClass: KClass<*>, isList: Boolean) {
        val tableName = kClass.simpleName ?: "Data"
        val db = writableDatabase

        val idType = if (isList) "INTEGER PRIMARY KEY AUTOINCREMENT" else "INTEGER PRIMARY KEY DEFAULT 0"
        val sb = StringBuilder("CREATE TABLE IF NOT EXISTS $tableName (id $idType")
        val schema = SchemaCache.get(kClass)

        schema.allProps.forEach { prop ->
            if (!prop.name.equals("id", ignoreCase = true)) {
                val type = getSqlType(prop.returnType.classifier as? KClass<*>)
                sb.append(", ${prop.name} $type")
            }
        }
        sb.append(")")
        db.execSQL(sb.toString())

        try {
            val cursor = db.rawQuery("PRAGMA table_info($tableName)", null)
            val existingColumns = mutableSetOf<String>()
            val nameIdx = cursor.getColumnIndex("name")
            while (cursor.moveToNext()) existingColumns.add(cursor.getString(nameIdx))
            cursor.close()

            schema.allProps.forEach { prop ->
                if (!prop.name.equals("id", ignoreCase = true) && !existingColumns.contains(prop.name)) {
                    val type = getSqlType(prop.returnType.classifier as? KClass<*>)
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
            List::class -> "TEXT"
            else -> "TEXT"
        }
    }

    fun saveOne(table: String, obj: Any) {
        val cv = objectToContentValues(obj)
        cv.put("id", 0)
        writableDatabase.insertWithOnConflict(table, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun insertListItem(table: String, obj: Any) {
        val cv = objectToContentValues(obj)
        cv.remove("id")
        val newId = writableDatabase.insert(table, null, cv)
        if (newId != -1L) DriftRegistry.setId(obj, newId)
    }

    fun batchInsert(table: String, items: List<Any>) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            for (item in items) {
                val cv = objectToContentValues(item)
                cv.remove("id")
                val newId = db.insert(table, null, cv)
                if (newId != -1L) DriftRegistry.setId(item, newId)
            }
            db.setTransactionSuccessful()
        } finally { db.endTransaction() }
    }

    fun updateListItem(table: String, obj: Any, id: Long) {
        val cv = objectToContentValues(obj)
        cv.remove("id")
        writableDatabase.update(table, cv, "id = ?", arrayOf(id.toString()))
    }

    fun deleteListItem(table: String, id: Long) {
        writableDatabase.delete(table, "id = ?", arrayOf(id.toString()))
    }

    fun nukeTable(table: String) {
        writableDatabase.execSQL("DELETE FROM $table")
    }

    fun <T : Any> readOne(table: String, kClass: KClass<T>): T? {
        val list = readList(table, kClass, "id = 0", null)
        return list.firstOrNull()
    }

    fun <T : Any> readList(table: String, kClass: KClass<T>, selection: String? = null, orderBy: String? = null): List<T> {
        val db = readableDatabase
        try {
            val check = db.rawQuery("SELECT 1 FROM $table LIMIT 1", null); check.close()
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
                if (parameters[i].name.equals("id", ignoreCase = true) && idIdx != -1) {
                    val dbId = cursor.getLong(idIdx)
                    val type = classifiers[i] as? KClass<*>
                    params[i] = if (type == Int::class) dbId.toInt() else dbId
                    continue
                }

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
                        List::class -> {
                            val rawJson = cursor.getString(idx)
                            try {
                                val jsonArray = JSONArray(rawJson)
                                val list = mutableListOf<String>()
                                for (j in 0 until jsonArray.length()) { list.add(jsonArray.getString(j)) }
                                list
                            } catch (e: Exception) { emptyList<String>() }
                        }
                        else -> cursor.getString(idx)
                    }
                }
            }
            try {
                val instance = ctor.call(*params)
                if (idIdx != -1) DriftRegistry.setId(instance, cursor.getLong(idIdx))
                results.add(instance)
            } catch (e: Exception) { e.printStackTrace() }
        }
        cursor.close()
        return results
    }

    fun <T : Any> readAndFilter(table: String, kClass: KClass<T>, predicate: (T) -> Boolean): List<T> {
        val all = readList(table, kClass)
        return all.filter(predicate)
    }

    private fun objectToContentValues(obj: Any): ContentValues {
        val cv = ContentValues()
        SchemaCache.get(obj::class).allProps.forEach { prop ->
            if (prop.name.equals("id", ignoreCase = true)) return@forEach

            val value = prop.getter.call(obj)
            when(value) {
                is String -> cv.put(prop.name, value)
                is Int -> cv.put(prop.name, value)
                is Boolean -> cv.put(prop.name, if(value) 1 else 0)
                is Float -> cv.put(prop.name, value)
                is Double -> cv.put(prop.name, value)
                is List<*> -> {
                    val json = JSONArray(value).toString()
                    cv.put(prop.name, json)
                }
                null -> cv.putNull(prop.name)
                else -> cv.put(prop.name, value.toString())
            }
        }
        return cv
    }
}

val Any.driftId: Long
    get() = DriftRegistry.getId(this)