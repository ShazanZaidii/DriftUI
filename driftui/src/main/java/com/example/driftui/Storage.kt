package com.example.driftui

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

// =============================================================================================
// THE ENGINE (Singleton)
// =============================================================================================

object DriftStorage {
    private const val PREF_NAME = "DriftUI_Storage"
    var prefs: SharedPreferences? = null

    fun initialize(context: Context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        }
    }
}

// =============================================================================================
// THE API (Capitalized for consistency with State)
// =============================================================================================

@Composable
inline fun <reified T> Storage(key: String, defaultValue: T): MutableState<T> {
    val context = LocalContext.current
    DriftStorage.initialize(context)

    val prefs = DriftStorage.prefs ?: return remember { mutableStateOf(defaultValue) }

    // 1. Read
    val initialValue: T = when (defaultValue) {
        is String -> prefs.getString(key, defaultValue) as T
        is Int -> prefs.getInt(key, defaultValue) as T
        is Boolean -> prefs.getBoolean(key, defaultValue) as T
        is Float -> prefs.getFloat(key, defaultValue) as T
        is Long -> prefs.getLong(key, defaultValue) as T
        else -> throw IllegalArgumentException("Storage supports: String, Int, Bool, Float, Long")
    }

    // 2. State Holder
    val state = remember { mutableStateOf(initialValue) }

    // 3. Write-Back Wrapper
    return remember(state) {
        object : MutableState<T> {
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
}