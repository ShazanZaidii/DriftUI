package com.example.driftui.core

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

// storage engine singleton
object DriftStorage {
    private const val PREF_NAME = "DriftUI_Storage"
    var prefs: SharedPreferences? = null

    fun initialize(context: Context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        }
    }
}

// composable version for ui views
@Composable
inline fun <reified T> Storage(key: String, defaultValue: T): MutableState<T> {
    val context = LocalContext.current
    DriftStorage.initialize(context)
    val prefs = DriftStorage.prefs ?: return remember { mutableStateOf(defaultValue) }

    // read initial value
    val initialValue = readValue(prefs, key, defaultValue)

    // state holder
    val state = remember { mutableStateOf(initialValue) }

    // write back wrapper
    return remember(state) {
        createPersistedState(state, prefs, key)
    }
}

// standard version for viewmodels and classes
inline fun <reified T> Any.Storage(key: String, defaultValue: T): MutableState<T> {

    // ensure engine is initialized in main activity
    val prefs = DriftStorage.prefs
        ?: throw IllegalStateException("DriftStorage not initialized Call DriftStorage initialize in MainActivity")

    // read initial value
    val initialValue = readValue(prefs, key, defaultValue)

    // create state without remember
    val state = mutableStateOf(initialValue)

    // return wrapper
    return createPersistedState(state, prefs, key)
}

// internal shared logic helpers
inline fun <reified T> readValue(prefs: SharedPreferences, key: String, defaultValue: T): T {
    return when (defaultValue) {
        is String -> prefs.getString(key, defaultValue) as T
        is Int -> prefs.getInt(key, defaultValue) as T
        is Boolean -> prefs.getBoolean(key, defaultValue) as T
        is Float -> prefs.getFloat(key, defaultValue) as T
        is Long -> prefs.getLong(key, defaultValue) as T
        else -> throw IllegalArgumentException("Storage supports String Int Bool Float Long")
    }
}

fun <T> createPersistedState(
    state: MutableState<T>,
    prefs: SharedPreferences,
    key: String
): MutableState<T> {
    return object : MutableState<T> {
        override var value: T
            get() = state.value
            set(newValue) {
                state.value = newValue
                val editor = prefs.edit()
                when (newValue) {
                    is String -> editor.putString(key, newValue)
                    is Int -> editor.putInt(key, newValue)
                    is Boolean -> editor.putBoolean(key, newValue)
                    is Float -> editor.putFloat(key, newValue)
                    is Long -> editor.putLong(key, newValue)
                }
                editor.apply()
            }

        override fun component1() = value
        override fun component2(): (T) -> Unit = { value = it }
    }
}