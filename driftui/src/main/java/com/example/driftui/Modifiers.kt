package com.example.driftui
//This file is Modifiers.kt
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding as foundationPadding
import androidx.compose.foundation.background as foundationBackground
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.composed
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.coroutineScope
import androidx.compose.ui.draw.shadow // For the shadow implementation
import androidx.compose.foundation.border // For the border implementation
import androidx.compose.ui.geometry.Offset
// --- CUSTOM CLASS IMPORTS (for defining modifier elements) ---
import com.example.driftui.SheetDetent
import com.example.driftui.State
import com.example.driftui.RoundedRectangle


// ---------------------------------------------------------------------------------------------
// PADDING
// ---------------------------------------------------------------------------------------------

fun Modifier.padding(all: Int): Modifier =
    this.then(Modifier.foundationPadding(all.dp))

fun Modifier.padding(horizontal: Int = 0, vertical: Int = 0): Modifier =
    this.then(Modifier.foundationPadding(horizontal = horizontal.dp, vertical = vertical.dp))

fun Modifier.padding(
    top: Int = 0,
    bottom: Int = 0,
    leading: Int = 0,
    trailing: Int = 0
): Modifier =
    this.then(
        Modifier.foundationPadding(
            PaddingValues(
                start = leading.dp,
                end = trailing.dp,
                top = top.dp,
                bottom = bottom.dp
            )
        )
    )


// ---------------------------------------------------------------------------------------------
// BACKGROUND
// ---------------------------------------------------------------------------------------------

fun Modifier.background(color: Color): Modifier =
    this.then(Modifier.foundationBackground(color))


// ---------------------------------------------------------------------------------------------
// FRAME (Uses widthIn and heightIn from foundation.layout)
// ---------------------------------------------------------------------------------------------

fun Modifier.frame(
    width: Int? = null,
    height: Int? = null,
    minWidth: Int = 0,
    maxWidth: Int? = null,
    minHeight: Int = 0,
    maxHeight: Int? = null
): Modifier {

    var m = this

    if (width != null) m = m.then(Modifier.width(width.dp))
    else m = m.then(Modifier.widthIn(
        min = if (minWidth > 0) minWidth.dp else Dp.Unspecified,
        max = if (maxWidth != null) maxWidth!!.dp else Dp.Unspecified
    ))

    if (height != null) m = m.then(Modifier.height(height.dp))
    else m = m.then(Modifier.heightIn(
        min = if (minHeight > 0) minHeight.dp else Dp.Unspecified,
        max = if (maxHeight != null) maxHeight!!.dp else Dp.Unspecified
    ))

    return m
}


// ---------------------------------------------------------------------------------------------
// VISUAL MODIFIERS
// ---------------------------------------------------------------------------------------------

fun Modifier.offset(x: Int = 0, y: Int = 0): Modifier =
    this.then(
        Modifier.graphicsLayer {
            translationX = x.dp.toPx()
            translationY = y.dp.toPx()
        }
    )

fun Modifier.offset(x: Dp = 0.dp, y: Dp = 0.dp): Modifier =
    this.then(
        Modifier.graphicsLayer {
            translationX = x.toPx()
            translationY = y.toPx()
        }
    )

fun Modifier.rotationEffect(degrees: Int): Modifier =
    this.then(Modifier.graphicsLayer { rotationZ = degrees.toFloat() })

fun Modifier.rotationEffect(degrees: Double): Modifier =
    this.then(Modifier.graphicsLayer { rotationZ = degrees.toFloat() })

fun Modifier.rotationEffect(degrees: Float): Modifier =
    this.then(Modifier.graphicsLayer { rotationZ = degrees })

fun Modifier.scaleEffect(scale: Int): Modifier =
    this.then(Modifier.graphicsLayer {
        scaleX = scale.toFloat()
        scaleY = scale.toFloat()
    })

fun Modifier.scaleEffect(scale: Double): Modifier =
    this.then(Modifier.graphicsLayer {
        scaleX = scale.toFloat()
        scaleY = scale.toFloat()
    })

fun Modifier.scaleEffect(scale: Float): Modifier =
    this.then(Modifier.graphicsLayer {
        scaleX = scale
        scaleY = scale
    })


data class TextAlignmentModifier(val x: Float, val y: Float) : Modifier.Element

fun Modifier.align(x: Float, y: Float): Modifier = this.then(TextAlignmentModifier(x, y))
fun Modifier.align(x: Int, y: Int): Modifier = this.then(TextAlignmentModifier(x.toFloat(), y.toFloat()))


// ---------------------------------------------------------------------------------------------
// SHADOW AND BORDER MODIFIERS
// ---------------------------------------------------------------------------------------------


fun Modifier.border(color: Color, width: Int): Modifier =
    this.then(Modifier.border(width.dp, color))


fun Modifier.shadow(
    radius: Int,
    color: Color = Color.Black.copy(alpha = 0.5f),
    x: Int = 0,
    y: Int = 0
): Modifier =
    this.then(
        Modifier.shadow(
            elevation = radius.dp,
            shape = androidx.compose.ui.graphics.RectangleShape,
            ambientColor = color,
            spotColor = color
        )
            .offset(x = x.dp, y = y.dp)
    )


// ---------------------------------------------------------------------------------------------
// LIFECYCLE MODIFIERS (onAppear / onDisappear)
// ---------------------------------------------------------------------------------------------

// Modifiers used in NavigationStack to trigger actions on Composition start/end
data class LifecycleAppearModifier(val action: () -> Unit) : Modifier.Element
data class LifecycleDisappearModifier(val action: () -> Unit) : Modifier.Element


fun Modifier.onAppear(action: () -> Unit): Modifier =
    this.then(LifecycleAppearModifier(action))


fun Modifier.onDisappear(action: () -> Unit): Modifier =
    this.then(LifecycleDisappearModifier(action))



// Opacity:

fun Modifier.opacity(value: Double): Modifier =
    this.then(
        Modifier.graphicsLayer {
            alpha = value.toFloat()
        }
    )

fun Modifier.opacity(value: Float): Modifier =
    this.then(
        Modifier.graphicsLayer {
            alpha = value
        }
    )


// ---------------------------------------------------------------------------------------------
// FONT, COLOR, AND SHAPE MODIFIER ELEMENTS
// ---------------------------------------------------------------------------------------------

val bold = FontWeight.Bold
val semibold = FontWeight.SemiBold
val medium = FontWeight.Medium
val regular = FontWeight.Normal
val light = FontWeight.Light
val thin = FontWeight.Thin
val ultralight = FontWeight.ExtraLight
val black = FontWeight.Black

data class SystemFont(val size: Int, val weight: FontWeight = regular)
fun system(size: Int, weight: FontWeight = regular) = SystemFont(size, weight)

data class FontModifier(val font: SystemFont) : Modifier.Element
data class ForegroundColorModifier(val color: Color) : Modifier.Element
data class BackgroundColorModifier(val color: Color) : Modifier.Element

data class BackButtonHiddenModifier(val hidden: Boolean) : Modifier.Element

// And ensure the extension function uses it correctly:
fun Modifier.navigationBarBackButtonHidden(hidden: Boolean): Modifier =
    this.then(BackButtonHiddenModifier(hidden))
fun Modifier.font(font: SystemFont) = this.then(FontModifier(font))
fun Modifier.foregroundStyle(color: Color) = this.then(ForegroundColorModifier(color))

fun Modifier.clipShape(shape: Shape): Modifier = this.clip(shape)
fun Modifier.cornerRadius(radius: Int): Modifier = this.clipShape(RoundedRectangle(radius))


// ---------------------------------------------------------------------------------------------
// CUSTOM STYLING MODIFIERS (made public for inter-file access)
// ---------------------------------------------------------------------------------------------

// Sheet Modifier
data class SheetModifier(
    val isPresented: State<Boolean>,
    val detents: List<SheetDetent> = listOf(SheetDetent.Large),
    val initialDetent: SheetDetent = SheetDetent.Large,
    val showGrabber: Boolean = true,
    val cornerRadius: Int = 20,
    val allowDismiss: Boolean = true,
    val content: @Composable () -> Unit
) : Modifier.Element

fun Modifier.sheet(
    isPresented: State<Boolean>,
    detents: List<SheetDetent> = listOf(SheetDetent.Large),
    initialDetent: SheetDetent = SheetDetent.Large,
    showGrabber: Boolean = true,
    cornerRadius: Int = 20,
    allowDismiss: Boolean = true,
    content: @Composable () -> Unit
): Modifier = this.then(
    SheetModifier(
        isPresented = isPresented,
        detents = detents,
        initialDetent = initialDetent,
        showGrabber = showGrabber,
        cornerRadius = cornerRadius,
        allowDismiss = allowDismiss,
        content = content
    )
)
fun Modifier.sheet(
    isPresented: State<Boolean>,
    detents: List<Double>,
    initialDetent: Double = detents.first(),
    showGrabber: Boolean = true,
    cornerRadius: Int = 20,
    allowDismiss: Boolean = true,
    content: @Composable () -> Unit
): Modifier = this.sheet(
    isPresented = isPresented,
    detents = detents.map { SheetDetent.Fraction(it.toFloat()) },
    initialDetent = SheetDetent.Fraction(initialDetent.toFloat()),
    showGrabber = showGrabber,
    cornerRadius = cornerRadius,
    allowDismiss = allowDismiss,
    content = content
)

// Toggle styling
data class ToggleStyleModifier(
    val onColor: Color? = null,
    val offColor: Color? = null,
    val thumbColor: Color? = null
) : Modifier.Element

fun Modifier.toggleStyle(
    onColor: Color? = null,
    offColor: Color? = null,
    thumbColor: Color? = null
): Modifier = this.then(
    ToggleStyleModifier(onColor, offColor, thumbColor)
)

// Slider styling
data class SliderStyleModifier(
    val activeTrackColor: Color? = null,
    val inactiveTrackColor: Color? = null,
    val thumbColor: Color? = null,
    val stepColor: Color? = null,
    val stepOpacity: Float? = null
) : Modifier.Element

private data class TapCountData(val count: Int, val lastTapTime: Long)

fun Modifier.sliderStyle(
    activeTrackColor: Color? = null,
    inactiveTrackColor: Color? = null,
    thumbColor: Color? = null,
    stepColor: Color? = null,
    stepOpacity: Double? = null
): Modifier = this.then(
    SliderStyleModifier(
        activeTrackColor = activeTrackColor,
        inactiveTrackColor = inactiveTrackColor,
        thumbColor = thumbColor,
        stepColor = stepColor,
        stepOpacity = stepOpacity?.toFloat()
    )
)

// Stepper styling
data class StepperStyleModifier(
    val buttonColor: Color? = null,
    val buttonForegroundColor: Color? = null,
    val valueColor: Color? = null,
    val buttonOffset: Dp? = null
) : Modifier.Element

fun Modifier.stepperStyle(
    buttonColor: Color? = null,
    buttonForegroundColor: Color? = null,
    valueColor: Color? = null,
    buttonOffset: Int? = null
): Modifier = this.then(
    StepperStyleModifier(
        buttonColor = buttonColor,
        buttonForegroundColor = buttonForegroundColor,
        valueColor = valueColor,
        buttonOffset = buttonOffset?.dp
    )
)

// Toolbar styling
data class ToolbarStyleModifier(
    val foreground: Color? = null,
    val background: Color? = null,
    val elevation: Dp? = null,
    val contentPadding: Dp? = null
) : Modifier.Element

fun Modifier.toolbarStyle(
    foregroundColor: Color? = null,
    backgroundColor: Color? = null,
    elevation: Dp? = null,
    contentPadding: Dp? = null
): Modifier = this.then(
    ToolbarStyleModifier(
        foreground = foregroundColor,
        background = backgroundColor,
        elevation = elevation,
        contentPadding = contentPadding
    )
)

// Toolbar Layout (must be defined for NavigationAndSheet.kt)
private data class ToolbarLayoutModifier(val layoutModifier: Modifier) : Modifier.Element
fun Modifier.toolbarLayout(modifier: Modifier): Modifier =
    this.then(ToolbarLayoutModifier(modifier))

// Navigation/Scheme Modifiers (made public)
data class NavigationTitleModifier(val title: String) : Modifier.Element

data class PreferredColorSchemeModifier(val scheme: DriftColorScheme) : Modifier.Element
enum class DriftColorScheme { Light, Dark }
val darkMode = DriftColorScheme.Dark
val lightMode = DriftColorScheme.Light

fun Modifier.preferredColorScheme(scheme: DriftColorScheme): Modifier =
    this.then(PreferredColorSchemeModifier(scheme))

// ---------------------------------------------------------------------------------------------
// GESTURE MODIFIERS
// ---------------------------------------------------------------------------------------------

fun Modifier.onTapGesture(action: () -> Unit): Modifier =
    composed {
        Modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) {
            action()
        }
    }

fun Modifier.onDoubleTap(action: () -> Unit): Modifier =
    this.then(
        Modifier.pointerInput(Unit) {
            detectTapGestures(onDoubleTap = { action() })
        }
    )

fun Modifier.onTripleTap(action: () -> Unit): Modifier =
    this.then(
        Modifier.composed {
            val tapData = remember { mutableStateOf(TapCountData(0, 0L)) }
            val resetTime = 500L

            Modifier.pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        val currentTime = System.currentTimeMillis()
                        val currentCount = tapData.value.count
                        val newCount: Int

                        if (currentTime - tapData.value.lastTapTime < resetTime) {
                            newCount = currentCount + 1
                        } else {
                            newCount = 1
                        }

                        tapData.value = TapCountData(newCount, currentTime)

                        if (newCount == 3) {
                            action()
                            tapData.value = TapCountData(0, 0L)
                        }
                    }
                )
            }
        }
    )

fun Modifier.onHold(action: () -> Unit): Modifier =
    this.then(
        Modifier.pointerInput(Unit) {
            detectTapGestures(onLongPress = { action() })
        }
    )

fun Modifier.untilHold(
    onPress: (() -> Unit)? = null,
    onRelease: (() -> Unit)? = null
): Modifier =
    this.then(
        Modifier.pointerInput(Unit) {
            coroutineScope {
                detectTapGestures(
                    onPress = {
                        onPress?.invoke()
                        try {
                            awaitRelease()
                        } finally {
                            onRelease?.invoke()
                        }
                    }
                )
            }
        }
    )

//Pen Tool:
data class DrawModifier(val onDraw: (Offset) -> Unit) : Modifier.Element
data class DrawEndModifier(val onEnd: () -> Unit) : Modifier.Element

fun Modifier.onDraw(action: (Offset) -> Unit): Modifier = this.then(DrawModifier(action))
fun Modifier.onDrawEnd(action: () -> Unit): Modifier = this.then(DrawEndModifier(action))
