package com.example.driftui.core

import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.coroutines.delay

val Int.milliseconds: Long get() = this.toLong()
val Int.seconds: Long get() = this * 1000L
val Int.minutes: Long get() = this * 60_000L
val Int.hours: Long get() = this * 3_600_000L
val Int.days: Long get() = this * 86_400_000L

@Composable
fun onActiveSession(
    delay: Long = 5.minutes,
    alwaysLaunchAtLogin: Boolean = true,
    key: Any? = Unit,
    action: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentAction by rememberUpdatedState(action)

    // 1. DATA LOADED TRIGGER (Restart when key changes)
    // This handles the "I just logged in" or "App just opened" logic.
    // It STOPS running if alwaysLaunchAtLogin is false.
    LaunchedEffect(key) {
        if (alwaysLaunchAtLogin && lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            Log.d("DriftHeartbeat", "⚡ Trigger: Data Loaded / Key Changed")
            currentAction()
        }
    }

    // 2. APP RESUME TRIGGER
    // This handles minimizing and reopening the app.
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && alwaysLaunchAtLogin) {
                Log.d("DriftHeartbeat", "⚡ Trigger: App Resumed")
                currentAction()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // 3. PERIODIC TIMER (IMMORTAL)
    // CRITICAL FIX: The key is 'Unit'. This means the timer NEVER resets
    // even if the user updates or the database refreshes.
    LaunchedEffect(Unit) {
        Log.d("DriftHeartbeat", "⏰ Timer Started: Loop initialized")
        while (true) {
            // Wait first
            delay(delay)

            // Check if app is alive
            if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                Log.d("DriftHeartbeat", "⏰ Trigger: Timer Fired")
                currentAction()
            } else {
                Log.d("DriftHeartbeat", "💤 Timer Skipped: App Backgrounded")
            }
        }
    }
}