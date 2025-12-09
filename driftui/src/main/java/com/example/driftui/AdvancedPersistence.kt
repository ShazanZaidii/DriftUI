package com.example.driftui

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import java.util.WeakHashMap
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

// =============================================================================================
// THE REGISTRY
// =============================================================================================

object DriftRegistry {
    // Maps the State Instance -> The Database Filename
    val stateMap = WeakHashMap<MutableState<*>, String>()
    val dbCache = mutableMapOf<String, DriftDatabaseHelper>()
    var context: Context? = null

    fun getDb(name: String): DriftDatabaseHelper {
        val ctx = context ?: throw IllegalStateException("DriftContext not initialized!")
        return dbCache.getOrPut(name) { DriftDatabaseHelper(ctx, name) }
    }
}

// =============================================================================================
// THE API
// =============================================================================================

/**
 * Returns a REACTIVE MutableState.
 * - Loading: Checks disk first.
 * - Reactivity: Uses neverEqualPolicy() so updates to 'var' fields trigger UI refresh.
 */
@Composable
inline fun <reified T : Any> DriftStore(fileName: String, default: T): MutableState<T> {
    val context = LocalContext.current
    if (DriftRegistry.context == null) DriftRegistry.context = context.applicationContext

    // 1. Create the State with "Never Equal" policy.
    // This ensures that even if we modify the object in place, Compose redraws.
    val state = remember {
        mutableStateOf(default, policy = neverEqualPolicy())
    }

    // 2. Load from Disk (Side Effect to run once)
    LaunchedEffect(fileName) {
        val dbHelper = DriftRegistry.getDb(fileName)
        dbHelper.ensureTable(default)

        val persisted = dbHelper.readOne(T::class.simpleName ?: "Data", T::class.java)
        if (persisted != null) {
            state.value = persisted
        } else {
            // First time? Save the default to disk immediately.
            dbHelper.save(T::class.simpleName ?: "Data", default)
        }
    }

    // 3. Register for .edit() access
    DriftRegistry.stateMap[state] = fileName

    return state
}

/**
 * The Reactive Edit Block.
 * 1. Updates RAM
 * 2. Saves to Disk
 * 3. Forces UI Refresh
 */
fun <T : Any> MutableState<T>.edit(block: T.() -> Unit) {
    // A. Run the user's change (e.g., name = "New")
    this.value.block()

    // B. Find the file
    val fileName = DriftRegistry.stateMap[this] ?: return
    val dbHelper = DriftRegistry.getDb(fileName)

    // C. Save to Disk
    dbHelper.save(this.value::class.simpleName ?: "Data", this.value)

    // D. FORCE RECOMPOSE
    // Because we used neverEqualPolicy(), setting the value to itself
    // tells Compose: "Something inside changed, redraw everything!"
    this.value = this.value
}

/**
 * Removes data from disk.
 */
fun <T : Any> MutableState<T>.remove() {
    val fileName = DriftRegistry.stateMap[this] ?: return
    val dbHelper = DriftRegistry.getDb(fileName)
    dbHelper.nukeTable(this.value::class.simpleName ?: "Data")
}

// =============================================================================================
// THE SQLITE BACKEND
// =============================================================================================

class DriftDatabaseHelper(context: Context, name: String) :
    SQLiteOpenHelper(context, "$name.sqlite", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {}
    override fun onUpgrade(db: SQLiteDatabase, o: Int, n: Int) {}

    fun ensureTable(obj: Any) {
        val tableName = obj::class.simpleName ?: "Data"
        val sb = StringBuilder("CREATE TABLE IF NOT EXISTS $tableName (id INTEGER PRIMARY KEY DEFAULT 0")

        obj::class.declaredMemberProperties.forEach { prop ->
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

    fun save(table: String, obj: Any) {
        val cv = ContentValues()
        cv.put("id", 0) // Singleton Row Strategy

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

        // Critical: Insert with REPLACE strategy
        writableDatabase.insertWithOnConflict(table, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun <T : Any> readOne(table: String, clazz: Class<T>): T? {
        val db = readableDatabase

        // Check if table exists to avoid crash on fresh install
        val check = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='$table'", null)
        if (!check.moveToFirst()) {
            check.close()
            return null
        }
        check.close()

        val cursor = db.query(table, null, "id = 0", null, null, null, null)
        if (!cursor.moveToFirst()) {
            cursor.close()
            return null
        }

        val ctor = clazz.kotlin.primaryConstructor ?: clazz.kotlin.constructors.first()

        val params = ctor.parameters.map { param ->
            val idx = cursor.getColumnIndex(param.name)
            // If column is missing (schema change), return generic default to avoid crash
            if (idx == -1) return@map null

            // FIX: Check for NULL before reading primitive types
            if (cursor.isNull(idx)) {
                null
            } else {
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

        cursor.close()
        return ctor.call(*params.toTypedArray())
    }

    fun nukeTable(table: String) {
        writableDatabase.execSQL("DELETE FROM $table")
    }
}