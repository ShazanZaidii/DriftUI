package com.example.driftui.core

import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.coroutines.delay

// time extension helpers
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

    // trigger on data load or key change
    LaunchedEffect(key) {
        if (alwaysLaunchAtLogin && lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            Log.d("DriftHeartbeat", "Trigger: Data Loaded or Key Changed")
            currentAction()
        }
    }

    // trigger on app resume
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && alwaysLaunchAtLogin) {
                Log.d("DriftHeartbeat", "Trigger: App Resumed")
                currentAction()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // immortal periodic timer
    LaunchedEffect(Unit) {
        Log.d("DriftHeartbeat", "Timer Started: Loop initialized")
        while (true) {
            delay(delay)
            if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                Log.d("DriftHeartbeat", "Trigger: Timer Fired")
                currentAction()
            } else {
                Log.d("DriftHeartbeat", "Timer Skipped: App Backgrounded")
            }
        }
    }
}