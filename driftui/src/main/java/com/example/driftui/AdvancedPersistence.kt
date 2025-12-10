//package com.example.driftui
//
//import android.content.ContentValues
//import android.content.Context
//import android.database.sqlite.SQLiteDatabase
//import android.database.sqlite.SQLiteOpenHelper
//import androidx.compose.runtime.*
//import androidx.compose.ui.platform.LocalContext
//import java.util.WeakHashMap
//import kotlin.reflect.full.declaredMemberProperties
//import kotlin.reflect.full.primaryConstructor
//import kotlin.reflect.jvm.isAccessible
//
//// =============================================================================================
//// THE REGISTRY
//// =============================================================================================
//
//object DriftRegistry {
//    // Maps the State Instance -> The Database Filename
//    val stateMap = WeakHashMap<MutableState<*>, String>()
//    val dbCache = mutableMapOf<String, DriftDatabaseHelper>()
//    var context: Context? = null
//
//    fun getDb(name: String): DriftDatabaseHelper {
//        val ctx = context ?: throw IllegalStateException("DriftContext not initialized!")
//        return dbCache.getOrPut(name) { DriftDatabaseHelper(ctx, name) }
//    }
//}
//
//// =============================================================================================
//// THE API
//// =============================================================================================
//
///**
// * Returns a REACTIVE MutableState.
// * - Loading: Checks disk first.
// * - Reactivity: Uses neverEqualPolicy() so updates to 'var' fields trigger UI refresh.
// */
//@Composable
//inline fun <reified T : Any> DriftStore(fileName: String, default: T): MutableState<T> {
//    val context = LocalContext.current
//    if (DriftRegistry.context == null) DriftRegistry.context = context.applicationContext
//
//    // 1. Create the State with "Never Equal" policy.
//    // This ensures that even if we modify the object in place, Compose redraws.
//    val state = remember {
//        mutableStateOf(default, policy = neverEqualPolicy())
//    }
//
//    // 2. Load from Disk (Side Effect to run once)
//    LaunchedEffect(fileName) {
//        val dbHelper = DriftRegistry.getDb(fileName)
//        dbHelper.ensureTable(default)
//
//        val persisted = dbHelper.readOne(T::class.simpleName ?: "Data", T::class.java)
//        if (persisted != null) {
//            state.value = persisted
//        } else {
//            // First time? Save the default to disk immediately.
//            dbHelper.save(T::class.simpleName ?: "Data", default)
//        }
//    }
//
//    // 3. Register for .edit() access
//    DriftRegistry.stateMap[state] = fileName
//
//    return state
//}
//
///**
// * The Reactive Edit Block.
// * 1. Updates RAM
// * 2. Saves to Disk
// * 3. Forces UI Refresh
// */
//fun <T : Any> MutableState<T>.edit(block: T.() -> Unit) {
//    // A. Run the user's change (e.g., name = "New")
//    this.value.block()
//
//    // B. Find the file
//    val fileName = DriftRegistry.stateMap[this] ?: return
//    val dbHelper = DriftRegistry.getDb(fileName)
//
//    // C. Save to Disk
//    dbHelper.save(this.value::class.simpleName ?: "Data", this.value)
//
//    // D. FORCE RECOMPOSE
//    // Because we used neverEqualPolicy(), setting the value to itself
//    // tells Compose: "Something inside changed, redraw everything!"
//    this.value = this.value
//}
//
///**
// * Removes data from disk.
// */
//fun <T : Any> MutableState<T>.remove() {
//    val fileName = DriftRegistry.stateMap[this] ?: return
//    val dbHelper = DriftRegistry.getDb(fileName)
//    dbHelper.nukeTable(this.value::class.simpleName ?: "Data")
//}
//
//// =============================================================================================
//// THE SQLITE BACKEND
//// =============================================================================================
//
//class DriftDatabaseHelper(context: Context, name: String) :
//    SQLiteOpenHelper(context, "$name.sqlite", null, 1) {
//
//    override fun onCreate(db: SQLiteDatabase) {}
//    override fun onUpgrade(db: SQLiteDatabase, o: Int, n: Int) {}
//
//    fun ensureTable(obj: Any) {
//        val tableName = obj::class.simpleName ?: "Data"
//        val sb = StringBuilder("CREATE TABLE IF NOT EXISTS $tableName (id INTEGER PRIMARY KEY DEFAULT 0")
//
//        obj::class.declaredMemberProperties.forEach { prop ->
//            val type = when(prop.returnType.classifier) {
//                String::class -> "TEXT"
//                Int::class -> "INTEGER"
//                Boolean::class -> "INTEGER"
//                Float::class -> "REAL"
//                Double::class -> "REAL"
//                else -> "TEXT"
//            }
//            sb.append(", ${prop.name} $type")
//        }
//        sb.append(")")
//        writableDatabase.execSQL(sb.toString())
//    }
//
//    fun save(table: String, obj: Any) {
//        val cv = ContentValues()
//        cv.put("id", 0) // Singleton Row Strategy
//
//        obj::class.declaredMemberProperties.forEach { prop ->
//            prop.isAccessible = true
//            val value = prop.getter.call(obj)
//            when(value) {
//                is String -> cv.put(prop.name, value)
//                is Int -> cv.put(prop.name, value)
//                is Boolean -> cv.put(prop.name, if(value) 1 else 0)
//                is Float -> cv.put(prop.name, value)
//                is Double -> cv.put(prop.name, value)
//            }
//        }
//
//        // Critical: Insert with REPLACE strategy
//        writableDatabase.insertWithOnConflict(table, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
//    }
//
//    fun <T : Any> readOne(table: String, clazz: Class<T>): T? {
//        val db = readableDatabase
//
//        // Check if table exists to avoid crash on fresh install
//        val check = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='$table'", null)
//        if (!check.moveToFirst()) {
//            check.close()
//            return null
//        }
//        check.close()
//
//        val cursor = db.query(table, null, "id = 0", null, null, null, null)
//        if (!cursor.moveToFirst()) {
//            cursor.close()
//            return null
//        }
//
//        val ctor = clazz.kotlin.primaryConstructor ?: clazz.kotlin.constructors.first()
//
//        val params = ctor.parameters.map { param ->
//            val idx = cursor.getColumnIndex(param.name)
//            // If column is missing (schema change), return generic default to avoid crash
//            if (idx == -1) return@map null
//
//            // FIX: Check for NULL before reading primitive types
//            if (cursor.isNull(idx)) {
//                null
//            } else {
//                when(param.type.classifier) {
//                    String::class -> cursor.getString(idx)
//                    Int::class -> cursor.getInt(idx)
//                    Boolean::class -> cursor.getInt(idx) == 1
//                    Float::class -> cursor.getFloat(idx)
//                    Double::class -> cursor.getDouble(idx)
//                    else -> ""
//                }
//            }
//        }
//
//        cursor.close()
//        return ctor.call(*params.toTypedArray())
//    }
//
//    fun nukeTable(table: String) {
//        writableDatabase.execSQL("DELETE FROM $table")
//    }
//}



// Version2:

//package com.example.driftui
//
//import android.content.ContentValues
//import android.content.Context
//import android.database.sqlite.SQLiteDatabase
//import android.database.sqlite.SQLiteOpenHelper
//import androidx.compose.runtime.*
//import androidx.compose.ui.platform.LocalContext
//import java.util.WeakHashMap
//import kotlin.reflect.KClass
//import kotlin.reflect.KProperty1 // Required for Type-Safe Sorting
//import kotlin.reflect.full.declaredMemberProperties
//import kotlin.reflect.full.primaryConstructor
//import kotlin.reflect.jvm.isAccessible
//
//// =============================================================================================
//// NEW: SORTING TYPES
//// =============================================================================================
//
//enum class Order { Ascending, Descending }
//
//// Helper class to hold a sort instruction
//data class SortRule(val property: String, val order: Order)
//
//// Constructor-like function allows syntax: Sort(User::name)
//fun <T> Sort(prop: KProperty1<T, *>, order: Order = Order.Ascending): SortRule {
//    return SortRule(prop.name, order)
//}
//
//// =============================================================================================
//// THE REGISTRY
//// =============================================================================================
//
//object DriftRegistry {
//    val sourceMap = WeakHashMap<Any, String>()
//    val idMap = WeakHashMap<Any, Long>()
//    val dbCache = mutableMapOf<String, DriftDatabaseHelper>()
//    var context: Context? = null
//
//    fun getDb(name: String): DriftDatabaseHelper {
//        val ctx = context ?: throw IllegalStateException("DriftContext not initialized! Call DriftStore inside a Composable first.")
//        return dbCache.getOrPut(name) { DriftDatabaseHelper(ctx, name) }
//    }
//}
//
//// =============================================================================================
//// API 1: SINGLETON MODE
//// =============================================================================================
//
//@Composable
//inline fun <reified T : Any> DriftStore(fileName: String, default: T): MutableState<T> {
//    val context = LocalContext.current
//    if (DriftRegistry.context == null) DriftRegistry.context = context.applicationContext
//
//    val state = remember { mutableStateOf(default, policy = neverEqualPolicy()) }
//
//    LaunchedEffect(fileName) {
//        val db = DriftRegistry.getDb(fileName)
//        db.ensureTable(default, isList = false)
//        val persisted = db.readOne(T::class.simpleName ?: "Data", T::class.java)
//        if (persisted != null) state.value = persisted
//        else db.saveOne(T::class.simpleName ?: "Data", default)
//    }
//    DriftRegistry.sourceMap[state] = fileName
//    return state
//}
//
//fun <T : Any> MutableState<T>.edit(block: T.() -> Unit) {
//    this.value.block()
//    val fileName = DriftRegistry.sourceMap[this] ?: return
//    DriftRegistry.getDb(fileName).saveOne(this.value::class.simpleName ?: "Data", this.value)
//    this.value = this.value
//}
//
//fun <T : Any> MutableState<T>.remove() {
//    val fileName = DriftRegistry.sourceMap[this] ?: return
//    DriftRegistry.getDb(fileName).nukeTable(this.value::class.simpleName ?: "Data")
//}
//
//// =============================================================================================
//// API 2: COLLECTION MODE
//// =============================================================================================
//
//@Composable
//fun <T : Any> DriftStore(fileName: String, kClass: KClass<T>): DriftListController<T> {
//    val context = LocalContext.current
//    if (DriftRegistry.context == null) DriftRegistry.context = context.applicationContext
//
//    val controller = remember { DriftListController(fileName, kClass.java) }
//
//    LaunchedEffect(fileName) {
//        val db = DriftRegistry.getDb(fileName)
//        db.ensureTableFromClass(kClass.java, isList = true)
//        controller.refresh()
//    }
//    return controller
//}
//
//class DriftListController<T : Any>(val fileName: String, val clazz: Class<T>) {
//    private val _items = mutableStateOf<List<T>>(emptyList(), policy = neverEqualPolicy())
//    val items: List<T> get() = _items.value
//
//    fun refresh() {
//        val db = DriftRegistry.getDb(fileName)
//        _items.value = db.readList(clazz.simpleName ?: "Data", clazz)
//    }
//
//    fun add(item: T) {
//        val db = DriftRegistry.getDb(fileName)
//        db.insertListItem(clazz.simpleName ?: "Data", item)
//        refresh()
//    }
//
//    fun remove(item: T) {
//        val id = DriftRegistry.idMap[item] ?: return
//        val db = DriftRegistry.getDb(fileName)
//        db.deleteListItem(clazz.simpleName ?: "Data", id)
//        refresh()
//    }
//
//    fun edit(item: T, block: T.() -> Unit) {
//        val id = DriftRegistry.idMap[item] ?: return
//        item.block()
//        val db = DriftRegistry.getDb(fileName)
//        db.updateListItem(clazz.simpleName ?: "Data", item, id)
//        refresh()
//    }
//
//    fun query(where: String? = null, orderBy: String? = null): List<T> {
//        val db = DriftRegistry.getDb(fileName)
//        return db.readList(clazz.simpleName ?: "Data", clazz, where, orderBy)
//    }
//
//    // --- NEW: TYPE-SAFE SORTING ---
//
//    // Usage: users.sort(User::age)
//    fun sort(by: KProperty1<T, *>, order: Order = Order.Ascending): List<T> {
//        val direction = if (order == Order.Ascending) "ASC" else "DESC"
//        return query(orderBy = "${by.name} $direction")
//    }
//
//    // Usage: users.sort(Sort(User::age), Sort(User::name))
//    fun sort(vararg rules: SortRule): List<T> {
//        val orderByClause = rules.joinToString(", ") {
//            val dir = if (it.order == Order.Ascending) "ASC" else "DESC"
//            "${it.property} $dir"
//        }
//        return query(orderBy = orderByClause)
//    }
//}
//
//// =============================================================================================
//// THE UNIFIED SQLITE BACKEND
//// =============================================================================================
//
//class DriftDatabaseHelper(context: Context, name: String) :
//    SQLiteOpenHelper(context, "$name.sqlite", null, 1) {
//
//    override fun onCreate(db: SQLiteDatabase) {}
//    override fun onUpgrade(db: SQLiteDatabase, o: Int, n: Int) {}
//
//    fun ensureTable(obj: Any, isList: Boolean) {
//        createTableSql(obj::class.java, obj::class.simpleName ?: "Data", isList)
//    }
//
//    fun ensureTableFromClass(clazz: Class<*>, isList: Boolean) {
//        createTableSql(clazz, clazz.simpleName ?: "Data", isList)
//    }
//
//    private fun createTableSql(clazz: Class<*>, tableName: String, isList: Boolean) {
//        val idType = if (isList) "INTEGER PRIMARY KEY AUTOINCREMENT" else "INTEGER PRIMARY KEY DEFAULT 0"
//        val sb = StringBuilder("CREATE TABLE IF NOT EXISTS $tableName (id $idType")
//
//        clazz.kotlin.declaredMemberProperties.forEach { prop ->
//            val type = when(prop.returnType.classifier) {
//                String::class -> "TEXT"
//                Int::class -> "INTEGER"
//                Boolean::class -> "INTEGER"
//                Float::class -> "REAL"
//                Double::class -> "REAL"
//                else -> "TEXT"
//            }
//            sb.append(", ${prop.name} $type")
//        }
//        sb.append(")")
//        writableDatabase.execSQL(sb.toString())
//    }
//
//    fun saveOne(table: String, obj: Any) {
//        val cv = objectToContentValues(obj)
//        cv.put("id", 0)
//        writableDatabase.insertWithOnConflict(table, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
//    }
//
//    fun <T : Any> readOne(table: String, clazz: Class<T>): T? {
//        val list = readList(table, clazz, "id = 0", null)
//        return list.firstOrNull()
//    }
//
//    fun insertListItem(table: String, obj: Any) {
//        val cv = objectToContentValues(obj)
//        cv.remove("id")
//        writableDatabase.insert(table, null, cv)
//    }
//
//    fun updateListItem(table: String, obj: Any, id: Long) {
//        val cv = objectToContentValues(obj)
//        writableDatabase.update(table, cv, "id = ?", arrayOf(id.toString()))
//    }
//
//    fun deleteListItem(table: String, id: Long) {
//        writableDatabase.delete(table, "id = ?", arrayOf(id.toString()))
//    }
//
//    fun <T : Any> readList(table: String, clazz: Class<T>, selection: String? = null, orderBy: String? = null): List<T> {
//        val db = readableDatabase
//        val check = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='$table'", null)
//        if (!check.moveToFirst()) { check.close(); return emptyList() }
//        check.close()
//
//        val cursor = db.query(table, null, selection, null, null, null, orderBy)
//        val results = mutableListOf<T>()
//
//        val ctor = clazz.kotlin.primaryConstructor ?: clazz.kotlin.constructors.first()
//
//        while (cursor.moveToNext()) {
//            val params = ctor.parameters.map { param ->
//                val idx = cursor.getColumnIndex(param.name)
//                if (idx == -1) return@map null
//                if (cursor.isNull(idx)) null else {
//                    when(param.type.classifier) {
//                        String::class -> cursor.getString(idx)
//                        Int::class -> cursor.getInt(idx)
//                        Boolean::class -> cursor.getInt(idx) == 1
//                        Float::class -> cursor.getFloat(idx)
//                        Double::class -> cursor.getDouble(idx)
//                        else -> ""
//                    }
//                }
//            }
//            val instance = ctor.call(*params.toTypedArray())
//            val idIdx = cursor.getColumnIndex("id")
//            if (idIdx != -1) {
//                DriftRegistry.idMap[instance] = cursor.getLong(idIdx)
//            }
//            results.add(instance)
//        }
//        cursor.close()
//        return results
//    }
//
//    fun nukeTable(table: String) {
//        writableDatabase.execSQL("DELETE FROM $table")
//    }
//
//    private fun objectToContentValues(obj: Any): ContentValues {
//        val cv = ContentValues()
//        obj::class.declaredMemberProperties.forEach { prop ->
//            prop.isAccessible = true
//            val value = prop.getter.call(obj)
//            when(value) {
//                is String -> cv.put(prop.name, value)
//                is Int -> cv.put(prop.name, value)
//                is Boolean -> cv.put(prop.name, if(value) 1 else 0)
//                is Float -> cv.put(prop.name, value)
//                is Double -> cv.put(prop.name, value)
//            }
//        }
//        return cv
//    }
//}

//Version3:
package com.example.driftui

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import java.util.WeakHashMap
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

// =============================================================================================
// SORTING TYPES
// =============================================================================================

enum class Order { Ascending, Descending }
data class SortRule(val property: String, val order: Order)

// Syntax helper: Sort(User::age)
fun <T> Sort(prop: KProperty1<T, *>, order: Order = Order.Ascending): SortRule {
    return SortRule(prop.name, order)
}

// =============================================================================================
// THE REGISTRY
// =============================================================================================

object DriftRegistry {
    // Maps Object Instance -> Filename
    val sourceMap = WeakHashMap<Any, String>()

    // Maps Object Instance -> Database ID (Hidden)
    val idMap = WeakHashMap<Any, Long>()

    val dbCache = mutableMapOf<String, DriftDatabaseHelper>()
    var context: Context? = null

    fun getDb(name: String): DriftDatabaseHelper {
        val ctx = context ?: throw IllegalStateException("DriftContext not initialized! Call DriftStore inside a Composable first.")
        return dbCache.getOrPut(name) { DriftDatabaseHelper(ctx, name) }
    }
}

// =============================================================================================
// API 1: SINGLETON MODE (MutableState)
// =============================================================================================

@Composable
inline fun <reified T : Any> DriftStore(fileName: String, default: T): MutableState<T> {
    val context = LocalContext.current
    if (DriftRegistry.context == null) DriftRegistry.context = context.applicationContext

    val state = remember { mutableStateOf(default, policy = neverEqualPolicy()) }

    LaunchedEffect(fileName) {
        val db = DriftRegistry.getDb(fileName)
        db.ensureTable(default, isList = false)
        val persisted = db.readOne(T::class.simpleName ?: "Data", T::class.java)
        if (persisted != null) state.value = persisted
        else db.saveOne(T::class.simpleName ?: "Data", default)
    }
    DriftRegistry.sourceMap[state] = fileName
    return state
}

// Edit extension for Singleton
fun <T : Any> MutableState<T>.edit(block: T.() -> Unit) {
    this.value.block()
    val fileName = DriftRegistry.sourceMap[this] ?: return
    DriftRegistry.getDb(fileName).saveOne(this.value::class.simpleName ?: "Data", this.value)
    this.value = this.value
}

// Remove extension for Singleton
fun <T : Any> MutableState<T>.remove() {
    val fileName = DriftRegistry.sourceMap[this] ?: return
    DriftRegistry.getDb(fileName).nukeTable(this.value::class.simpleName ?: "Data")
}

// =============================================================================================
// API 2: COLLECTION MODE (Controller)
// =============================================================================================

@Composable
fun <T : Any> DriftStore(fileName: String, kClass: KClass<T>): DriftListController<T> {
    val context = LocalContext.current
    if (DriftRegistry.context == null) DriftRegistry.context = context.applicationContext

    val controller = remember { DriftListController(fileName, kClass.java) }

    LaunchedEffect(fileName) {
        val db = DriftRegistry.getDb(fileName)
        db.ensureTableFromClass(kClass.java, isList = true)
        controller.refresh()
    }
    return controller
}

class DriftListController<T : Any>(val fileName: String, val clazz: Class<T>) {
    private val _items = mutableStateOf<List<T>>(emptyList(), policy = neverEqualPolicy())
    val items: List<T> get() = _items.value

    fun refresh() {
        val db = DriftRegistry.getDb(fileName)
        _items.value = db.readList(clazz.simpleName ?: "Data", clazz)
    }

    // --- ADD ---
    fun add(item: T) {
        val db = DriftRegistry.getDb(fileName)
        db.insertListItem(clazz.simpleName ?: "Data", item)
        refresh()
    }

    // --- EDIT ---
    fun edit(item: T, block: T.() -> Unit) {
        val id = DriftRegistry.idMap[item] ?: return
        item.block()
        val db = DriftRegistry.getDb(fileName)
        db.updateListItem(clazz.simpleName ?: "Data", item, id)
        refresh()
    }

    // --- REMOVE 1: Specific Object ---
    fun remove(item: T) {
        val id = DriftRegistry.idMap[item] ?: return
        val db = DriftRegistry.getDb(fileName)
        db.deleteListItem(clazz.simpleName ?: "Data", id)
        refresh()
    }

    // --- REMOVE 2: Wipe Table ---
    fun removeAll() {
        val db = DriftRegistry.getDb(fileName)
        db.nukeTable(clazz.simpleName ?: "Data")
        refresh()
    }

    // --- REMOVE 3: By Parameter (e.g. name="John") ---
    fun <V> removeBy(prop: KProperty1<T, V>, value: V) {
        val db = DriftRegistry.getDb(fileName)
        // Convert Boolean to Int (0/1) for SQLite, others to String
        val sqlValue = if (value is Boolean) (if (value) "1" else "0") else value.toString()

        db.deleteWhere(
            table = clazz.simpleName ?: "Data",
            whereClause = "${prop.name} = ?",
            args = arrayOf(sqlValue)
        )
        refresh()
    }

    // --- REMOVE 4: By ID (Manual) ---
    fun removeById(id: Long) {
        val db = DriftRegistry.getDb(fileName)
        db.deleteListItem(clazz.simpleName ?: "Data", id)
        refresh()
    }

    // --- QUERIES & SORTING ---
    fun query(where: String? = null, orderBy: String? = null): List<T> {
        // [CRITICAL FIX]
        // We read the state here. This registers a dependency with Compose.
        // When 'add/remove' calls refresh(), '_items' changes, forcing this query to re-run.
        val trigger = _items.value

        val db = DriftRegistry.getDb(fileName)
        return db.readList(clazz.simpleName ?: "Data", clazz, where, orderBy)
    }

    fun sort(by: KProperty1<T, *>, order: Order = Order.Ascending): List<T> {
        val direction = if (order == Order.Ascending) "ASC" else "DESC"
        return query(orderBy = "${by.name} $direction")
    }

    fun sort(vararg rules: SortRule): List<T> {
        val orderByClause = rules.joinToString(", ") {
            val dir = if (it.order == Order.Ascending) "ASC" else "DESC"
            "${it.property} $dir"
        }
        return query(orderBy = orderByClause)
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
        createTableSql(obj::class.java, obj::class.simpleName ?: "Data", isList)
    }

    fun ensureTableFromClass(clazz: Class<*>, isList: Boolean) {
        createTableSql(clazz, clazz.simpleName ?: "Data", isList)
    }

    private fun createTableSql(clazz: Class<*>, tableName: String, isList: Boolean) {
        val idType = if (isList) "INTEGER PRIMARY KEY AUTOINCREMENT" else "INTEGER PRIMARY KEY DEFAULT 0"
        val sb = StringBuilder("CREATE TABLE IF NOT EXISTS $tableName (id $idType")

        clazz.kotlin.declaredMemberProperties.forEach { prop ->
            val type = when(prop.returnType.classifier) {
                String::class -> "TEXT"
                Int::class -> "INTEGER"
                Boolean::class -> "INTEGER"
                Float::class -> "REAL"
                Double::class -> "REAL"
                else -> "TEXT"
            }
            sb.append(", ${prop.name} $type")
        }
        sb.append(")")
        writableDatabase.execSQL(sb.toString())
    }

    // --- SINGLETON OPS ---
    fun saveOne(table: String, obj: Any) {
        val cv = objectToContentValues(obj)
        cv.put("id", 0)
        writableDatabase.insertWithOnConflict(table, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun <T : Any> readOne(table: String, clazz: Class<T>): T? {
        val list = readList(table, clazz, "id = 0", null)
        return list.firstOrNull()
    }

    // --- LIST OPS ---
    fun insertListItem(table: String, obj: Any) {
        val cv = objectToContentValues(obj)
        cv.remove("id")
        writableDatabase.insert(table, null, cv)
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

    fun <T : Any> readList(table: String, clazz: Class<T>, selection: String? = null, orderBy: String? = null): List<T> {
        val db = readableDatabase
        // Safety check if table exists
        val check = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='$table'", null)
        if (!check.moveToFirst()) { check.close(); return emptyList() }
        check.close()

        val cursor = db.query(table, null, selection, null, null, null, orderBy)
        val results = mutableListOf<T>()

        val ctor = clazz.kotlin.primaryConstructor ?: clazz.kotlin.constructors.first()

        while (cursor.moveToNext()) {
            val params = ctor.parameters.map { param ->
                val idx = cursor.getColumnIndex(param.name)
                if (idx == -1) return@map null
                if (cursor.isNull(idx)) null else {
                    when(param.type.classifier) {
                        String::class -> cursor.getString(idx)
                        Int::class -> cursor.getInt(idx)
                        Boolean::class -> cursor.getInt(idx) == 1
                        Float::class -> cursor.getFloat(idx)
                        Double::class -> cursor.getDouble(idx)
                        else -> ""
                    }
                }
            }
            val instance = ctor.call(*params.toTypedArray())

            // Capture the ID for the .driftId extension
            val idIdx = cursor.getColumnIndex("id")
            if (idIdx != -1) {
                DriftRegistry.idMap[instance] = cursor.getLong(idIdx)
            }
            results.add(instance)
        }
        cursor.close()
        return results
    }

    fun nukeTable(table: String) {
        writableDatabase.execSQL("DELETE FROM $table")
    }

    private fun objectToContentValues(obj: Any): ContentValues {
        val cv = ContentValues()
        obj::class.declaredMemberProperties.forEach { prop ->
            prop.isAccessible = true
            val value = prop.getter.call(obj)
            when(value) {
                is String -> cv.put(prop.name, value)
                is Int -> cv.put(prop.name, value)
                is Boolean -> cv.put(prop.name, if(value) 1 else 0)
                is Float -> cv.put(prop.name, value)
                is Double -> cv.put(prop.name, value)
            }
        }
        return cv
    }
}

// =============================================================================================
// SYNTAX SUGAR
// =============================================================================================

/**
 * Access the hidden database ID of any object managed by Drift.
 * Usage: user.driftId
 * Returns -1 if the object is not in the database.
 */
val Any.driftId: Long
    get() = DriftRegistry.idMap[this] ?: -1L