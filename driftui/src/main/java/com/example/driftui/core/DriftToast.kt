package com.example.driftui.core

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import android.view.HapticFeedbackConstants
import kotlinx.coroutines.*

// =============================================================================================
// ENUMS & NODES
// =============================================================================================

enum class ToastType {
    Default, Success, Error, Warning
}

private data class ToastDurationNode(val seconds: Double) : Modifier.Element
private data class ToastOnEndNode(val action: () -> Unit) : Modifier.Element
private data class ToastColorNode(val color: Color) : Modifier.Element
private data class ToastTextColorNode(val color: Color) : Modifier.Element
private data class ToastTypeNode(val type: ToastType) : Modifier.Element

// =============================================================================================
// PUBLIC DSL MODIFIERS
// =============================================================================================

fun Modifier.duration(seconds: Double): Modifier = this.then(ToastDurationNode(seconds))
fun Modifier.onEnd(action: () -> Unit): Modifier = this.then(ToastOnEndNode(action))
fun Modifier.toastColor(color: Color): Modifier = this.then(ToastColorNode(color))
fun Modifier.textColor(color: Color): Modifier = this.then(ToastTextColorNode(color))
fun Modifier.type(type: ToastType): Modifier = this.then(ToastTypeNode(type))

// =============================================================================================
// TOAST MANAGER
// =============================================================================================

object DriftToastManager {
    var activeToast by mutableStateOf<(@Composable () -> Unit)?>(null)
    var activeType by mutableStateOf(ToastType.Default)
    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    fun show(
        duration: Double,
        type: ToastType = ToastType.Default,
        onEnd: (() -> Unit)? = null,
        content: @Composable () -> Unit
    ) {
        job?.cancel()
        activeType = type
        activeToast = content
        job = scope.launch {
            delay((duration * 1000).toLong())
            activeToast = null
            onEnd?.invoke()
        }
    }
}

// =============================================================================================
// INTERNAL CONTAINER
// =============================================================================================

@Composable
private fun ToastContainer(modifier: Modifier, content: @Composable () -> Unit) {
    var type = ToastType.Default
    var customColor: Color? = null

    modifier.foldIn(Unit) { _, element ->
        if (element is ToastTypeNode) type = element.type
        if (element is ToastColorNode) customColor = element.color
        Unit
    }

    val baseColor = customColor ?: when (type) {
        ToastType.Success -> Color(0xFF2E7D32).copy(alpha = 0.95f)
        ToastType.Error -> Color(0xFFD32F2F).copy(alpha = 0.95f)
        ToastType.Warning -> Color(0xFFFFA000).copy(alpha = 0.95f)
        ToastType.Default -> Color(0xFF303030).copy(alpha = 0.95f)
    }

    Box(
        modifier = Modifier
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(50), clip = false)
            .background(baseColor, RoundedCornerShape(50))
            // We only apply the modifier to the container for things like layout/padding
            .then(modifier)
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        content()
    }
}

// =============================================================================================
// THE END API
// =============================================================================================

fun Toast(message: String, modifier: Modifier = Modifier) {
    var duration = 2.0
    var onEnd: (() -> Unit)? = null
    var textCol = Color.White
    var type = ToastType.Default

    // Scrutinize the modifier for text properties
    modifier.foldIn(Unit) { _, element ->
        if (element is ToastDurationNode) duration = element.seconds
        if (element is ToastOnEndNode) onEnd = element.action
        if (element is ToastTextColorNode) textCol = element.color
        if (element is ToastTypeNode) type = element.type
        Unit
    }

    DriftToastManager.show(duration, type, onEnd) {
        ToastContainer(modifier) {
            // DIRECT FIX: Use the standard Material3 Text color parameter
            // to ensure the DSL custom modifiers aren't being ignored.
            Text(
                text = message,
                color = textCol,
                style = TextStyle(fontSize = 14.sp)
            )
        }
    }
}

fun Toast(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    var duration = 2.0
    var onEnd: (() -> Unit)? = null
    var type = ToastType.Default

    modifier.foldIn(Unit) { _, element ->
        if (element is ToastDurationNode) duration = element.seconds
        if (element is ToastOnEndNode) onEnd = element.action
        if (element is ToastTypeNode) type = element.type
        Unit
    }

    DriftToastManager.show(duration, type, onEnd) {
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
    val type = DriftToastManager.activeType
    val view = LocalView.current

    // --- SHAKE ANIMATION LOGIC ---
    val shakeOffset = remember { androidx.compose.animation.core.Animatable(0f) }

    LaunchedEffect(toast) {
        if (toast != null) {
            // TRIGGER STRONG HAPTICS
            when(type) {
                ToastType.Error -> {
                    // VIRTUAL_KEY is much stronger than REJECT
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING)

                    // Visual Shake for Error
                    repeat(4) {
                        shakeOffset.animateTo(20f, tween(50))
                        shakeOffset.animateTo(-20f, tween(50))
                    }
                    shakeOffset.animateTo(0f, tween(50))
                }
                ToastType.Success -> {
                    // LONG_PRESS is a distinct, heavy pulse
                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING)
                }
                else -> {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                }
            }
        }
    }

    Box(
        Modifier.fillMaxSize().padding(bottom = 64.dp).zIndex(100f),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = toast != null,
            enter = fadeIn(tween(300)) + scaleIn(initialScale = 0.8f, animationSpec = tween(300)),
            exit = fadeOut(tween(200)) + scaleOut(targetScale = 0.8f, animationSpec = tween(200))
        ) {
            Box(
                Modifier
                    .padding(32.dp)
                    // Apply the visual shake offset
                    .offset(x = shakeOffset.value.dp)
            ) {
                toast?.invoke()
            }
        }
    }
}