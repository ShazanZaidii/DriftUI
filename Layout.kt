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


// convenience modifier for toolbar background specifically (keeps it explicit)
fun Modifier.toolbarBackground(color: Color) = this.then(BackgroundColorModifier(color))

// ---- toolbar styling support ----
data class ToolbarStyle(
    val background: Color? = null,
    val foreground: Color? = null
)

val LocalToolbarStyle = compositionLocalOf { ToolbarStyle() }


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


// --- Overload: State<Boolean>
@Composable
fun Toggle(
    label: String,
    value: State<Boolean>,
    modifier: Modifier = Modifier
) {
    val b = value.binding()
    Toggle(isOn = b.value, onToggle = b.set, modifier = modifier) {
        Text(label)
    }
}

// --- Overload: Binding<Boolean>
@Composable
fun Toggle(
    value: Binding<Boolean>,
    modifier: Modifier = Modifier,
    label: (@Composable () -> Unit)? = null
) {
    Toggle(
        isOn = value.value,
        onToggle = { value.set(it) },
        modifier = modifier,
        label = label
    )
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
// CRITICAL: it also writes the *entire* modifier passed to toolbar(...) into the shared LayoutState so
// NavigationStack's renderer (which lives above the toolbar in the tree) can use it to size the Surface.
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

// ---------------------------
// Navigation controller
// ---------------------------
class DriftNavController(private val stack: MutableList<@Composable () -> Unit>) {
    fun push(screen: @Composable () -> Unit) { stack.add(screen) }
    fun pop() { if (stack.isNotEmpty()) stack.removeLast() }
    fun clear() { stack.clear() }
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
    return { nav.dismiss() }
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationStack(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // --- Navigation state ---
    val navStack = remember { mutableStateListOf<@Composable () -> Unit>() }
    val navController = remember { DriftNavController(navStack) }
    val currentScreen = navStack.lastOrNull()

    // --- Read navigation title (modifier on this NavigationStack instance) ---
    var navTitle: String? = null
    modifier.foldIn(Unit) { _, el ->
        if (el is NavigationTitleModifier) navTitle = el.title
        Unit
    }

    // --- Read back button hidden ---
    var hideBackButton = false
    modifier.foldIn(Unit) { _, el ->
        if (el is BackButtonHiddenModifier) hideBackButton = el.hidden
        Unit
    }

    // --- Read preferredColorScheme ---
    var overrideScheme: DriftColorScheme? = null
    modifier.foldIn(Unit) { _, el ->
        if (el is PreferredColorSchemeModifier) overrideScheme = el.scheme
        Unit
    }

    // --- Read any toolbarStyle applied directly on NavigationStack (nav-level defaults) ---
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

        // A mutable state that toolbar() children will write their full layout modifier into.
        // NavigationStack reads this value when rendering the toolbar. This allows toolbar(...) (child)
        // to define exact height/width/clip, while NavigationStack (parent) renders it.
        val toolbarLayoutState = remember { mutableStateOf<Modifier?>(null) }

        // Provide both the toolbar registration list and the nav controller to children
        CompositionLocalProvider(
            LocalToolbarState provides toolbarItems,
            LocalNavController provides navController,
            LocalToolbarLayoutState provides toolbarLayoutState // parent provides state that child writes into
        ) {
            // Also provide the nav-level defaults for toolbar style — child toolbar() can override these
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
                )
                {
                    // decide whether toolbar should be shown:
                    val shouldShowToolbar = toolbarItems.isNotEmpty() || (navController.canPop() && !hideBackButton)

                    if (shouldShowToolbar) {
                        // Choose layout modifier: toolbar child may have set one into toolbarLayoutState.value.
                        // Priority: toolbar()'s full modifier (child) > fallback full-width modifier (default).
                        val layoutModifier = toolbarLayoutState.value ?: Modifier.fillMaxWidth()

                        // render our custom toolbar; allow dev to pass modifier to control height/clip/shape etc
                        CustomToolbarSurface(
                            modifier = layoutModifier,
                            navController = navController,
                            toolbarItems = toolbarItems,
                            navTitle = navTitle,
                            hideBackButton = hideBackButton
                        )
                    }

                    // content area (root or pushed screen)
                    Box(Modifier.fillMaxSize()) {
                        if (currentScreen == null) content()
                        else currentScreen.invoke()
                    }
                }
            }
        }

        // Clean up on composition leaving
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
