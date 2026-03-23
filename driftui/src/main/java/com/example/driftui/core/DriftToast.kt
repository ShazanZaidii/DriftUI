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
import android.view.HapticFeedbackConstants
import kotlinx.coroutines.*
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

// enums and nodes

enum class ToastType {
    Default, Success, Error, Warning
}

private data class ToastDurationNode(val seconds: Double) : Modifier.Element
private data class ToastOnEndNode(val action: () -> Unit) : Modifier.Element
private data class ToastColorNode(val color: Color) : Modifier.Element
private data class ToastTextColorNode(val color: Color) : Modifier.Element
private data class ToastTypeNode(val type: ToastType) : Modifier.Element

// public dsl modifiers

fun Modifier.duration(seconds: Double): Modifier = this.then(ToastDurationNode(seconds))
fun Modifier.onEnd(action: () -> Unit): Modifier = this.then(ToastOnEndNode(action))
fun Modifier.toastColor(color: Color): Modifier = this.then(ToastColorNode(color))
fun Modifier.textColor(color: Color): Modifier = this.then(ToastTextColorNode(color))
fun Modifier.type(type: ToastType): Modifier = this.then(ToastTypeNode(type))

// toast manager

object DriftToastManager {
    var activeToast by mutableStateOf<(@Composable () -> Unit)?>(null)
    var activeType by mutableStateOf(ToastType.Default)

    // tracks the identity of the currently visible toast
    var activeId by mutableStateOf<String?>(null)

    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    fun show(
        duration: Double,
        type: ToastType = ToastType.Default,
        id: String = "",
        overload: Boolean = false,
        onEnd: (() -> Unit)? = null,
        content: @Composable () -> Unit
    ) {
        // ignore spam if overload is enabled and exact toast is visible
        if (overload && activeToast != null && activeId == id) {
            return
        }

        job?.cancel()
        activeType = type
        activeId = id
        activeToast = content

        job = scope.launch {
            delay((duration * 1000).toLong())
            activeToast = null
            activeId = null
            onEnd?.invoke()
        }
    }
}

// internal container

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
            .then(modifier)
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        content()
    }
}

// the end api

fun Toast(
    message: String,
    overload: Boolean = false,
    modifier: Modifier = Modifier
) {
    var duration = 2.0
    var onEnd: (() -> Unit)? = null
    var textCol = Color.White
    var type = ToastType.Default

    modifier.foldIn(Unit) { _, element ->
        if (element is ToastDurationNode) duration = element.seconds
        if (element is ToastOnEndNode) onEnd = element.action
        if (element is ToastTextColorNode) textCol = element.color
        if (element is ToastTypeNode) type = element.type
        Unit
    }

    DriftToastManager.show(
        duration = duration,
        type = type,
        id = message, // uses the message string as its unique identity
        overload = overload,
        onEnd = onEnd
    ) {
        ToastContainer(modifier) {
            Text(
                text = message,
                color = textCol,
                style = TextStyle(fontSize = 14.sp)
            )
        }
    }
}

fun Toast(
    modifier: Modifier = Modifier,
    overloadKey: String = "custom_toast", // identity for custom lambda toasts
    overload: Boolean = false,
    content: @Composable () -> Unit
) {
    var duration = 2.0
    var onEnd: (() -> Unit)? = null
    var type = ToastType.Default

    modifier.foldIn(Unit) { _, element ->
        if (element is ToastDurationNode) duration = element.seconds
        if (element is ToastOnEndNode) onEnd = element.action
        if (element is ToastTypeNode) type = element.type
        Unit
    }

    DriftToastManager.show(
        duration = duration,
        type = type,
        id = overloadKey,
        overload = overload,
        onEnd = onEnd
    ) {
        ToastContainer(modifier) {
            content()
        }
    }
}

// the host

@Composable
fun DriftToastHost() {
    val toast = DriftToastManager.activeToast
    val type = DriftToastManager.activeType
    val view = LocalView.current

    val shakeOffset = remember { androidx.compose.animation.core.Animatable(0f) }

    var isVisible by remember { mutableStateOf(false) }
    var activeContent by remember { mutableStateOf<(@Composable () -> Unit)?>(null) }

    LaunchedEffect(toast) {
        if (toast != null) {
            activeContent = toast
            isVisible = true

            when(type) {
                ToastType.Error -> {
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING)
                    repeat(4) {
                        shakeOffset.animateTo(20f, tween(50))
                        shakeOffset.animateTo(-20f, tween(50))
                    }
                    shakeOffset.animateTo(0f, tween(50))
                }
                ToastType.Success -> {
                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING)
                }
                else -> {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                }
            }
        } else {
            isVisible = false
            delay(200)
            activeContent = null
        }
    }

    if (activeContent != null) {
        Popup(
            alignment = Alignment.BottomCenter,
            properties = PopupProperties(
                focusable = false,
                clippingEnabled = false
            )
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 64.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(300)) + scaleIn(initialScale = 0.8f, animationSpec = tween(300)),
                    exit = fadeOut(tween(200)) + scaleOut(targetScale = 0.8f, animationSpec = tween(200))
                ) {
                    Box(
                        Modifier
                            .padding(32.dp)
                            .offset(x = shakeOffset.value.dp)
                    ) {
                        activeContent?.invoke()
                    }
                }
            }
        }
    }
}