package com.example.driftui.core
//This file is Components.kt
// --- IMPORTS ---

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image as ComposeImage
import androidx.compose.foundation.background as foundationBackground
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider as MaterialDivider
import androidx.compose.material3.Slider as MaterialSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text as MaterialText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.foundation.Canvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.Canvas as FoundationCanvas // Use alias for standard Canvas
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.geometry.Offset
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntSize
import kotlin.math.max
import kotlin.math.min
import androidx.compose.foundation.horizontalScroll
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.runtime.MutableState

import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
// --- CUSTOM IMPORTS ---

//-----------------------------------------
//ALIGNMENT EXTRACTOR:
//-----------------------------------------
@Composable
private fun Modifier.getTextAlignment(): Pair<TextAlign, Alignment> {
    var alignMod: TextAlignmentModifier? = null
    this.foldIn(Unit) { _, element ->
        if (element is TextAlignmentModifier) {
            alignMod = element
        }
        Unit
    }

    val x = alignMod?.x ?: -1f // Default X: -1 (Left/Start)
    val y = alignMod?.y ?: 0f  // Default Y: 0 (Vertically Centered)

    // Calculate TextAlign (Horizontal)
    val textAlign = when {
        x < -0.2f -> TextAlign.Start
        x > 0.2f  -> TextAlign.End
        else      -> TextAlign.Center
    }

    // Calculate Box Alignment (Vertical + Horizontal positioning)
    val alignment = BiasAlignment(x, y)

    return textAlign to alignment
}
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
        modifier = modifier.applyShadowIfNeeded()
            .size(width.dp, height.dp)
            .clip(RectangleShape)
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
        modifier = modifier.applyShadowIfNeeded()
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
        modifier = modifier.applyShadowIfNeeded()
            .size(width.dp, height.dp)
            .clip(RoundedCornerShape(percent = 50))
            .foundationBackground(fgColor)
    )
}

@Composable
fun RoundedRectangle(
    width: Number,
    height: Number,
    cornerRadius: Number,
    modifier: Modifier = Modifier
) {
    val fgColor = modifier.getForegroundColor() ?: driftColors.text

    Box(
        modifier = modifier.applyShadowIfNeeded()
            .size(width.toFloat().dp, height.toFloat().dp)
            .clip(RoundedCornerShape(cornerRadius.toFloat().dp))
            .foundationBackground(fgColor)
    )
}

// --- TRIANGLE SHAPE DEFINITION ---
// Points up by default (Top-Center -> Bottom-Right -> Bottom-Left)
val TriangleShape = GenericShape { size, _ ->
    moveTo(size.width / 2f, 0f)      // 1. Move to Top Center
    lineTo(size.width, size.height)  // 2. Draw to Bottom Right
    lineTo(0f, size.height)          // 3. Draw to Bottom Left
    close()                          // 4. Close path (back to top)
}

// --- COMPOSABLE COMPONENT ---
@Composable
fun Triangle(
    width: Int,
    height: Int,
    modifier: Modifier = Modifier
) {
    val fgColor = modifier.getForegroundColor() ?: driftColors.text

    Box(
        modifier = modifier.applyShadowIfNeeded()
            .size(width.dp, height.dp)
            .clip(TriangleShape)
            .foundationBackground(fgColor)
    )
}

// --- ARROW SHAPE DEFINITION ---
// Points Right by default
val ArrowShape = GenericShape { size, _ ->
    val width = size.width
    val height = size.height

    val shaftRatio = 0.35f // Thickness of the shaft (35% of height)
    val headRatio = 0.4f   // Length of the head (40% of width)

    val shaftTop = (height * (1 - shaftRatio)) / 2
    val shaftBottom = (height * (1 + shaftRatio)) / 2
    val headStart = width * (1 - headRatio)

    // Draw the path
    moveTo(0f, shaftTop)              // 1. Top-Left of Shaft
    lineTo(headStart, shaftTop)       // 2. Shaft meets Head (Top)
    lineTo(headStart, 0f)             // 3. Top Wing of Head
    lineTo(width, height / 2)         // 4. Tip of Arrow
    lineTo(headStart, height)         // 5. Bottom Wing of Head
    lineTo(headStart, shaftBottom)    // 6. Shaft meets Head (Bottom)
    lineTo(0f, shaftBottom)           // 7. Bottom-Left of Shaft
    close()
}

// --- COMPOSABLE COMPONENT ---
@Composable
fun Arrow(
    width: Int,
    height: Int,
    modifier: Modifier = Modifier
) {
    val fgColor = modifier.getForegroundColor() ?: driftColors.text

    Box(
        modifier = modifier
            .size(width.dp, height.dp)
            .clip(ArrowShape)
            .foundationBackground(fgColor)
    )
}

// --- SHAPE HELPER ---


// --- SHAPE HELPER (For modifiers like .clipShape(Triangle())) ---

fun Circle(): Shape = CircleShape
fun Capsule(): Shape = RoundedCornerShape(percent = 50)
fun RoundedRectangle(radius: Int): Shape = RoundedCornerShape(radius.dp)

fun Triangle(): Shape = TriangleShape

fun Arrow(): Shape = ArrowShape
// ---------------------------------------------------------------------------------------------
// TEXT & IMAGE
// ---------------------------------------------------------------------------------------------

@Composable
fun Text(text: String, modifier: Modifier = Modifier) {
    var chosenFont: SystemFont? = null
    var chosenColor: Color.Companion? = null

    // 1. Extract Color/Font from Modifier chain
    var finalColor = Color.Unspecified
    modifier.foldIn(Unit) { _, element ->
        if (element is ForegroundColorModifier) {
            finalColor = element.color
        }
        Unit
    }

    // 2. Extract Font
    var fontStyle: SystemFont? = null
    modifier.foldIn(Unit) { _, element ->
        if (element is FontModifier) fontStyle = element.font
        Unit
    }

    val style = TextStyle(
        fontSize = fontStyle?.size?.sp ?: TextStyle.Default.fontSize,
        fontWeight = fontStyle?.weight ?: TextStyle.Default.fontWeight,
        color = finalColor.takeUnless { it == Color.Unspecified } ?: driftColors.text
    )

    // 3. Wrap in Box to center content by default
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        MaterialText(
            text = text,
            style = style,
        )
    }
}

//@Composable
//fun Image(
//    name: String,
//    modifier: Modifier = Modifier,
//    contentScale: ContentScale = ContentScale.Fit
//) {
//    val ctx = LocalContext.current
//    val id = ctx.resources.getIdentifier(name, "drawable", ctx.packageName)
//
//    ComposeImage(
//        painter = painterResource(id),
//        contentDescription = null,
//        modifier = modifier,
//        contentScale = contentScale
//    )
//}

@Composable
fun Image(
    name: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit
) {
    val ctx = LocalContext.current
    val id = ctx.resources.getIdentifier(name, "drawable", ctx.packageName)

    // 1. Extract the color from your custom modifier
    val customColor = modifier.getForegroundColor()

    // 2. Create a ColorFilter if a color was found
    val colorFilter = customColor?.let { ColorFilter.tint(it) }

    ComposeImage(
        painter = painterResource(id),
        contentDescription = null,
        modifier = modifier,
        contentScale = contentScale,
        colorFilter = colorFilter // 3. Apply the tint here
    )
}



// ---------------------------------------------------------------------------------------------
// INPUTS (TextField, SecureField)
// ---------------------------------------------------------------------------------------------

@Composable
fun TextField(
    placeholder: String,
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // 1. Extract Styling
    val customColor = modifier.getForegroundColor()
    val (textAlign, contentAlign) = modifier.getTextAlignment()

    // 2. Extract Font & Placeholder Style
    var fontStyle: SystemFont? = null
    var phStyle: PlaceholderStyleModifier? = null // <--- NEW

    modifier.foldIn(Unit) { _, element ->
        if (element is FontModifier) fontStyle = element.font
        if (element is PlaceholderStyleModifier) phStyle = element // <--- NEW
        Unit
    }

    val inputColor = customColor ?: driftColors.text

    // Existing fallback: If no specific placeholder color, use customColor or Gray
    val defaultPhColor = customColor ?: Color.Gray

    // Default to 16.sp if no font modifier is provided
    val fontSize = fontStyle?.size?.sp ?: 16.sp
    val fontWeight = fontStyle?.weight ?: FontWeight.Normal

    // Resolve Final Placeholder Specs
    val finalPhColor = phStyle?.color ?: defaultPhColor
    val finalPhFontSize = phStyle?.font?.size?.sp ?: fontSize
    val finalPhWeight = phStyle?.font?.weight ?: fontWeight

    BasicTextField(
        value = text,
        onValueChange = onTextChange,
        modifier = modifier, // Keep modifier here for layout/padding
        textStyle = TextStyle.Default.copy(
            fontSize = fontSize,
            fontWeight = fontWeight,
            color = inputColor,
            textAlign = textAlign // Apply horizontal text alignment
        ),
        decorationBox = { inner ->
            Box(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                contentAlignment = contentAlign // Apply Frame Alignment (x, y)
            ) {
                if (text.isEmpty()) {
                    MaterialText(
                        text = placeholder,
                        style = TextStyle.Default.copy(
                            fontSize = finalPhFontSize, // <--- UPDATED
                            fontWeight = finalPhWeight, // <--- UPDATED
                            color = finalPhColor,       // <--- UPDATED
                            textAlign = textAlign
                        ),
                        modifier = Modifier.fillMaxWidth() // Ensure placeholder fills width to respect alignment
                    )
                }
                inner()
            }
        }
    )
}

// Overload 1: For standard Compose MutableState
@Composable
fun TextField(
    placeholder: String,
    value: MutableState<String>,
    modifier: Modifier = Modifier
) {
    TextField(
        placeholder = placeholder,
        text = value.value,
        onTextChange = { value.value = it },
        modifier = modifier
    )
}

// Overload 2: For YOUR Custom 'State' class
@Composable
fun TextField(
    placeholder: String,
    value: State<String>,
    modifier: Modifier = Modifier
) {
    TextField(
        placeholder = placeholder,
        text = value.value,
        onTextChange = { value.set(it) },
        modifier = modifier
    )
}

// ---------------------------------------------------------------------------------------------
// SECURE FIELD (Fixed & Complete)
// ---------------------------------------------------------------------------------------------

@Composable
fun SecureField(
    placeholder: String,
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // 1. Extract Styling (Color & Alignment)
    val customColor = modifier.getForegroundColor()
    val (textAlign, contentAlign) = modifier.getTextAlignment()

    // 2. Extract Font & Placeholder Style
    var fontStyle: SystemFont? = null
    var phStyle: PlaceholderStyleModifier? = null // <--- NEW

    modifier.foldIn(Unit) { _, element ->
        if (element is FontModifier) fontStyle = element.font
        if (element is PlaceholderStyleModifier) phStyle = element // <--- NEW
        Unit
    }

    // 3. Resolve Colors & Fonts
    val inputColor = customColor ?: driftColors.text

    // Existing fallback
    val defaultPhColor = customColor ?: Color.Gray

    val fontSize = fontStyle?.size?.sp ?: 16.sp
    val fontWeight = fontStyle?.weight ?: FontWeight.Normal

    // Resolve Final Placeholder Specs
    val finalPhColor = phStyle?.color ?: defaultPhColor
    val finalPhFontSize = phStyle?.font?.size?.sp ?: fontSize
    val finalPhWeight = phStyle?.font?.weight ?: fontWeight

    BasicTextField(
        value = text,
        onValueChange = onTextChange,
        modifier = modifier,
        textStyle = TextStyle.Default.copy(
            fontSize = fontSize,
            fontWeight = fontWeight,
            color = inputColor,
            textAlign = textAlign
        ),
        visualTransformation = PasswordVisualTransformation(),
        decorationBox = { inner ->
            Box(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                contentAlignment = contentAlign
            ) {
                if (text.isEmpty()) {
                    MaterialText(
                        text = placeholder,
                        style = TextStyle.Default.copy(
                            fontSize = finalPhFontSize, // <--- UPDATED
                            fontWeight = finalPhWeight, // <--- UPDATED
                            color = finalPhColor,       // <--- UPDATED
                            textAlign = textAlign
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                inner()
            }
        }
    )
}

// Overload 1: For standard Compose MutableState
@Composable
fun SecureField(
    placeholder: String,
    value: MutableState<String>,
    modifier: Modifier = Modifier
) {
    SecureField(
        placeholder = placeholder,
        text = value.value,
        onTextChange = { value.value = it },
        modifier = modifier
    )
}

// Overload 2: For YOUR Custom 'State' class
@Composable
fun SecureField(
    placeholder: String,
    value: State<String>,
    modifier: Modifier = Modifier
) {
    SecureField(
        placeholder = placeholder,
        text = value.value,
        onTextChange = { value.set(it) },
        modifier = modifier
    )
}


// ---------------------------------------------------------------------------------------------
// BUTTON, DIVIDER, LIST
// ---------------------------------------------------------------------------------------------

@Composable
fun Button(
    action: () -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier.applyShadowIfNeeded().clickable(
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
    thickness: Int = 1,
    width: Number? = null // Accepts Int, Double, Float
) {
    // 1. Start with the passed modifier (containing padding, offset, etc.)
    var finalModifier = modifier

    // 2. Append the width constraint if provided
    if (width != null) {
        finalModifier = finalModifier.width(width.toFloat().dp)
    }

    // 3. Render
    MaterialDivider(
        modifier = finalModifier,
        color = color,
        thickness = thickness.dp
    )
}

// --- INTELLIGENT LISTS (THE FIX) ---

// 1. Standalone List (Normal behavior)
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

// 2. VStack Context List (Reactive Spacing)
@Composable
fun ColumnScope.List(
    alignment: Alignment = center,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    // FIX: weight(1f, fill = false) allows the list to SHRINK if content is small
    Box(Modifier.weight(1f, fill = false), contentAlignment = alignment) {
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

// 3. Standalone Data List (Normal behavior)
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

// 4. VStack Context Data List (Reactive Spacing)
@Composable
fun <T> ColumnScope.List(
    items: List<T>,
    alignment: Alignment = center,
    modifier: Modifier = Modifier,
    rowContent: @Composable (T) -> Unit
) {
    LazyColumn(
        // FIX: weight(1f, fill = false) allows the list to SHRINK if content is small
        modifier = modifier.weight(1f, fill = false).fillMaxWidth().padding(horizontal = 8),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        itemsIndexed(items) { index, item ->
            rowContent(item)
            if (index < items.size - 1) Divider()
        }
    }
}


// ---------------------------------------------------------------------------------------------
// TOGGLE, SLIDER, SCROLLVIEW
// ---------------------------------------------------------------------------------------------

@Composable
fun Toggle(
    isOn: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    label: (@Composable () -> Unit)? = null
) {
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
        label = "thumb_offset"
    )

    val thumbScale by animateFloatAsState(
        targetValue = if (isOn) 1.06f else 1.0f,
        animationSpec = tween(120),
        label = "thumb_scale"
    )

    val trackColor by animateColorAsState(
        targetValue = if (isOn)
            (styleOn ?: driftColors.accent)
        else
            (styleOff ?: driftColors.fieldBackground),
        animationSpec = tween(200),
        label = "track_color"
    )

    val trackShadow = if (isOn) 8.dp else 1.dp
    val trackShadowColor = if (isOn)
        driftColors.accent.copy(alpha = 0.35f)
    else
        Color.Black.copy(alpha = 0.12f)

    val thumbColor by animateColorAsState(
        targetValue = styleThumb
            ?: if (isOn) Color.White
            else Color.White.copy(alpha = 0.85f),
        animationSpec = tween(180),
        label = "thumb_color"
    )

    val thumbShadow = if (isOn) 10.dp else 0.dp

    var pressed by remember { mutableStateOf(false) }
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.92f else 1.0f,
        animationSpec = tween(80),
        label = "press_scale"
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

@Composable
fun Toggle(
    value: State<Boolean>, // RESTORED
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

@Composable
fun Toggle(
    label: String, // String label for convenience
    value: State<Boolean>, // RESTORED
    modifier: Modifier = Modifier
) {
    val b = value.binding()
    Toggle(isOn = b.value, onToggle = b.set, modifier = modifier) {
        // Renders the string label using the Text composable
        Text(label)
    }
}

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
    MaterialSlider(
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
    )
}

@Composable
fun Slider(
    value: State<Int>, // RESTORED
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

//ScrollView::

enum class ScrollDirection {
    Vertical,
    Horizontal
}

//class OnTop(val action: () -> Unit)
//class OnScroll(val action: () -> Unit)
//
//fun onTop(action: () -> Unit) = OnTop(action)
//fun onScroll(action: () -> Unit) = OnScroll(action)


@Composable
fun ScrollView(
    modifier: Modifier = Modifier,
    direction: ScrollDirection = ScrollDirection.Vertical,
    onTop: (() -> Unit)? = null,
    onScroll: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val scrollState = rememberScrollState()
    var wasAtTop by remember { mutableStateOf(true) }

    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.value }
            .collect { offset ->
                val isAtTop = offset == 0

                if (isAtTop && !wasAtTop) {
                    wasAtTop = true
                    onTop?.invoke()
                }

                if (!isAtTop && wasAtTop) {
                    wasAtTop = false
                    onScroll?.invoke()
                }
            }
    }

    val scrollModifier = when (direction) {
        ScrollDirection.Vertical -> Modifier.verticalScroll(scrollState)
        ScrollDirection.Horizontal -> Modifier.horizontalScroll(scrollState)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .then(scrollModifier),
        content = content
    )
}





@Composable
fun Canvas(
    modifier: Modifier = Modifier,
    content: DrawScope.() -> Unit
) {
    FoundationCanvas(
        modifier = modifier,
        onDraw = content
    )
}

@Composable
fun PenTool(
    path: State<com.example.driftui.core.Path>? = null,
    color: Color = Color.Black,
    width: Float = 3f,
    smooth: Boolean = true,
    modifier: Modifier = Modifier,
    onDrawStart: () -> Unit = {}, // New callback for UNDO
    onDrawEnd: () -> Unit = {}
) {
    val lastPointAdded = remember { mutableStateOf(Offset.Zero) }
    val internal = remember { com.example.driftui.core.Path() }
    val actualPath = path?.value ?: internal

    Canvas(
        modifier = modifier
            // FIX 1: Add color/width to keys so it updates immediately
            .pointerInput(actualPath, color, width) {
                awaitEachGesture {
                    val canvasSize = size
                    val down = awaitFirstDown()

                    // FIX 2: Trigger snapshot BEFORE drawing starts
                    onDrawStart()

                    val clampedStart = clampOffset(down.position, canvasSize)
                    actualPath.start(clampedStart, color, width)

                    lastPointAdded.value = clampedStart
                    var change: PointerInputChange

                    do {
                        val event = awaitPointerEvent()
                        change = event.changes.first()

                        if (change.pressed) {
                            val clampedPosition = clampOffset(change.position, canvasSize)
                            actualPath.lineTo(clampedPosition)
                            lastPointAdded.value = clampedPosition
                            change.consumePositionChange()
                        }
                    } while (change.pressed)

                    if (smooth) actualPath.smoothAllStrokes()
                    lastPointAdded.value = Offset.Zero
                    actualPath.finish()
                    onDrawEnd()
                }
            }
    ) {
        lastPointAdded.value // force recompose

        // DRAW EACH STROKE INDIVIDUALLY
        actualPath.strokes.forEach { strokeData ->
            if (strokeData.points.isNotEmpty()) {
                val composePath = Path()
                composePath.moveTo(strokeData.points.first().x, strokeData.points.first().y)

                for (i in 1 until strokeData.points.size) {
                    val p = strokeData.points[i]
                    composePath.lineTo(p.x, p.y)
                }

                drawPath(
                    path = composePath,
                    color = strokeData.color,
                    style = Stroke(
                        width = strokeData.width,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }
    }
}


// In Components.kt

// 1. Define the types clearly
enum class EraserType { Line, Area }

@Composable
fun EraserTool(
    path: State<com.example.driftui.core.Path>,
    type: EraserType = EraserType.Area, // Default, but you can change it
    radius: Float = 30f,
    modifier: Modifier = Modifier,
    onDrawStart: () -> Unit = {}, // New callback
    onDrawEnd: () -> Unit = {}
) {
    val forceRecompose = remember { mutableStateOf(0) }
    val actualPath = path.value

    // State to track the cursor position (for visual feedback)
    val currentEraserPos = remember { mutableStateOf<Offset?>(null) }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            // Add keys to ensure it stays fresh
            .pointerInput(actualPath, type, radius) {
                awaitEachGesture {
                    val canvasSize = size
                    val down = awaitFirstDown()

                    // Trigger snapshot BEFORE erasing
                    onDrawStart()

                    var change: PointerInputChange? = down

                    while (change != null && change.pressed) {
                        val currentPos = change.position
                        val clamped = clampOffset(currentPos, canvasSize)

                        // Update cursor position
                        currentEraserPos.value = clamped

                        val removed = if (type == EraserType.Line) {
                            actualPath.removeStrokeAt(clamped, radius)
                        } else {
                            actualPath.eraseAreaAt(clamped, radius)
                        }

                        if (removed) {
                            forceRecompose.value += 1
                            path.set(actualPath)
                        }

                        change.consumePositionChange()
                        val event = awaitPointerEvent()
                        change = event.changes.firstOrNull()
                    }
                    currentEraserPos.value = null
                    onDrawEnd()
                }
            }
    ) {
        forceRecompose.value

        // Draw visual cursor
        currentEraserPos.value?.let { pos ->
            drawCircle(
                color = Color.Gray.copy(alpha = 0.2f),
                radius = radius,
                center = pos
            )
            drawCircle(
                color = Color.Black.copy(alpha = 0.1f),
                radius = radius,
                center = pos,
                style = Stroke(1f)
            )
        }
    }
}

/**
 * Utility function to ensure an Offset remains within the bounds of the Canvas size.
 */
private fun clampOffset(offset: Offset, size: IntSize): Offset {
    val x = offset.x
    val y = offset.y
    val clampedX = max(0f, min(x, size.width.toFloat()))
    val clampedY = max(0f, min(y, size.height.toFloat()))
    return Offset(clampedX, clampedY)
}

// Add to Components.kt

@Composable
fun ColorPicker(
    selectedColor: State<Color>,
    colors: List<Color> = listOf(
        Color.black, Color.red, Color.blue, Color.green,
        Color.yellow, Color.magenta, Color.cyan, Color.gray
    ),
    modifier: Modifier = Modifier
) {
    val b = selectedColor.binding()

    HStack(spacing = 10, modifier = modifier.horizontalScroll(rememberScrollState())) {
        colors.forEach { color ->
            val isSelected = color == b.value

            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(Circle())
                    .background(color)
                    .border(
                        width = if (isSelected) 3 else 0,
                        color = if (isSelected) driftColors.text else Color.Transparent
                    )
                    .onTapGesture {
                        b.set(color)
                    }
            )
        }
    }
}

// ... (Your existing PenTool, EraserTool, etc.) ...

/**
 * The Magic Wrapper.
 * Handles layering, tools, and auto-saving history.
 */
@Composable
fun DriftCanvas(
    controller: DriftDrawController,
    modifier: Modifier = Modifier
) {
    // FIX 3: Bind snapshot to onDrawStart instead of onDrawEnd
    val autoSnapshot = { controller.snapshot() }

    ZStack(modifier = modifier) {
        // LAYER 1: PEN
        // It listens to the controller's path and color
        PenTool(
            path = controller.path,
            color = controller.color.value,
            width = controller.width.value,
            onDrawStart = autoSnapshot, // SNAPSHOT HERE
            onDrawEnd = {}, // No op
            modifier = Modifier.fillMaxSize()
        )

        // LAYER 2: ERASER
        // Auto-appears based on controller.eraser state
        if (controller.eraser.value) {
            EraserTool(
                path = controller.path,
                type = controller.eraserType.value,
                onDrawStart = autoSnapshot, // SNAPSHOT HERE
                onDrawEnd = {}, // No op
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

// =============================================================================================
// COLOR EXTENSIONS (Darker / Lighter)
// =============================================================================================

/**
 * Returns a darker version of the color.
 * @param factor 0.0 to 1.0 (default 0.2). Higher is darker.
 */
fun Color.darker(factor: Float = 0.2f): Color {
    val r = (this.red * (1 - factor)).coerceIn(0f, 1f)
    val g = (this.green * (1 - factor)).coerceIn(0f, 1f)
    val b = (this.blue * (1 - factor)).coerceIn(0f, 1f)
    return Color(r, g, b, this.alpha)
}

/**
 * Returns a lighter version of the color.
 * @param factor 0.0 to 1.0 (default 0.2). Higher is lighter.
 */
fun Color.lighter(factor: Float = 0.2f): Color {
    val r = (this.red + (1 - this.red) * factor).coerceIn(0f, 1f)
    val g = (this.green + (1 - this.green) * factor).coerceIn(0f, 1f)
    val b = (this.blue + (1 - this.blue) * factor).coerceIn(0f, 1f)
    return Color(r, g, b, this.alpha)
}

// =============================================================================================
// COLOR HELPERS (RGB / RGBA / HEX)
// =============================================================================================

/**
 * Create a color using 0-255 integer values.
 * Usage: rgb(255, 0, 0) // Red
 */
fun rgb(r: Int, g: Int, b: Int): Color {
    return Color(r, g, b)
}

/**
 * Create a color using 0-255 RGB integers and a 0.0-1.0 Alpha double.
 * Usage: rgba(255, 0, 0, 0.5) // 50% Transparent Red
 */
fun rgba(r: Int, g: Int, b: Int, a: Double): Color {
    return Color(r, g, b, (a * 255).toInt())
}

/**
 * Create a color from a Hex string.
 * Usage: hex("#FF0000") or hex("FF0000")
 */
fun hex(hexString: String): Color {
    val cleanHex = hexString.removePrefix("#")
    return try {
        when (cleanHex.length) {
            6 -> {
                val r = cleanHex.substring(0, 2).toInt(16)
                val g = cleanHex.substring(2, 4).toInt(16)
                val b = cleanHex.substring(4, 6).toInt(16)
                Color(r, g, b)
            }
            8 -> {
                val a = cleanHex.substring(0, 2).toInt(16)
                val r = cleanHex.substring(2, 4).toInt(16)
                val g = cleanHex.substring(4, 6).toInt(16)
                val b = cleanHex.substring(6, 8).toInt(16)
                Color(r, g, b, a)
            }
            else -> Color.Black // Fallback for invalid hex
        }
    } catch (e: Exception) {
        Color.Black
    }
}