package com.example.driftui.core

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
// 1. COMPOSABLE VERSION (For UI / Views)
// =============================================================================================
// Used when you call Storage(...) inside a @Composable function (e.g., inside metro())

@Composable
inline fun <reified T> Storage(key: String, defaultValue: T): MutableState<T> {
    val context = LocalContext.current
    DriftStorage.initialize(context)
    val prefs = DriftStorage.prefs ?: return remember { mutableStateOf(defaultValue) }

    // Read initial value
    val initialValue = readValue(prefs, key, defaultValue)

    // State Holder
    val state = remember { mutableStateOf(initialValue) }

    // Write-Back Wrapper
    return remember(state) {
        createPersistedState(state, prefs, key)
    }
}

// =============================================================================================
// 2. STANDARD VERSION (For ViewModels / Classes)
// =============================================================================================
// Used when you call Storage(...) inside a Class or ViewModel.
// FIX: We make this an extension on 'Any' to distinguish it from the Composable version.

inline fun <reified T> Any.Storage(key: String, defaultValue: T): MutableState<T> {
    // 1. Ensure Engine is ready (Must call initialize in MainActivity)
    val prefs = DriftStorage.prefs
        ?: throw IllegalStateException("DriftStorage not initialized! Call DriftStorage.initialize(context) in MainActivity onCreate.")

    // 2. Read Initial
    val initialValue = readValue(prefs, key, defaultValue)

    // 3. Create State (No 'remember' needed in ViewModel)
    val state = mutableStateOf(initialValue)

    // 4. Return wrapper
    return createPersistedState(state, prefs, key)
}


// =============================================================================================
// INTERNAL HELPERS (Shared Logic)
// =============================================================================================

inline fun <reified T> readValue(prefs: SharedPreferences, key: String, defaultValue: T): T {
    return when (defaultValue) {
        is String -> prefs.getString(key, defaultValue) as T
        is Int -> prefs.getInt(key, defaultValue) as T
        is Boolean -> prefs.getBoolean(key, defaultValue) as T
        is Float -> prefs.getFloat(key, defaultValue) as T
        is Long -> prefs.getLong(key, defaultValue) as T
        else -> throw IllegalArgumentException("Storage supports: String, Int, Bool, Float, Long")
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