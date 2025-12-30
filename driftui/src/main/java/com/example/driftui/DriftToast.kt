//package com.example.driftui
//
//import androidx.compose.animation.*
//import androidx.compose.animation.core.tween
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.shadow
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.zIndex
//import kotlinx.coroutines.*
//
//// =============================================================================================
//// CUSTOM MODIFIERS
//// =============================================================================================
//
//private data class ToastDurationNode(val seconds: Double) : Modifier.Element
//private data class ToastOnEndNode(val action: () -> Unit) : Modifier.Element
//
//fun Modifier.duration(seconds: Double): Modifier = this.then(ToastDurationNode(seconds))
//fun Modifier.onEnd(action: () -> Unit): Modifier = this.then(ToastOnEndNode(action))
//
//// =============================================================================================
//// TOAST MANAGER
//// =============================================================================================
//
//object DriftToastManager {
//    var activeToast by mutableStateOf<(@Composable () -> Unit)?>(null)
//    private var job: Job? = null
//    private val scope = CoroutineScope(Dispatchers.Main)
//
//    fun show(duration: Double, onEnd: (() -> Unit)? = null, content: @Composable () -> Unit) {
//        job?.cancel()
//        activeToast = content
//        job = scope.launch {
//            delay((duration * 1000).toLong())
//            activeToast = null
//            onEnd?.invoke()
//        }
//    }
//}
//
//// =============================================================================================
//// SHARED CONTAINER (The Fix)
//// =============================================================================================
//
//@Composable
//private fun ToastContainer(modifier: Modifier, content: @Composable () -> Unit) {
//    Box(
//        modifier = Modifier
//            // 1. Base Shadow (Clip=false avoids artifacts)
//            .shadow(elevation = 6.dp, shape = RoundedCornerShape(50), clip = false)
//
//            // 2. Default Background (Dark Grey)
//            // We apply this first. If you add .background(Color.Red) in 'modifier',
//            // it will draw ON TOP of this, effectively replacing it.
//            .background(Color(0xFF303030).copy(alpha = 0.95f), RoundedCornerShape(50))
//
//            // 3. User Modifiers (This is where your Color.Red is applied!)
//            .then(modifier)
//
//            // 4. Default Padding (Inside the shape)
//            .padding(horizontal = 24.dp, vertical = 12.dp)
//    ) {
//        content()
//    }
//}
//
//// =============================================================================================
//// THE API
//// =============================================================================================
//
//fun Toast(
//    message: String,
//    modifier: Modifier = Modifier
//) {
//    var duration = 2.0
//    var onEnd: (() -> Unit)? = null
//
//    modifier.foldIn(Unit) { _, element ->
//        if (element is ToastDurationNode) duration = element.seconds
//        if (element is ToastOnEndNode) onEnd = element.action
//        Unit
//    }
//
//    DriftToastManager.show(duration, onEnd) {
//        ToastContainer(modifier) {
//            Text(message, Modifier.foregroundStyle(Color.white).font(system(14, regular)))
//        }
//    }
//}
//
//fun Toast(
//    modifier: Modifier = Modifier,
//    content: @Composable () -> Unit
//) {
//    var duration = 2.0
//    var onEnd: (() -> Unit)? = null
//
//    modifier.foldIn(Unit) { _, element ->
//        if (element is ToastDurationNode) duration = element.seconds
//        if (element is ToastOnEndNode) onEnd = element.action
//        Unit
//    }
//
//    DriftToastManager.show(duration, onEnd) {
//        // FIXED: Now wrapping custom content in the container too
//        ToastContainer(modifier) {
//            content()
//        }
//    }
//}
//
//// =============================================================================================
//// THE HOST
//// =============================================================================================
//
//@Composable
//fun DriftToastHost() {
//    val toast = DriftToastManager.activeToast
//
//    Box(
//        Modifier
//            .fillMaxSize()
//            .padding(bottom = 60.dp)
//            .zIndex(100f),
//        contentAlignment = Alignment.BottomCenter
//    ) {
//        AnimatedVisibility(
//            visible = toast != null,
//            // Expand In + Shrink Out
//            enter = fadeIn(tween(300)) + scaleIn(initialScale = 0.8f, animationSpec = tween(300)),
//            exit = fadeOut(tween(300)) + scaleOut(targetScale = 0.8f, animationSpec = tween(300))
//        ) {
//            // Shadow Buffer
//            Box(Modifier.padding(30.dp)) {
//                toast?.invoke()
//            }
//        }
//    }
//}


package com.example.driftui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.*

// =============================================================================================
// CUSTOM MODIFIERS (Configuration)
// =============================================================================================

private data class ToastDurationNode(val seconds: Double) : Modifier.Element
private data class ToastOnEndNode(val action: () -> Unit) : Modifier.Element
private data class ToastColorNode(val color: Color) : Modifier.Element

/** Sets the duration in seconds (Default: 2.0) */
fun Modifier.duration(seconds: Double): Modifier = this.then(ToastDurationNode(seconds))

/** Runs a block of code when the toast finishes */
fun Modifier.onEnd(action: () -> Unit): Modifier = this.then(ToastOnEndNode(action))

/** Sets the BASE color of the toast (Replaces the default Dark Grey) */
fun Modifier.toastColor(color: Color): Modifier = this.then(ToastColorNode(color))

// =============================================================================================
// TOAST MANAGER
// =============================================================================================

object DriftToastManager {
    var activeToast by mutableStateOf<(@Composable () -> Unit)?>(null)
    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    fun show(duration: Double, onEnd: (() -> Unit)? = null, content: @Composable () -> Unit) {
        job?.cancel()
        activeToast = content
        job = scope.launch {
            delay((duration * 1000).toLong())
            activeToast = null
            onEnd?.invoke()
        }
    }
}

// =============================================================================================
// SHARED CONTAINER
// =============================================================================================

@Composable
private fun ToastContainer(modifier: Modifier, content: @Composable () -> Unit) {
    // 1. Set Defaults
    var baseColor = Color(0xFF303030).copy(alpha = 0.95f) // Default Dark Grey

    // 2. Scan Modifiers for Custom Color override
    modifier.foldIn(Unit) { _, element ->
        if (element is ToastColorNode) baseColor = element.color
        Unit
    }

    Box(
        modifier = Modifier
            // Base Shadow
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(50), clip = false)

            // 3. APPLY BASE COLOR (This is now dynamic!)
            .background(baseColor, RoundedCornerShape(50))

            // 4. Apply other user modifiers (padding, offsets, opacity etc.)
            // Note: If you use .background() here, it will paint ON TOP.
            // Use .toastColor() to replace the layer above.
            .then(modifier)

            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        content()
    }
}

// =============================================================================================
// THE API
// =============================================================================================

fun Toast(message: String, modifier: Modifier = Modifier) {
    var duration = 2.0
    var onEnd: (() -> Unit)? = null

    modifier.foldIn(Unit) { _, element ->
        if (element is ToastDurationNode) duration = element.seconds
        if (element is ToastOnEndNode) onEnd = element.action
        Unit
    }

    DriftToastManager.show(duration, onEnd) {
        ToastContainer(modifier) {
            Text(message, Modifier.foregroundStyle(Color.white).font(system(14, regular)))
        }
    }
}

fun Toast(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    var duration = 2.0
    var onEnd: (() -> Unit)? = null

    modifier.foldIn(Unit) { _, element ->
        if (element is ToastDurationNode) duration = element.seconds
        if (element is ToastOnEndNode) onEnd = element.action
        Unit
    }

    DriftToastManager.show(duration, onEnd) {
        ToastContainer(modifier) {
            content()
        }
    }
}

// =============================================================================================
// THE HOST
// =============================================================================================

@Composable
fun DriftToastHost() {
    val toast = DriftToastManager.activeToast

    Box(
        Modifier.fillMaxSize().padding(bottom = 60.dp).zIndex(100f),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = toast != null,
            enter = fadeIn(tween(300)) + scaleIn(initialScale = 0.8f, animationSpec = tween(300)),
            exit = fadeOut(tween(300)) + scaleOut(targetScale = 0.8f, animationSpec = tween(300))
        ) {
            Box(Modifier.padding(30.dp)) { toast?.invoke() }
        }
    }
}