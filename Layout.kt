package com.example.driftui

// ---------------------------------------------------------------------------------------------
// IMPORTS
// ---------------------------------------------------------------------------------------------

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image as ComposeImage
import androidx.compose.foundation.background as foundationBackground
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding as foundationPadding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider as MaterialDivider
import androidx.compose.material3.Text as MaterialText
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.LocalLifecycleOwner

import kotlin.reflect.KProperty
import androidx.compose.foundation.clickable

import androidx.lifecycle.ViewModelStoreOwner
import androidx.compose.material3.TopAppBar

import androidx.compose.material3.ExperimentalMaterial3Api
//Theme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.composed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.material3.Surface
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.IntSize
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.core.view.WindowCompat
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.ShapeDefaults.Large
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner.provides
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
// Add to your IMPORTS section at the top of the file
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.ui.platform.LocalConfiguration
import kotlin.math.abs
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.ui.platform.LocalConfiguration

// ---------------------------------------------------------------------------------------------
// COLORS (SwiftUI-style)
// ---------------------------------------------------------------------------------------------

private val Black     = Color(0xFF000000)
private val Blue      = Color(0xFF007AFF)
private val Cyan      = Color(0xFF00FFFF)
private val Gray      = Color(0xFF8E8E93)
private val Green     = Color(0xFF34C759)
private val LightGray = Color(0xFFD1D1D6)
private val Magenta   = Color(0xFFFF2D55)
private val Red       = Color(0xFFFF3B30)
private val White     = Color(0xFFFFFFFF)
private val Yellow    = Color(0xFFFFCC00)
//private val mintGreen = Color(0xFF567779)



val Color.Companion.black get() = Black
val Color.Companion.blue get() = Blue
val Color.Companion.cyan get() = Cyan
val Color.Companion.gray get() = Gray
val Color.Companion.green get() = Green
val Color.Companion.lightGray get() = LightGray
val Color.Companion.magenta get() = Magenta
val Color.Companion.red get() = Red
val Color.Companion.white get() = White
val Color.Companion.yellow get() = Yellow

val Color.Companion.pink get() = Color(0xFFCF00FF)
val Color.Companion.orange get() = Color(0xFFFF9500)
val Color.Companion.purple get() = Color(0xFF7D00FF)
val Color.Companion.brown get() = Color(0xFFA2845E)
val Color.Companion.mint get() = Color(0xFF00C7BE)
val Color.Companion.teal get() = Color(0xFF30B0C7)
val Color.Companion.indigo get() = Color(0xFF5856D6)
val Color.Companion.shazan get() = Color(0xFF567779)


//Default theme colours for modifiers

data class DriftColors(
    val text: Color,
    val background: Color,
    val fieldBackground: Color,
    val accent: Color
)
val driftColors: DriftColors
    @Composable get() = DriftColors(
        text = MaterialTheme.colorScheme.onBackground,
        background = MaterialTheme.colorScheme.background,
        fieldBackground = MaterialTheme.colorScheme.surface,
        accent = MaterialTheme.colorScheme.primary
    )



// ---------------------------------------------------------------------------------------------
// ALIGNMENTS
// ---------------------------------------------------------------------------------------------

val top: Alignment = Alignment.TopCenter
val center: Alignment = Alignment.Center
val bottom: Alignment = Alignment.BottomCenter
val leading: Alignment = Alignment.CenterStart
val trailing: Alignment = Alignment.CenterEnd
val topLeading: Alignment = Alignment.TopStart
val topTrailing: Alignment = Alignment.TopEnd
val bottomLeading: Alignment = Alignment.BottomStart
val bottomTrailing: Alignment = Alignment.BottomEnd



// ---------------------------------------------------------------------------------------------
// VSTACK / HSTACK / ZSTACK / DriftView
// ---------------------------------------------------------------------------------------------

@Composable
fun VStack(
    modifier: Modifier = Modifier,
    alignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    spacing: Int = 8,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = alignment,
        verticalArrangement = Arrangement.spacedBy(spacing.dp),
        content = content
    )
}

@Composable
fun HStack(
    modifier: Modifier = Modifier,
    alignment: Alignment.Vertical = Alignment.CenterVertically,
    spacing: Int = 8,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = alignment,
        horizontalArrangement = Arrangement.spacedBy(spacing.dp),
        content = content
    )
}

@Composable
fun ZStack(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier, contentAlignment = contentAlignment, content = content)
}

@Composable
fun DriftView(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(modifier = modifier.fillMaxSize().background(driftColors.background),
        contentAlignment = Alignment.Center, content = content)
}



// ---------------------------------------------------------------------------------------------
// PADDING (fixed to avoid Modifier.then errors)
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
// SPACER
// ---------------------------------------------------------------------------------------------

@Composable
fun ColumnScope.Spacer() = androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))

@Composable
fun RowScope.Spacer() = androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))

@Composable
fun Spacer(size: Int) = androidx.compose.foundation.layout.Spacer(Modifier.size(size.dp))


// ---------------------------------------------------------------------------------------------
// SHAPES
// ---------------------------------------------------------------------------------------------

@Composable
private fun Modifier.getForegroundColor(): Color? {
    var chosenColor: Color? = null
    this.foldIn(Unit) { _, element ->
        if (element is ForegroundColorModifier) {
            chosenColor = element.color
        }
        Unit
    }
    return chosenColor
}

@Composable
fun Rectangle(
    width: Int,
    height: Int,
    modifier: Modifier = Modifier
) {
    val fgColor = modifier.getForegroundColor() ?: driftColors.text

    Box(
        modifier = modifier
            .size(width.dp, height.dp)
            .clip(androidx.compose.ui.graphics.RectangleShape)
            .foundationBackground(fgColor)
    )
}
@Composable
fun Circle(
    radius: Int,
    modifier: Modifier = Modifier
) {
    val fgColor = modifier.getForegroundColor() ?: driftColors.text

    Box(
        modifier = modifier
            .size(radius.dp * 2)
            .clip(CircleShape)
            .foundationBackground(fgColor)
    )
}

@Composable
fun Capsule(
    width: Int,
    height: Int,
    modifier: Modifier = Modifier
) {
    val fgColor = modifier.getForegroundColor() ?: driftColors.text

    Box(
        modifier = modifier
            .size(width.dp, height.dp)
            .clip(RoundedCornerShape(percent = 50))
            .foundationBackground(fgColor)
    )
}

@Composable
fun RoundedRectangle(
    width: Int,
    height: Int,
    cornerRadius: Int,
    modifier: Modifier = Modifier
) {
    val fgColor = modifier.getForegroundColor() ?: driftColors.text

    Box(
        modifier = modifier
            .size(width.dp, height.dp)
            .clip(RoundedCornerShape(cornerRadius.dp))
            .foundationBackground(fgColor)
    )
}
fun Circle(): Shape = CircleShape
fun Capsule(): Shape = RoundedCornerShape(percent = 50)
fun RoundedRectangle(radius: Int): Shape = RoundedCornerShape(radius.dp)

fun Modifier.clipShape(shape: Shape): Modifier = this.clip(shape)
fun Modifier.cornerRadius(radius: Int): Modifier = this.clipShape(RoundedRectangle(radius))

// ---------------------------------------------------------------------------------------------
// BACKGROUND (fixed implementation)
// ---------------------------------------------------------------------------------------------

fun Modifier.background(color: Color): Modifier =
    this.then(Modifier.foundationBackground(color))



// ---------------------------------------------------------------------------------------------
// FRAME
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
// OFFSET (SwiftUI-style)
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



// --- rotationEffect clean overloads ---
fun Modifier.rotationEffect(degrees: Int): Modifier =
    this.then(Modifier.graphicsLayer { rotationZ = degrees.toFloat() })

fun Modifier.rotationEffect(degrees: Double): Modifier =
    this.then(Modifier.graphicsLayer { rotationZ = degrees.toFloat() })

fun Modifier.rotationEffect(degrees: Float): Modifier =
    this.then(Modifier.graphicsLayer { rotationZ = degrees })

// --- scaleEffect clean overloads ---
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

// ---------------------------------------------------------------------------------------------
// OPACITY (SwiftUI-style)
// ---------------------------------------------------------------------------------------------

fun Modifier.opacity(value: Double): Modifier =
    this.then(
        Modifier.graphicsLayer {
            // Converts the Double value (0.0 to 1.0) into the composable's alpha channel
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
// FONT SYSTEM + TEXT
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

private data class FontModifier(val font: SystemFont) : Modifier.Element


private data class ForegroundColorModifier(val color: Color) : Modifier.Element

private data class BackgroundColorModifier(val color: Color) : Modifier.Element

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




// --- Toggle styling support ---
private data class ToggleStyleModifier(
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


// --- Slider styling support ---
private data class SliderStyleModifier(
    val activeTrackColor: Color? = null,
    val inactiveTrackColor: Color? = null,
    val thumbColor: Color? = null,
    val stepColor: Color? = null,
    val stepOpacity: Float? = null // Using Float for opacity (0.0 to 1.0)
) : Modifier.Element

private data class TapCountData(val count: Int, val lastTapTime: Long)

fun Modifier.sliderStyle(
    activeTrackColor: Color? = null,
    inactiveTrackColor: Color? = null,
    thumbColor: Color? = null,
    stepColor: Color? = null,
    // REMOVED: thumbSize: Int? = null,
    stepOpacity: Double? = null
): Modifier = this.then(
    SliderStyleModifier(
        activeTrackColor = activeTrackColor,
        inactiveTrackColor = inactiveTrackColor,
        thumbColor = thumbColor,
        stepColor = stepColor,
        // REMOVED: thumbSize = thumbSize?.dp,
        stepOpacity = stepOpacity?.toFloat()
    )
)

// convenience modifier for toolbar background specifically (keeps it explicit)
fun Modifier.toolbarBackground(color: Color) = this.then(BackgroundColorModifier(color))

// ---- toolbar styling support ----
data class ToolbarStyle(
    val background: Color? = null,
    val foreground: Color? = null
)

val LocalToolbarStyle = compositionLocalOf { ToolbarStyle() }


// --- Stepper styling support ---
private data class StepperStyleModifier(
    val buttonColor: Color? = null,
    val buttonForegroundColor: Color? = null,
    val valueColor: Color? = null,
) : Modifier.Element

fun Modifier.stepperStyle(
    buttonColor: Color? = null,
    buttonForegroundColor: Color? = null,
    valueColor: Color? = null,
): Modifier = this.then(
    StepperStyleModifier(
        buttonColor = buttonColor,
        buttonForegroundColor = buttonForegroundColor,
        valueColor = valueColor,
    )
)

fun Modifier.font(font: SystemFont) = this.then(FontModifier(font))
fun Modifier.foregroundStyle(color: Color) = this.then(ForegroundColorModifier(color))


@Composable
fun Text(text: String, modifier: Modifier = Modifier) {
    var chosenFont: SystemFont? = null
    var chosenColor = Color.Unspecified

    modifier.foldIn(Unit) { _, element ->
        when (element) {
            is FontModifier -> chosenFont = element.font
            is ForegroundColorModifier -> chosenColor = element.color
        }
        Unit
    }

    val style = TextStyle(
        fontSize = chosenFont?.size?.sp ?: TextStyle.Default.fontSize,
        fontWeight = chosenFont?.weight ?: TextStyle.Default.fontWeight,
        color = chosenColor.takeUnless { it == Color.Unspecified } ?: driftColors.text
    )

    MaterialText(
        text = text,
        modifier = modifier,
        style = style,
    )
}




// ---------------------------------------------------------------------------------------------
// IMAGE
// ---------------------------------------------------------------------------------------------

@Composable
fun Image(
    name: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit
) {
    val ctx = LocalContext.current
    val id = ctx.resources.getIdentifier(name, "drawable", ctx.packageName)

    ComposeImage(
        painter = painterResource(id),
        contentDescription = null,
        modifier = modifier,
        contentScale = contentScale
    )
}



// =============================================================================================
// STATE, PUBLISHED, TEXTFIELD, SECUREFIELD
// =============================================================================================

// -----------------------------------------
// STATE<T>
// -----------------------------------------

class Binding<T>(
    val value: T,
    val set: (T) -> Unit
)

class State<T>(initial: T) {
    private val s = mutableStateOf(initial)

    val value get() = s.value

    operator fun getValue(thisRef: Any?, property: KProperty<*>) = s.value
    operator fun setValue(thisRef: Any?, property: KProperty<*>, v: T) { s.value = v }

    fun set(v: T) { s.value = v }

    fun binding() = Binding(s.value) { s.value = it }

    fun toggle() {
        if (s.value is Boolean) {
            // Safe cast and flip the value
            @Suppress("UNCHECKED_CAST")
            s.value = !(s.value as Boolean) as T
        } else {
            // Optional: Log an error or ignore if not a Boolean state
            println("Drift UI Error: .toggle() called on non-Boolean State.")
        }
    }
    fun getSetter(): (T) -> Unit = { newValue ->
        s.value = newValue
    }


}

// -----------------------------------------
// Published<T>
// -----------------------------------------

typealias Published<T> = State<T>

// -----------------------------------------
// TEXTFIELD
// -----------------------------------------

@Composable
fun TextField(
    placeholder: String,
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = text,
        onValueChange = onTextChange,
        modifier = modifier, // use external modifier entirely
        textStyle = TextStyle.Default.copy(fontSize = 16.sp),
        decorationBox = { inner ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 8, vertical = 8) // inner padding only
            ) {
                if (text.isEmpty()) {
                    MaterialText(
                        text = placeholder,
                        style = TextStyle.Default.copy(
                            fontSize = 16.sp,
//                            color = driftColors.text.copy(alpha = 0.6f)
                            color = Color.Gray
                        )
                    )
                }
                inner()
            }
        }
    )
}

@Composable
fun TextField(
    placeholder: String,
    value: State<String>,
    modifier: Modifier = Modifier
) {
    val b = value.binding()
    TextField(placeholder, b.value, b.set, modifier)
}



// -----------------------------------------
// SECUREFIELD
// -----------------------------------------

@Composable
fun SecureField(
    placeholder: String,
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = text,
        onValueChange = onTextChange,
        modifier = modifier, // use external modifier entirely
        textStyle = TextStyle.Default.copy(fontSize = 16.sp),
        visualTransformation = PasswordVisualTransformation(),
        decorationBox = { inner ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 8, vertical = 8) // inner padding only
            ) {
                if (text.isEmpty()) {
                    MaterialText(
                        text = placeholder,
                        style = TextStyle.Default.copy(
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    )
                }
                inner()
            }
        }
    )
}

@Composable
fun SecureField(
    placeholder: String,
    value: State<String>,
    modifier: Modifier = Modifier
) {
    val b = value.binding()
    SecureField(placeholder, b.value, b.set, modifier)
}




// =============================================================================================
// BUTTON, DIVIDER, LIST
// =============================================================================================

@Composable
fun Button(
    action: () -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier.clickable(
            onClick = action,
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ),
        contentAlignment = Alignment.Center,
        content = label
    )
}

@Composable
fun Divider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
    thickness: Int = 1
) {
    MaterialDivider(
        modifier = modifier,
        color = color,
        thickness = thickness.dp
    )
}

@Composable
fun List(
    alignment: Alignment = center,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(Modifier.fillMaxSize(), contentAlignment = alignment) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 8)
                .clip(RoundedRectangle(10))
                .background(driftColors.fieldBackground.copy(alpha = 0.2f)),
            content = content
        )
    }
}

@Composable
fun <T> List(
    items: List<T>,
    alignment: Alignment = center,
    modifier: Modifier = Modifier,
    rowContent: @Composable (T) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(horizontal = 8),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        itemsIndexed(items) { index, item ->
            rowContent(item)
            if (index < items.size - 1) Divider()
        }
    }
}

// =============================================================================================
// PREMIUM TOGGLE (iOS-grade animation, DriftUI style)
// =============================================================================================

@Composable
fun Toggle(
    isOn: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    label: (@Composable () -> Unit)? = null
) {
    // --- ANIMATION STATE MAGIC ----------------------------------------------

    // Read styling from modifier
    var styleOn: Color? = null
    var styleOff: Color? = null
    var styleThumb: Color? = null

    modifier.foldIn(Unit) { _, el ->
        if (el is ToggleStyleModifier) {
            if (el.onColor != null) styleOn = el.onColor
            if (el.offColor != null) styleOff = el.offColor
            if (el.thumbColor != null) styleThumb = el.thumbColor
        }
        Unit
    }


    // 1. Thumb overshoot animation (bounce on state change)
    val thumbOffset by animateDpAsState(
        targetValue = if (isOn) 22.dp else 2.dp,
        animationSpec = spring(
            dampingRatio = 0.55f,
            stiffness = 300f
        ),
        label = ""
    )

    // 2. Thumb scale (pop when toggled)
    val thumbScale by animateFloatAsState(
        targetValue = if (isOn) 1.06f else 1.0f,
        animationSpec = tween(120),
        label = ""
    )

    // 3. Track background color
    val trackColor by animateColorAsState(
        targetValue = if (isOn)
            (styleOn ?: driftColors.accent)
        else
            (styleOff ?: driftColors.fieldBackground),
        animationSpec = tween(200),
        label = ""
    )


    // 4. Track shadow glow when ON
    val trackShadow = if (isOn) 8.dp else 1.dp
    val trackShadowColor = if (isOn)
        driftColors.accent.copy(alpha = 0.35f)
    else
        Color.Black.copy(alpha = 0.12f)

    // 5. Thumb color fade
    val thumbColor by animateColorAsState(
        targetValue = styleThumb
            ?: if (isOn) Color.White
            else Color.White.copy(alpha = 0.85f),
        animationSpec = tween(180),
        label = ""
    )


    // 6. Thumb glow when ON
    val thumbShadow = if (isOn) 10.dp else 0.dp

    // 7. Press gesture scale (tap feedback)
    var pressed by remember { mutableStateOf(false) }
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.92f else 1.0f,
        animationSpec = tween(80),
        label = ""
    )

    // --- UI -----------------------------------------------------------

    Row(
        modifier = modifier
            .wrapContentHeight()
            .then(Modifier.onTapGesture {
                pressed = true
                onToggle(!isOn)
                // slight delay to release scale animation
                pressed = false
            }),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // Label
        if (label != null) label()

        // Track
        Box(
            modifier = Modifier
                .size(width = 52.dp, height = 30.dp)
                .graphicsLayer {
                    scaleX = pressScale
                    scaleY = pressScale
                }
                .clip(Capsule())
                .background(trackColor)
                .shadow(trackShadow, Capsule(), false, trackShadowColor),
            contentAlignment = Alignment.CenterStart
        ) {

            // Thumb
            Box(
                modifier = Modifier
                    .offset(x = thumbOffset)
                    .size(26.dp)
                    .graphicsLayer {
                        scaleX = thumbScale
                        scaleY = thumbScale
                        shadowElevation = thumbShadow.toPx()
                    }
                    .clip(Circle())
                    .background(thumbColor)
            )
        }
    }

}


// In Layout.kt (Add after the Primary Toggle function)

@Composable
fun Toggle(
    value: State<Boolean>, // Accepts the raw state variable (e.g., value = wifi)
    modifier: Modifier = Modifier,
    label: @Composable () -> Unit // Trailing lambda for custom content
) {
    val b = value.binding()
    Toggle(
        isOn = b.value,
        onToggle = b.set,
        modifier = modifier,
        label = label
    )
}

// In Layout.kt (Add after the Custom Content Block overload)

@Composable
fun Toggle(
    label: String, // String label for convenience
    value: State<Boolean>,
    modifier: Modifier = Modifier
) {
    val b = value.binding()
    Toggle(isOn = b.value, onToggle = b.set, modifier = modifier) {
        // Renders the string label using the Text composable
        Text(label)
    }
}



// =============================================================================================
// MVVM (SwiftUI-like)
// =============================================================================================


open class ObservableObject : ViewModel()

@Composable
inline fun <reified T : ObservableObject> StateObject(): T {
    return viewModel<T>()
}

@Composable
inline fun <reified T : ObservableObject> EnvironmentObject(): T {

    val lifecycleOwner = LocalLifecycleOwner.current
    val storeOwner = lifecycleOwner as ViewModelStoreOwner

    // NOTE: Your Compose version supports viewModel(owner)
    return viewModel<T>(storeOwner)
}

@Composable
inline fun <reified T : ObservableObject> ObservedObject(noinline factory: () -> T): T {
    return remember { factory() }
}

// Slider (SwiftUI-style)

// In Layout.kt (Replace the first Slider function definition)

@Composable
fun Slider(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange = 0..100,
    step: Int = 0,
    modifier: Modifier = Modifier
) {
    // --- READ STYLING FROM MODIFIER CHAIN ---
    var styleActiveTrack: Color? = null
    var styleInactiveTrack: Color? = null
    var styleThumbColor: Color? = null
    var styleStepColor: Color? = null
    var styleThumbSize: Dp? = null
    var styleStepOpacity: Float? = null

    modifier.foldIn(Unit) { _, el ->
        if (el is SliderStyleModifier) {
            styleActiveTrack = el.activeTrackColor ?: styleActiveTrack
            styleInactiveTrack = el.inactiveTrackColor ?: styleInactiveTrack
            styleThumbColor = el.thumbColor ?: styleThumbColor
            styleStepColor = el.stepColor ?: styleStepColor
            styleStepOpacity = el.stepOpacity ?: styleStepOpacity
        }
        Unit
    }

    // --- DETERMINE FINAL COLORS AND VALUES ---
    val finalActiveTrackColor = styleActiveTrack ?: driftColors.accent
    val finalInactiveTrackColor = styleInactiveTrack ?: driftColors.fieldBackground
    val finalThumbColor = styleThumbColor ?: driftColors.accent
    val finalStepColor = styleStepColor ?: styleActiveTrack ?: driftColors.accent // Default step color to active track

    // Apply Opacity to the step color if set
    val stepsColorWithOpacity = finalStepColor.copy(alpha = styleStepOpacity ?: 0.54f) // Default step opacity is usually 54%

    // --- CONVERSION LOGIC ---
    val floatValue = value.toFloat()
    val floatRange = range.first.toFloat()..range.last.toFloat()
    val stepsCount = if (step > 0) ((range.last - range.first) / step) else 0

    // --- RENDER SLIDER ---
    androidx.compose.material3.Slider(
        value = floatValue,
        onValueChange = { newValue -> onValueChange(newValue.toInt()) },
        modifier = modifier.fillMaxWidth(),
        valueRange = floatRange,
        steps = stepsCount,
        colors = SliderDefaults.colors(
            activeTrackColor = finalActiveTrackColor,
            inactiveTrackColor = finalInactiveTrackColor,
            thumbColor = finalThumbColor,
            activeTickColor = stepsColorWithOpacity,
            inactiveTickColor = stepsColorWithOpacity // Use the same color for both
        ),
        // Applying thumb size requires a custom track and thumb composable, which is complex.
        // We can pass the size to colors or rely on the theme, but for now, we leave it in the styling struct.
    )
}

// In Layout.kt (Replace your existing SwiftUI-style Slider overload)

@Composable
fun Slider(
    value: State<Int>, // Now uses State<Int>
    range: IntRange = 0..100, // Uses standard IntRange
    step: Int = 0,
    modifier: Modifier = Modifier
) {
    // Delegates to the primary implementation using the State<Int> binding
    val b = value.binding()
    Slider(
        value = b.value,
        onValueChange = b.set,
        range = range,
        step = step,
        modifier = modifier
    )
}
//------------------------------------------------------------------------------------------------
// MARK: Toolbar Below
//------------------------------------------------------------------------------------------------


// ---------------------------
// Toolbar model + registration
// ---------------------------
enum class ToolbarPlacement { Leading, Center, Trailing }

data class ToolbarEntry(
    val placement: ToolbarPlacement,
    val content: @Composable () -> Unit
)

val LocalToolbarState = compositionLocalOf<MutableList<ToolbarEntry>?> { null }

// New composition local: a mutable state holder that toolbar() will write its full layout modifier into.
// NavigationStack will provide an instance of this mutable state and read it when rendering the toolbar.
val LocalToolbarLayoutState = compositionLocalOf<MutableState<Modifier?>?> { null }

@Composable
fun ToolbarItem(
    placement: ToolbarPlacement,
    content: @Composable () -> Unit
) {
    val state = LocalToolbarState.current ?: return
    val entry = remember { ToolbarEntry(placement, content) }
    DisposableEffect(state, entry) {
        state.add(entry)
        onDispose { state.remove(entry) }
    }
}

// ---------------------------
// Toolbar style locals + modifier
// ---------------------------
private data class ToolbarStyleModifier(
    val foreground: Color? = null,
    val background: Color? = null,
    val elevation: Dp? = null,
    val contentPadding: Dp? = null
) : Modifier.Element

private data class ToolbarLayoutModifier(val layoutModifier: Modifier) : Modifier.Element

fun Modifier.toolbarLayout(modifier: Modifier): Modifier =
    this.then(ToolbarLayoutModifier(modifier))

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

val LocalToolbarForeground = compositionLocalOf<Color?> { null }
val LocalToolbarBackground = compositionLocalOf<Color?> { null }
val LocalToolbarElevation = compositionLocalOf<Dp?> { null }
val LocalToolbarContentPadding = compositionLocalOf<Dp?> { null }

// toolbar {} block — collects style from modifier and provides locals for children.
@Composable
fun toolbar(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // Extract ONLY styling values from modifier for locals (so toolbar children can see colors/elevation/padding)
    var fg: Color? = null
    var bg: Color? = null
    var elev: Dp? = null
    var paddingDp: Dp? = null

    modifier.foldIn(Unit) { _, element ->
        if (element is ToolbarStyleModifier) {
            if (element.foreground != null) fg = element.foreground
            if (element.background != null) bg = element.background
            if (element.elevation != null) elev = element.elevation
            if (element.contentPadding != null) paddingDp = element.contentPadding
        }
        Unit
    }

    // Write the full layout modifier into the shared LayoutState (if provided by parent NavigationStack)
    val layoutState = LocalToolbarLayoutState.current
    DisposableEffect(layoutState, modifier) {
        // save previous modifier to restore on dispose
        val previous = layoutState?.value
        layoutState?.value = modifier
        onDispose {
            layoutState?.value = previous
        }
    }

    CompositionLocalProvider(
        LocalToolbarForeground provides fg,
        LocalToolbarBackground provides bg,
        LocalToolbarElevation provides elev,
        LocalToolbarContentPadding provides paddingDp
    ) {
        content()
    }
}
// ---------------------------------------------------------
// SHEET SYSTEM (SwiftUI-style)
// ---------------------------------------------------------

data class SheetState(
    var isPresented: MutableState<Boolean>,
    val content: MutableState<(@Composable () -> Unit)?>
)


// ---------------------------
// Navigation controller
// ---------------------------

class DriftNavController(private val stack: MutableList<@Composable () -> Unit>) {
    fun push(screen: @Composable () -> Unit) {
        stack.add(screen)
    }

    fun pop() {
        if (stack.isNotEmpty()) {
            stack.removeAt(stack.lastIndex) // API 21+ compatible
        }
    }

    fun clear() {
        stack.clear()
    }

    fun dismiss() = pop()

    fun current(): (@Composable () -> Unit)? = stack.lastOrNull()

    fun canPop() = stack.isNotEmpty()
}

val LocalNavController = compositionLocalOf<DriftNavController> {
    error("NavigationStack not found in composition.")
}

// SwiftUI-style Dismiss helper
@Composable
fun Dismiss(): () -> Unit {
    val nav = LocalNavController.current
    return remember {
        {nav.dismiss()}
    }
}

// ---------------------------
// NavigationLink variants
// ---------------------------
@Composable
fun NavigationLink(
    title: String,
    destination: @Composable () -> Unit
) {
    val nav = LocalNavController.current
    Text(
        title,
        Modifier.onTapGesture {
            nav.push(destination)
        }
    )
}

@Composable
fun NavigationLink(
    destination: @Composable () -> Unit,
    label: @Composable () -> Unit
) {
    val nav = LocalNavController.current
    Box(
        Modifier.onTapGesture {
            nav.push(destination)
        }
    ) {
        label()
    }
}

// Navigation title modifier (keeps your existing API)
private data class NavigationTitleModifier(val title: String) : Modifier.Element
fun Modifier.navigationTitle(title: String): Modifier =
    this.then(NavigationTitleModifier(title))

// Back button hidden modifier
private data class BackButtonHiddenModifier(val hidden: Boolean) : Modifier.Element
fun Modifier.navigationBarBackButtonHidden(hidden: Boolean): Modifier =
    this.then(BackButtonHiddenModifier(hidden))

// preferredColorScheme modifier (keeps previous)
enum class DriftColorScheme { Light, Dark }
private data class PreferredColorSchemeModifier(val scheme: DriftColorScheme) : Modifier.Element
fun Modifier.preferredColorScheme(scheme: DriftColorScheme): Modifier =
    this.then(PreferredColorSchemeModifier(scheme))

val darkMode = DriftColorScheme.Dark
val lightMode = DriftColorScheme.Light

// ---------------------------
// Custom toolbar renderer
// ---------------------------
@Composable
private fun CustomToolbarSurface(
    modifier: Modifier = Modifier,
    navController: DriftNavController,
    toolbarItems: List<ToolbarEntry>,
    navTitle: String?,
    hideBackButton: Boolean
) {
    val fg = LocalToolbarForeground.current
    val bg = LocalToolbarBackground.current
    val elev = LocalToolbarElevation.current
    val paddingFromChild = LocalToolbarContentPadding.current

    // Use the provided modifier directly on the Surface. IMPORTANT: the modifier should include width/height/clip.
    // If the modifier is empty, caller (NavigationStack) will pass a default full-width modifier.
    Surface(
        modifier = modifier,
        color = bg ?: MaterialTheme.colorScheme.surface,
        tonalElevation = elev ?: 0.dp,
        shadowElevation = elev ?: 0.dp
    ) {
        // Row fills the *Surface* area (fillMaxSize()) so children are constrained to the toolbar size.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (paddingFromChild != null)
                        Modifier.padding(horizontal = paddingFromChild)
                    else Modifier.padding(horizontal = 12.dp)
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // LEADING
            Box(
                modifier = Modifier.wrapContentWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                val leading = toolbarItems.firstOrNull { it.placement == ToolbarPlacement.Leading }

                if (leading != null) {
                    leading.content()
                } else if (navController.canPop() && !hideBackButton) {
                    Text(
                        "< Back",
                        Modifier
                            .padding(start = 8.dp)
                            .onTapGesture { navController.pop() }
                            .font(system(16, bold))
                            .foregroundStyle(fg ?: driftColors.text)
                    )
                }
            }

            // CENTER
            Box(
                modifier = Modifier
                    .weight(1f)          // <-- only center takes remaining space
                    .wrapContentWidth(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                if (navTitle != null) {
                    Text(navTitle, Modifier.font(system(18, semibold)))
                } else {
                    toolbarItems.firstOrNull { it.placement == ToolbarPlacement.Center }
                        ?.content?.invoke()
                }
            }

            // TRAILING
            Row(
                modifier = Modifier.wrapContentWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val trailing = toolbarItems.filter { it.placement == ToolbarPlacement.Trailing }
                trailing.forEach { it.content() }
            }
        }

    }
}

// ---------------------------
// NavigationStack (uses CustomToolbarSurface)
// ---------------------------
val LocalSheetState = compositionLocalOf<SheetState> {
    error("SheetState not provided.")
}



@Composable
fun SheetHost(sheetState: SheetState, sheetModifier: SheetModifier?) {
    val isPresented by sheetState.isPresented
    val sheetContent by sheetState.content

    // Track if sheet should be visible (for animation)
    var isVisible by remember { mutableStateOf(false) }

    // Update visibility when isPresented changes
    LaunchedEffect(isPresented) {
        if (isPresented) {
            isVisible = true
        } else {
            // Delay hiding to allow dismiss animation
            delay(300) // Match animation duration
            isVisible = false
        }
    }

    if (!isVisible || sheetContent == null || sheetModifier == null) {
        return
    }

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }

    // Calculate detent heights as FRACTIONS (0.0 to 1.0)
    val detentFractions = remember(sheetModifier.detents) {
        sheetModifier.detents.map { detent ->
            when (detent) {
                is SheetDetent.Fraction -> detent.fraction
                SheetDetent.Large -> 0.95f
            }
        }.sorted()
    }

    val initialFraction = remember(sheetModifier.initialDetent) {
        when (val initial = sheetModifier.initialDetent) {
            is SheetDetent.Fraction -> initial.fraction
            SheetDetent.Large -> 0.95f
        }
    }

    var targetFraction by remember { mutableStateOf(initialFraction) }
    var dragOffsetPx by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    // Reset to initial fraction when sheet opens
    LaunchedEffect(isPresented) {
        if (isPresented) {
            targetFraction = initialFraction
            dragOffsetPx = 0f
        } else {
            // Animate to 0 when dismissed
            targetFraction = 0f
        }
    }

    // Smooth tween animation instead of spring
    val animatedFraction by animateFloatAsState(
        targetValue = targetFraction,
        animationSpec = tween(
            durationMillis = 300,
            easing = androidx.compose.animation.core.FastOutSlowInEasing
        ),
        label = "sheet_fraction"
    )

    fun snapToNearestDetent() {
        isDragging = false
        val dragFraction = dragOffsetPx / screenHeightPx
        val currentFraction = (targetFraction + dragFraction).coerceIn(0f, 1f)

        val nearest = detentFractions.minByOrNull {
            kotlin.math.abs(it - currentFraction)
        } ?: detentFractions.first()

        // Dismiss if dragged below threshold
        if (currentFraction < detentFractions.first() * 0.6f && sheetModifier.allowDismiss) {
            sheetState.isPresented.value = false
        } else {
            targetFraction = nearest
        }

        dragOffsetPx = 0f
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Scrim
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    if (sheetModifier.allowDismiss) {
                        sheetState.isPresented.value = false
                    }
                }
        )

        // Calculate actual height - use drag offset directly when dragging
        val displayFraction = if (isDragging) {
            (animatedFraction + (dragOffsetPx / screenHeightPx)).coerceIn(0f, 1f)
        } else {
            animatedFraction.coerceIn(0f, 1f)
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(displayFraction)
                .align(Alignment.BottomCenter)
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragStart = {
                            isDragging = true
                        },
                        onDragEnd = {
                            snapToNearestDetent()
                        },
                        onDragCancel = {
                            isDragging = false
                            dragOffsetPx = 0f
                        },
                        onVerticalDrag = { _, dragAmount ->
                            // Negative drag = upward = increase height
                            dragOffsetPx = (dragOffsetPx - dragAmount).coerceIn(
                                -screenHeightPx * 0.5f,
                                screenHeightPx * 0.5f
                            )
                        }
                    )
                },
            shape = RoundedCornerShape(
                topStart = sheetModifier.cornerRadius.dp,
                topEnd = sheetModifier.cornerRadius.dp
            ),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            shadowElevation = 16.dp
        ) {
            Column(Modifier.fillMaxWidth()) {
                // Grabber
                if (sheetModifier.showGrabber) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color.Gray.copy(alpha = 0.4f))
                        )
                    }
                }

                // Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    sheetContent?.invoke()
                }
            }
        }
    }
}
// ---------------------------
// Sheet detent model
// ---------------------------
sealed class SheetDetent {
    data class Fraction(val fraction: Float) : SheetDetent()
    object Large : SheetDetent()
    companion object
}

// Allow direct Double → Fraction detent
val Double.detent get() = SheetDetent.Fraction(this.toFloat())

// Allow direct Float → Fraction detent
val Float.detent get() = SheetDetent.Fraction(this)

// Allow direct Int (%) → Fraction detent
val Int.percentDetent get() = SheetDetent.Fraction(this / 100f)


val SheetDetent.Companion.large get() = Large
fun SheetDetent.Companion.fraction(value: Double) = SheetDetent.Fraction(value.toFloat())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationStack(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val navStack = remember { mutableStateListOf<@Composable () -> Unit>() }
    val navController = remember { DriftNavController(navStack) }
    val currentScreen = navStack.lastOrNull()

    var navTitle: String? = null
    modifier.foldIn(Unit) { _, el ->
        if (el is NavigationTitleModifier) navTitle = el.title
        Unit
    }

    var hideBackButton = false
    modifier.foldIn(Unit) { _, el ->
        if (el is BackButtonHiddenModifier) hideBackButton = el.hidden
        Unit
    }

    var overrideScheme: DriftColorScheme? = null
    modifier.foldIn(Unit) { _, el ->
        if (el is PreferredColorSchemeModifier) overrideScheme = el.scheme
        Unit
    }

    var sheetModifier: SheetModifier? = null
    modifier.foldIn(Unit) { _, el ->
        if (el is SheetModifier) sheetModifier = el
        Unit
    }

    var navToolbarFg: Color? = null
    var navToolbarBg: Color? = null
    var navToolbarElev: Dp? = null
    var navToolbarPadding: Dp? = null

    modifier.foldIn(Unit) { _, el ->
        if (el is ToolbarStyleModifier) {
            if (el.foreground != null) navToolbarFg = el.foreground
            if (el.background != null) navToolbarBg = el.background
            if (el.elevation != null) navToolbarElev = el.elevation
            if (el.contentPadding != null) navToolbarPadding = el.contentPadding
        }
        Unit
    }

    val isDark = when (overrideScheme) {
        DriftColorScheme.Dark -> true
        DriftColorScheme.Light -> false
        null -> isSystemInDarkTheme()
    }

    val colors = if (isDark) {
        darkColorScheme(
            primary = Color(0xFF88B4B5),
            onPrimary = Color.Black,
            background = Color(0xFF121212),
            onBackground = Color.White,
            surface = Color(0xFF1E1E1E),
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF567779),
            onPrimary = Color.White,
            background = Color(0xFFF7F3F5),
            onBackground = Color.Black,
            surface = Color.White,
        )
    }

    MaterialTheme(colorScheme = colors) {
        val toolbarItems = remember { mutableStateListOf<ToolbarEntry>() }
        val toolbarLayoutState = remember { mutableStateOf<Modifier?>(null) }

        // Create internal sheet state that syncs with external
        val internalSheetState = remember {
            SheetState(
                isPresented = mutableStateOf(false),
                content = mutableStateOf(null)
            )
        }

        // BIDIRECTIONAL SYNC LOGIC
        if (sheetModifier != null) {
            val externalState = sheetModifier.isPresented
            val externalSetter = externalState.getSetter()

            // --- 1. External → Internal (Sheet is opened/closed by developer action)
            LaunchedEffect(externalState.value) {
                internalSheetState.isPresented.value = externalState.value
                if (externalState.value) {
                    internalSheetState.content.value = sheetModifier.content
                }
            }

            // --- 2. Internal → External (Sheet is dismissed by user swipe/scrim tap)
            val internalIsPresented by internalSheetState.isPresented
            LaunchedEffect(internalIsPresented) {
                if (!internalIsPresented && externalState.value) {
                    externalSetter(false)
                }
            }
        }

        CompositionLocalProvider(
            LocalToolbarState provides toolbarItems,
            LocalNavController provides navController,
            LocalToolbarLayoutState provides toolbarLayoutState
        ) {
            CompositionLocalProvider(
                LocalToolbarForeground provides navToolbarFg,
                LocalToolbarBackground provides navToolbarBg,
                LocalToolbarElevation provides navToolbarElev,
                LocalToolbarContentPadding provides navToolbarPadding
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets(0, 0, 0, 0))
                ) {
                    val shouldShowToolbar = toolbarItems.isNotEmpty() ||
                            (navController.canPop() && !hideBackButton)

                    if (shouldShowToolbar) {
                        val layoutModifier = toolbarLayoutState.value ?: Modifier.fillMaxWidth()
                        CustomToolbarSurface(
                            modifier = layoutModifier,
                            navController = navController,
                            toolbarItems = toolbarItems,
                            navTitle = navTitle,
                            hideBackButton = hideBackButton
                        )
                    }

                    Box(Modifier.fillMaxSize()) {
                        if (currentScreen == null) content()
                        else currentScreen.invoke()
                    }
                }
            }
        }

        // Render sheet if present
        SheetHost(internalSheetState, sheetModifier)

        DisposableEffect(Unit) {
            onDispose {
                toolbarItems.clear()
                navStack.clear()
            }
        }
    }
}

//------------------------------------------------------------------------------------------------
// MARK: Toolbar Below  (REPLACE END)
//------------------------------------------------------------------------------------------------


//------------------------------------------------------------------------------------------------
// ScrollView
//------------------------------------------------------------------------------------------------
@Composable
fun ScrollView(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        content = content
    )
}

//------------------------------------------------------------------------------------------------
// onTapGesture (helper)
//------------------------------------------------------------------------------------------------
fun Modifier.onTapGesture(action: () -> Unit): Modifier =
    composed {
        Modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) {
            action()
        }
    }

// DoubleTap Gesture

fun Modifier.onDoubleTap(action: () -> Unit): Modifier =
    this.then(
        Modifier.pointerInput(Unit) { // 'Unit' is used as the key to restart the listener
            detectTapGestures(onDoubleTap = { action() })
        }
    )

// Triple tap Gesture


fun Modifier.onTripleTap(action: () -> Unit): Modifier =
    this.then(
        Modifier.composed {
            // State to track the tap count and the last tap time
            val tapData = remember { mutableStateOf(TapCountData(0, 0L)) }
            val resetTime = 500L // Maximum time gap between taps (0.5 seconds)

            Modifier.pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        val currentTime = System.currentTimeMillis()
                        val currentCount = tapData.value.count

                        val newCount: Int

                        // Check if the time since the last tap is within the reset threshold
                        if (currentTime - tapData.value.lastTapTime < resetTime) {
                            newCount = currentCount + 1
                        } else {
                            // First tap or too much time passed; start count from 1
                            newCount = 1
                        }

                        // Update the state
                        tapData.value = TapCountData(newCount, currentTime)

                        // Check for completion
                        if (newCount == 3) {
                            action()
                            // Immediately reset the count after execution
                            tapData.value = TapCountData(0, 0L)
                        }
                    }
                    // We intentionally leave onDoubleTap empty here since we only care about the count
                )
            }
        }
    )

// In Layout.kt (Add near other gesture modifiers)

fun Modifier.onHold(action: () -> Unit): Modifier =
    this.then(
        Modifier.pointerInput(Unit) {
            detectTapGestures(
                // Maps directly to SwiftUI's onLongPressGesture
                onLongPress = { action() }
            )
        }
    )



fun Modifier.untilHold(
    onPress: (() -> Unit)? = null, // Action that runs when the screen is pressed down
    onRelease: (() -> Unit)? = null // Action that runs when the finger is lifted
): Modifier =
    this.then(
        // Use raw pointerInput to gain access to the low-level events required for onRelease
        Modifier.pointerInput(Unit) {
            // This lambda runs inside the PointerInputScope
            // We use coroutineScope to access coroutine builders (like launch) and provide a fixed scope
            coroutineScope {
                detectTapGestures(
                    // Maps to onPress (finger down)
                    onPress = {
                        onPress?.invoke()
                        // Use a try-finally block to ensure onRelease is called after the press is finished
                        try {
                            // Wait for the finger to be released
                            awaitRelease()
                        } finally {
                            // This code runs when the pointer is lifted (released)
                            onRelease?.invoke()
                        }
                    }
                    // We must leave all other callbacks (onTap, onDoubleTap, etc.) null here
                    // to ensure this scope dominates the press/release cycle.
                )
            }
        }
    )

// STEPPER:
@Composable
fun Stepper(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange = 0..100,
    step: Int = 1,
    modifier: Modifier = Modifier,
    label: @Composable RowScope.() -> Unit
) {
    // --- READ STYLING ---
    var styleButtonColor: Color? = null
    var styleButtonForeground: Color? = null
    var styleValueColor: Color? = null

    modifier.foldIn(Unit) { _, el ->
        if (el is StepperStyleModifier) {
            styleButtonColor = el.buttonColor
            styleButtonForeground = el.buttonForegroundColor
            styleValueColor = el.valueColor
        }
        Unit
    }

    // --- FINAL VALUES ---
    val finalButtonColor = styleButtonColor ?: driftColors.accent
    val finalButtonFg = styleButtonForeground ?: Color.white
    val finalValueColor = styleValueColor ?: driftColors.text

    // Base button styling
    val buttonBaseModifier = Modifier
        .size(32.dp)
        .cornerRadius(4)
        .background(finalButtonColor)
        .composed {
            Modifier.clickable(onClick = { /* no-op */ })
        }

    // Center text modifier
    val centerValueModifier = Modifier
        .width(30.dp)
        .wrapContentHeight(Alignment.CenterVertically)
        .padding(horizontal = 2.dp)
        .foregroundStyle(finalValueColor)
        .font(system(size = 16))
        .wrapContentSize(Alignment.Center)

    // --- LAYOUT ---
    Row(
        modifier = modifier
            .wrapContentHeight()
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        // LABEL
        Row(
            modifier = Modifier.weight(1f).wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            label()
        }

        // - BUTTON
        Button(
            action = {
                val newValue = (value - step).coerceAtLeast(range.first)
                onValueChange(newValue)
            },
            modifier = buttonBaseModifier
        ) {
            Text("-", Modifier.foregroundStyle(finalButtonFg))
        }

        // VALUE
        Text(
            text = "$value",
            modifier = centerValueModifier
        )

        // + BUTTON
        Button(
            action = {
                val newValue = (value + step).coerceAtMost(range.last)
                onValueChange(newValue)
            },
            modifier = buttonBaseModifier
        ) {
            Text("+", Modifier.foregroundStyle(finalButtonFg))
        }
    }
}
// NOTE: The Stepper overload (State<Int>) remains correct as it calls this primary function.


@Composable
fun Stepper(
    value: State<Int>, // Accepts the State delegate
    range: IntRange = 0..100,
    step: Int = 1,
    modifier: Modifier = Modifier,
    label: @Composable RowScope.() -> Unit
) {
    val b = value.binding()
    Stepper(
        value = b.value,
        onValueChange = b.set, // The setter handles the two-way sync
        range = range,
        step = step,
        modifier = modifier,
        label = label
    )
}
