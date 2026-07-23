package com.shazan.driftui.core

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

object DriftStorage {
    private const val PREF_NAME = "DriftUI_Storage"
    var prefs: SharedPreferences? = null

    fun initialize(context: Context) {
        if (prefs == null) {
            prefs = context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        }
    }
}

@Composable
inline fun <reified T> Storage(key: String, defaultValue: T): MutableState<T> {
    val context = LocalContext.current
    DriftStorage.initialize(context)
    val prefs = DriftStorage.prefs ?: return remember { mutableStateOf(defaultValue) }

    val state = remember { mutableStateOf(readValue(prefs, key, defaultValue)) }

    // Adds true SwiftUI reactivity: Automatically recomposes if updated from another screen or ViewModel
    DisposableEffect(key, prefs) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, changedKey ->
            if (key == changedKey) {
                state.value = readValue(sharedPreferences, key, defaultValue)
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        onDispose {
            prefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    return remember(state, prefs, key) {
        createPersistedState(state, prefs, key)
    }
}

inline fun <reified T> Any.Storage(key: String, defaultValue: T): MutableState<T> {
    val prefs = DriftStorage.prefs
        ?: throw IllegalStateException("DriftStorage not initialized. Call DriftStorage.initialize() in MainActivity.")

    val state = mutableStateOf(readValue(prefs, key, defaultValue))
    return createPersistedState(state, prefs, key)
}

inline fun <reified T> readValue(prefs: SharedPreferences, key: String, defaultValue: T): T {
    return when (defaultValue) {
        is String -> prefs.getString(key, defaultValue) as T
        is Int -> prefs.getInt(key, defaultValue) as T
        is Boolean -> prefs.getBoolean(key, defaultValue) as T
        is Float -> prefs.getFloat(key, defaultValue) as T
        is Long -> prefs.getLong(key, defaultValue) as T
        else -> throw IllegalArgumentException("Storage supports String, Int, Boolean, Float, Long")
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