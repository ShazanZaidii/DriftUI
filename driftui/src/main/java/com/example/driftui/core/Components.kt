package com.example.driftui.core

import android.annotation.SuppressLint
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image as ComposeImage
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider as MaterialDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider as MaterialSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text as MaterialText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

// =============================================================================================
// STATUS BAR
// =============================================================================================

@Composable
fun StatusBar(style: DriftColorScheme) {
    val view = LocalView.current
    val context = LocalContext.current

    if (!view.isInEditMode) {
        SideEffect {
            val activity = findActivity(context)
            activity.enableEdgeToEdge(
                statusBarStyle = if (style == darkMode) {
                    SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
                } else {
                    SystemBarStyle.light(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT
                    )
                }
            )
        }
    }
}

// =============================================================================================
// ALIGNMENT EXTRACTOR
// =============================================================================================

@Composable
internal fun Modifier.getTextAlignment(): Pair<TextAlign, Alignment> {
    var alignMod: TextAlignmentModifier? = null
    this.foldIn(Unit) { _, element ->
        if (element is TextAlignmentModifier) alignMod = element
        Unit
    }

    val x = alignMod?.x ?: -1f
    val y = alignMod?.y ?: 0f

    val textAlign = when {
        x < -0.2f -> TextAlign.Start
        x > 0.2f  -> TextAlign.End
        else      -> TextAlign.Center
    }

    val alignment = BiasAlignment(x, y)
    return textAlign to alignment
}

// =============================================================================================
// SHAPES
// =============================================================================================

@Composable
internal fun Modifier.getForegroundColor(): Color? {
    var chosenColor: Color? = null
    this.foldIn(Unit) { _, element ->
        if (element is ForegroundColorModifier) chosenColor = element.color
        Unit
    }
    return chosenColor
}

@Composable
fun Rectangle(width: Int, height: Int, modifier: Modifier = Modifier) {
    val fgColor = modifier.getForegroundColor() ?: driftColors.text
    Box(
        modifier = modifier.applyShadowIfNeeded()
            .size(width.dp, height.dp)
            .clip(RectangleShape)
            .background(fgColor)
    )
}

@Composable
fun Circle(radius: Int, modifier: Modifier = Modifier) {
    val fgColor = modifier.getForegroundColor() ?: driftColors.text
    Box(
        modifier = modifier.applyShadowIfNeeded()
            .size(radius.dp * 2)
            .clip(CircleShape)
            .background(fgColor)
    )
}

@Composable
fun Capsule(width: Int, height: Int, modifier: Modifier = Modifier) {
    val fgColor = modifier.getForegroundColor() ?: driftColors.text
    Box(
        modifier = modifier.applyShadowIfNeeded()
            .size(width.dp, height.dp)
            .clip(RoundedCornerShape(percent = 50))
            .background(fgColor)
    )
}

@Composable
fun RoundedRectangle(width: Number, height: Number, cornerRadius: Number, modifier: Modifier = Modifier) {
    val fgColor = modifier.getForegroundColor() ?: driftColors.text
    Box(
        modifier = modifier.applyShadowIfNeeded()
            .size(width.toFloat().dp, height.toFloat().dp)
            .clip(RoundedCornerShape(cornerRadius.toFloat().dp))
            .background(fgColor)
    )
}

val TriangleShape = GenericShape { size, _ ->
    moveTo(size.width / 2f, 0f)
    lineTo(size.width, size.height)
    lineTo(0f, size.height)
    close()
}

@Composable
fun Triangle(width: Int, height: Int, modifier: Modifier = Modifier) {
    val fgColor = modifier.getForegroundColor() ?: driftColors.text
    Box(
        modifier = modifier.applyShadowIfNeeded()
            .size(width.dp, height.dp)
            .clip(TriangleShape)
            .background(fgColor)
    )
}

val ArrowShape = GenericShape { size, _ ->
    val width = size.width
    val height = size.height
    val shaftRatio = 0.35f
    val headRatio = 0.4f
    val shaftTop = (height * (1 - shaftRatio)) / 2
    val shaftBottom = (height * (1 + shaftRatio)) / 2
    val headStart = width * (1 - headRatio)

    moveTo(0f, shaftTop)
    lineTo(headStart, shaftTop)
    lineTo(headStart, 0f)
    lineTo(width, height / 2)
    lineTo(headStart, height)
    lineTo(headStart, shaftBottom)
    lineTo(0f, shaftBottom)
    close()
}

@Composable
fun Arrow(width: Int, height: Int, modifier: Modifier = Modifier) {
    val fgColor = modifier.getForegroundColor() ?: driftColors.text
    Box(
        modifier = modifier
            .size(width.dp, height.dp)
            .clip(ArrowShape)
            .background(fgColor)
    )
}

val FourPointStar = GenericShape { size, _ ->
    val w = size.width
    val h = size.height
    val cx = w / 2f
    val cy = h / 2f
    moveTo(cx, 0f)
    quadraticBezierTo(cx, cy, w, cy)
    quadraticBezierTo(cx, cy, cx, h)
    quadraticBezierTo(cx, cy, 0f, cy)
    quadraticBezierTo(cx, cy, cx, 0f)
    close()
}

@Composable
fun FourPointStar(width: Number, height: Number, modifier: Modifier = Modifier) {
    val fgColor = modifier.getForegroundColor() ?: driftColors.text
    Box(
        modifier = modifier.applyShadowIfNeeded()
            .size(width.toFloat().dp, height.toFloat().dp)
            .clip(FourPointStar)
            .background(fgColor)
    )
}

// --- SHAPE HELPER ---
fun Circle(): Shape = CircleShape
fun Capsule(): Shape = RoundedCornerShape(percent = 50)
fun RoundedRectangle(radius: Int): Shape = RoundedCornerShape(radius.dp)
fun Triangle(): Shape = TriangleShape
fun Arrow(): Shape = ArrowShape
fun FourPointStar(): Shape = FourPointStar

// =============================================================================================
// IMAGE & INPUTS
// =============================================================================================

@Composable
fun Image(name: String, modifier: Modifier = Modifier, contentScale: ContentScale = ContentScale.Fit) {
    val ctx = LocalContext.current
    val id = ctx.resources.getIdentifier(name, "drawable", ctx.packageName)
    val customColor = modifier.getForegroundColor()
    val colorFilter = customColor?.let { ColorFilter.tint(it) }

    ComposeImage(
        painter = painterResource(id),
        contentDescription = null,
        modifier = modifier,
        contentScale = contentScale,
        colorFilter = colorFilter
    )
}

// =============================================================================================
// TOGGLE, SLIDER, SCROLLVIEW
// =============================================================================================

@Composable
fun Toggle(
    isOn: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    label: (@Composable () -> Unit)? = null
) {
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

    val thumbOffset by animateDpAsState(
        targetValue = if (isOn) 22.dp else 2.dp,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 300f),
        label = "thumb_offset"
    )

    val thumbScale by animateFloatAsState(
        targetValue = if (isOn) 1.06f else 1.0f,
        animationSpec = tween(120),
        label = "thumb_scale"
    )

    val trackColor by animateColorAsState(
        targetValue = if (isOn) (styleOn ?: driftColors.accent) else (styleOff ?: driftColors.fieldBackground),
        animationSpec = tween(200),
        label = "track_color"
    )

    val trackShadow = if (isOn) 8.dp else 1.dp
    val trackShadowColor = if (isOn) driftColors.accent.copy(alpha = 0.35f) else Color.Black.copy(alpha = 0.12f)

    val thumbColor by animateColorAsState(
        targetValue = styleThumb ?: if (isOn) Color.White else Color.White.copy(alpha = 0.85f),
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

    Row(
        modifier = modifier
            .wrapContentHeight()
            .then(Modifier.onTapGesture {
                pressed = true
                onToggle(!isOn)
                pressed = false
            }),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (label != null) label()

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
            Box(
                modifier = Modifier
                    .offset(x = thumbOffset)
                    .size(26.dp)
                    .graphicsLayer {
                        scaleX = thumbScale
                        scaleY = thumbScale
                        shadowElevation = thumbShadow.toPx()
                    }
                    .clip(CircleShape)
                    .background(thumbColor)
            )
        }
    }
}

@Composable
fun Toggle(value: MutableState<Boolean>, modifier: Modifier = Modifier, label: @Composable () -> Unit) {
    Toggle(isOn = value.value, onToggle = { value.value = it }, modifier = modifier, label = label)
}

@Composable
fun Toggle(label: String, value: MutableState<Boolean>, modifier: Modifier = Modifier) {
    Toggle(isOn = value.value, onToggle = { value.value = it }, modifier = modifier) {
        MaterialText(label)
    }
}

// ---------------------------------------------------------------------------------------------
// SLIDER
// ---------------------------------------------------------------------------------------------

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Slider(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange = 0..100,
    modifier: Modifier = Modifier,
    step: Int = 0,
    thickness: Number = 6,
    trackColor: GaugeColor = gaugeColor(Color.DarkGray),
    fillColor: GaugeColor = gaugeColor(Color.Red),
    tracker: (@Composable () -> Unit)? = null
) {
    if (step > 0) {
        val rangeLength = range.last - range.first
        val stepsCount = if (rangeLength > 0) (rangeLength / step) - 1 else 0

        val activeColor = when (fillColor) {
            is GaugeColor.Solid -> fillColor.color
            is GaugeColor.Gradient -> fillColor.gradient.colors.firstOrNull() ?: driftColors.accent
        }
        val inactiveColor = when (trackColor) {
            is GaugeColor.Solid -> trackColor.color
            is GaugeColor.Gradient -> trackColor.gradient.colors.firstOrNull() ?: driftColors.fieldBackground
        }

        MaterialSlider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.roundToInt()) },
            valueRange = range.first.toFloat()..range.last.toFloat(),
            steps = stepsCount,
            modifier = modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                activeTrackColor = activeColor,
                inactiveTrackColor = inactiveColor,
                thumbColor = activeColor,
                activeTickColor = activeColor.copy(alpha = 0.5f),
                inactiveTickColor = inactiveColor.copy(alpha = 0.5f)
            ),
            thumb = {
                if (tracker != null) {
                    tracker()
                } else {
                    SliderDefaults.Thumb(
                        interactionSource = remember { MutableInteractionSource() },
                        colors = SliderDefaults.colors(thumbColor = activeColor)
                    )
                }
            }
        )
    } else {
        val progress = normalize(value, range)
        BoxWithConstraints(
            modifier = modifier
                .pointerInput(range) {
                    detectHorizontalDragGestures { change, _ ->
                        val totalWidth = size.width.toFloat()
                        val x = change.position.x
                        val newProgress = (x / totalWidth).coerceIn(0f, 1f)
                        val rangeSpan = range.last - range.first
                        val newValue = (range.first + (rangeSpan * newProgress)).roundToInt()
                        if (newValue != value) onValueChange(newValue)
                    }
                }
                .pointerInput(range) {
                    detectTapGestures { offset ->
                        val totalWidth = size.width.toFloat()
                        val x = offset.x
                        val newProgress = (x / totalWidth).coerceIn(0f, 1f)
                        val rangeSpan = range.last - range.first
                        val newValue = (range.first + (rangeSpan * newProgress)).roundToInt()
                        onValueChange(newValue)
                    }
                }
        ) {
            RenderLinearGauge(
                progress = progress,
                modifier = Modifier.fillMaxWidth(),
                thickness = thickness.toDp(),
                trackColor = trackColor,
                fillColor = fillColor,
                tracker = tracker
            )
        }
    }
}

@Composable
fun Slider(
    value: MutableState<Int>,
    range: IntRange = 0..100,
    modifier: Modifier = Modifier,
    step: Int = 0,
    thickness: Number = 6,
    trackColor: GaugeColor = gaugeColor(Color.DarkGray),
    fillColor: GaugeColor = gaugeColor(Color.Red),
    tracker: (@Composable () -> Unit)? = null
) {
    Slider(
        value = value.value,
        onValueChange = { value.value = it },
        range = range,
        modifier = modifier,
        step = step,
        thickness = thickness,
        trackColor = trackColor,
        fillColor = fillColor,
        tracker = tracker
    )
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Slider(
    value: Number,
    onValueChange: (Double) -> Unit,
    range: ClosedFloatingPointRange<Double> = 0.0..1.0,
    modifier: Modifier = Modifier,
    step: Double = 0.0,
    thickness: Number = 6,
    trackColor: GaugeColor = gaugeColor(Color.DarkGray),
    fillColor: GaugeColor = gaugeColor(Color.Red),
    tracker: (@Composable () -> Unit)? = null
) {
    if (step > 0.0) {
        val rangeSpan = range.endInclusive - range.start
        val stepsCount = if (step > 0) ((rangeSpan / step) - 1).toInt().coerceAtLeast(0) else 0

        val activeColor = when (fillColor) {
            is GaugeColor.Solid -> fillColor.color
            is GaugeColor.Gradient -> fillColor.gradient.colors.firstOrNull() ?: driftColors.accent
        }
        val inactiveColor = when (trackColor) {
            is GaugeColor.Solid -> trackColor.color
            is GaugeColor.Gradient -> trackColor.gradient.colors.firstOrNull() ?: driftColors.fieldBackground
        }

        MaterialSlider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toDouble()) },
            valueRange = range.start.toFloat()..range.endInclusive.toFloat(),
            steps = stepsCount,
            modifier = modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                activeTrackColor = activeColor,
                inactiveTrackColor = inactiveColor,
                thumbColor = activeColor,
                activeTickColor = activeColor.copy(alpha = 0.5f),
                inactiveTickColor = inactiveColor.copy(alpha = 0.5f)
            ),
            thumb = {
                if (tracker != null) {
                    tracker()
                } else {
                    SliderDefaults.Thumb(
                        interactionSource = remember { MutableInteractionSource() },
                        colors = SliderDefaults.colors(thumbColor = activeColor)
                    )
                }
            }
        )
    } else {
        val progress = normalize(value, range)
        BoxWithConstraints(
            modifier = modifier
                .pointerInput(range) {
                    detectHorizontalDragGestures { change, _ ->
                        val totalWidth = size.width.toFloat()
                        val x = change.position.x
                        val newProgress = (x / totalWidth).coerceIn(0f, 1f)
                        val rangeSpan = range.endInclusive - range.start
                        val newValue = range.start + (rangeSpan * newProgress)
                        onValueChange(newValue)
                    }
                }
                .pointerInput(range) {
                    detectTapGestures { offset ->
                        val totalWidth = size.width.toFloat()
                        val x = offset.x
                        val newProgress = (x / totalWidth).coerceIn(0f, 1f)
                        val rangeSpan = range.endInclusive - range.start
                        val newValue = range.start + (rangeSpan * newProgress)
                        onValueChange(newValue)
                    }
                }
        ) {
            RenderLinearGauge(
                progress = progress,
                modifier = Modifier.fillMaxWidth(),
                thickness = thickness.toDp(),
                trackColor = trackColor,
                fillColor = fillColor,
                tracker = tracker
            )
        }
    }
}



// =============================================================================================
// CANVAS TOOLS
// =============================================================================================

@Composable
fun PenTool(
    path: com.example.driftui.core.Path? = null,
    color: Color = Color.Black,
    width: Float = 3f,
    smooth: Boolean = true,
    modifier: Modifier = Modifier,
    onDrawStart: () -> Unit = {},
    onDrawEnd: () -> Unit = {}
) {
    val lastPointAdded = remember { mutableStateOf(Offset.Zero) }
    val internal = remember { com.example.driftui.core.Path() }
    val actualPath = path ?: internal

    Canvas(
        modifier = modifier
            .pointerInput(actualPath, color, width) {
                awaitEachGesture {
                    val canvasSize = size
                    val down = awaitFirstDown()

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
        lastPointAdded.value

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

enum class EraserType { Line, Area }

@Composable
fun EraserTool(
    path: com.example.driftui.core.Path,
    type: EraserType = EraserType.Area,
    radius: Float = 30f,
    modifier: Modifier = Modifier,
    onDrawStart: () -> Unit = {},
    onDrawEnd: () -> Unit = {},
    onPathChanged: () -> Unit = {}
) {
    val forceRecompose = remember { mutableStateOf(0) }
    val actualPath = path

    val currentEraserPos = remember { mutableStateOf<Offset?>(null) }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(actualPath, type, radius) {
                awaitEachGesture {
                    val canvasSize = size
                    val down = awaitFirstDown()

                    onDrawStart()

                    var change: PointerInputChange? = down

                    while (change != null && change.pressed) {
                        val currentPos = change.position
                        val clamped = clampOffset(currentPos, canvasSize)
                        currentEraserPos.value = clamped

                        val removed = if (type == EraserType.Line) {
                            actualPath.removeStrokeAt(clamped, radius)
                        } else {
                            actualPath.eraseAreaAt(clamped, radius)
                        }

                        if (removed) {
                            forceRecompose.value += 1
                            onPathChanged()
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

        currentEraserPos.value?.let { pos ->
            drawCircle(color = Color.Gray.copy(alpha = 0.2f), radius = radius, center = pos)
            drawCircle(color = Color.Black.copy(alpha = 0.1f), radius = radius, center = pos, style = Stroke(1f))
        }
    }
}

internal fun clampOffset(offset: Offset, size: IntSize): Offset {
    val x = offset.x
    val y = offset.y
    val clampedX = max(0f, min(x, size.width.toFloat()))
    val clampedY = max(0f, min(y, size.height.toFloat()))
    return Offset(clampedX, clampedY)
}

@Composable
fun DriftCanvas(controller: DriftDrawController, modifier: Modifier = Modifier) {
    val autoSnapshot = { controller.snapshot() }

    Box(modifier = modifier) {
        PenTool(
            path = controller.path,
            color = controller.color,
            width = controller.width,
            onDrawStart = autoSnapshot,
            onDrawEnd = {},
            modifier = Modifier.fillMaxSize()
        )

        if (controller.eraser) {
            EraserTool(
                path = controller.path,
                type = controller.eraserType,
                onDrawStart = autoSnapshot,
                onDrawEnd = {},
                onPathChanged = { controller.path = controller.path },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

// =================================================================================================
// GAUGES
// =================================================================================================

internal fun normalize(value: Number, range: ClosedFloatingPointRange<Double>): Float {
    val min = range.start
    val max = range.endInclusive
    val v = value.toDouble()
    if (max <= min) return 0f
    return ((v - min) / (max - min)).coerceIn(0.0, 1.0).toFloat()
}

internal fun normalize(value: Number, range: IntRange): Float {
    val min = range.first.toDouble()
    val max = range.last.toDouble()
    val v = value.toDouble()
    if (max <= min) return 0f
    return ((v - min) / (max - min)).coerceIn(0.0, 1.0).toFloat()
}

@Composable
internal fun Number.toDp(): Dp = with(LocalDensity.current) { this@toDp.toFloat().dp }

sealed interface GaugeColor {
    data class Solid(val color: Color) : GaugeColor
    data class Gradient(val gradient: GradientColor) : GaugeColor
}

fun gaugeColor(color: Color): GaugeColor = GaugeColor.Solid(color)
fun gaugeColor(gradient: GradientColor): GaugeColor = GaugeColor.Gradient(gradient)

internal fun GradientColor.toBrush(): Brush = Brush.linearGradient(this.colors)
internal fun GaugeColor.toBrush(): Brush = when (this) {
    is GaugeColor.Solid -> SolidColor(color)
    is GaugeColor.Gradient -> gradient.toBrush()
}

@Composable
internal fun RenderCircularGauge(
    progress: Float,
    modifier: Modifier,
    radius: Dp,
    thickness: Dp,
    startAngle: Float,
    sweepAngle: Float,
    trackColor: GaugeColor,
    fillColor: GaugeColor,
    tracker: (@Composable () -> Unit)?,
    content: (@Composable () -> Unit)?
) {
    val size = radius * 2
    val angle = startAngle + sweepAngle * progress

    val strokePx: Float
    val radiusPx: Float
    with(LocalDensity.current) {
        strokePx = thickness.toPx()
        radiusPx = radius.toPx()
    }

    val trackBrush = trackColor.toBrush()
    val fillBrush = fillColor.toBrush()

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val stroke = Stroke(width = strokePx, cap = StrokeCap.Round)
            drawArc(brush = trackBrush, startAngle = startAngle, sweepAngle = sweepAngle, useCenter = false, style = stroke)
            drawArc(brush = fillBrush, startAngle = startAngle, sweepAngle = sweepAngle * progress, useCenter = false, style = stroke)
        }
        if (tracker != null) {
            val rad = Math.toRadians(angle.toDouble())
            val x = cos(rad).toFloat() * radiusPx
            val y = sin(rad).toFloat() * radiusPx
            Box(Modifier.offset { IntOffset(x.roundToInt(), y.roundToInt()) }) { tracker() }
        }
        content?.invoke()
    }
}

@Composable
internal fun RenderLinearGauge(
    progress: Float,
    modifier: Modifier,
    thickness: Dp,
    trackColor: GaugeColor,
    fillColor: GaugeColor,
    tracker: (@Composable () -> Unit)?
) {
    val trackBrush = trackColor.toBrush()
    val fillBrush = fillColor.toBrush()

    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.CenterStart) {
        val totalWidth = constraints.maxWidth.toFloat()
        Box(modifier = Modifier.fillMaxWidth().height(thickness).clip(RoundedCornerShape(thickness / 2)).background(trackBrush))
        Box(modifier = Modifier.fillMaxWidth(progress).height(thickness).clip(RoundedCornerShape(thickness / 2)).background(fillBrush))

        if (tracker != null) {
            Layout(content = tracker, modifier = Modifier.zIndex(1f)) { measurables, constraints ->
                val placeable = measurables.first().measure(constraints.copy(minWidth = 0, minHeight = 0))
                val centerX = totalWidth * progress
                val x = (centerX - (placeable.width / 2)).roundToInt()
                layout(placeable.width, placeable.height) { placeable.placeRelative(x, 0) }
            }
        }
    }
}

@Composable
fun LinearGauge(
    value: Number,
    range: ClosedFloatingPointRange<Double> = 0.0..1.0,
    modifier: Modifier = Modifier,
    thickness: Number = 6,
    trackColor: GaugeColor = gaugeColor(Color.DarkGray),
    fillColor: GaugeColor = gaugeColor(Color.Red),
    tracker: (@Composable () -> Unit)? = null
) {
    RenderLinearGauge(normalize(value, range), modifier, thickness.toDp(), trackColor, fillColor, tracker)
}

@Composable
fun LinearGauge(
    value: Number,
    range: IntRange,
    modifier: Modifier = Modifier,
    thickness: Number = 6,
    trackColor: GaugeColor = gaugeColor(Color.DarkGray),
    fillColor: GaugeColor = gaugeColor(Color.Red),
    tracker: (@Composable () -> Unit)? = null
) {
    RenderLinearGauge(normalize(value, range), modifier, thickness.toDp(), trackColor, fillColor, tracker)
}

@Composable
fun CircularGauge(
    value: Number,
    range: ClosedFloatingPointRange<Double> = 0.0..1.0,
    modifier: Modifier = Modifier,
    radius: Number = 44,
    thickness: Number = 6,
    trackColor: GaugeColor = gaugeColor(Color.DarkGray),
    fillColor: GaugeColor = gaugeColor(Color.Red),
    content: (@Composable () -> Unit)? = null
) {
    RenderCircularGauge(normalize(value, range), modifier, radius.toDp(), thickness.toDp(), -90f, 360f, trackColor, fillColor, null, content)
}

@Composable
fun CircularGauge(
    value: Number,
    range: IntRange,
    modifier: Modifier = Modifier,
    radius: Number = 44,
    thickness: Number = 6,
    trackColor: GaugeColor = gaugeColor(Color.DarkGray),
    fillColor: GaugeColor = gaugeColor(Color.Red),
    content: (@Composable () -> Unit)? = null
) {
    RenderCircularGauge(normalize(value, range), modifier, radius.toDp(), thickness.toDp(), -90f, 360f, trackColor, fillColor, null, content)
}

@Composable
fun AccessoryCircularGauge(
    value: Number,
    range: ClosedFloatingPointRange<Double> = 0.0..1.0,
    modifier: Modifier = Modifier,
    radius: Number = 44,
    thickness: Number = 6,
    trackColor: GaugeColor = gaugeColor(Color.DarkGray),
    fillColor: GaugeColor = gaugeColor(Color.Red),
    tracker: (@Composable () -> Unit)? = null,
    content: (@Composable () -> Unit)? = null
) {
    RenderCircularGauge(normalize(value, range), modifier, radius.toDp(), thickness.toDp(), 135f, 270f, trackColor, fillColor, tracker, content)
}

@Composable
fun AccessoryCircularGauge(
    value: Number,
    range: IntRange,
    modifier: Modifier = Modifier,
    radius: Number = 44,
    thickness: Number = 6,
    trackColor: GaugeColor = gaugeColor(Color.DarkGray),
    fillColor: GaugeColor = gaugeColor(Color.Red),
    tracker: (@Composable () -> Unit)? = null,
    content: (@Composable () -> Unit)? = null
) {
    RenderCircularGauge(normalize(value, range), modifier, radius.toDp(), thickness.toDp(), 135f, 270f, trackColor, fillColor, tracker, content)
}

// =============================================================================================
// MISC COMPONENTS
// =============================================================================================

@Composable
fun ColorPicker(
    selectedColor: MutableState<Color>,
    colors: List<Color> = listOf(
        Color.Black, Color.Red, Color.Blue, Color.Green,
        Color.Yellow, Color.Magenta, Color.Cyan, Color.Gray
    ),
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier.horizontalScroll(rememberScrollState())
    ) {
        colors.forEach { color ->
            val isSelected = color == selectedColor.value
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(
                        width = if (isSelected) 3.dp else 0.dp,
                        color = if (isSelected) driftColors.text else Color.Transparent,
                        shape = CircleShape
                    )
                    .onTapGesture {
                        selectedColor.value = color
                    }
            )
        }
    }
}

// =============================================================================================
// COLOR HELPERS
// =============================================================================================

fun Color.darker(factor: Float = 0.2f): Color {
    val r = (this.red * (1 - factor)).coerceIn(0f, 1f)
    val g = (this.green * (1 - factor)).coerceIn(0f, 1f)
    val b = (this.blue * (1 - factor)).coerceIn(0f, 1f)
    return Color(r, g, b, this.alpha)
}

fun Color.lighter(factor: Float = 0.2f): Color {
    val r = (this.red + (1 - this.red) * factor).coerceIn(0f, 1f)
    val g = (this.green + (1 - this.green) * factor).coerceIn(0f, 1f)
    val b = (this.blue + (1 - this.blue) * factor).coerceIn(0f, 1f)
    return Color(r, g, b, this.alpha)
}

fun rgb(r: Int, g: Int, b: Int): Color = Color(r, g, b)
fun rgba(r: Int, g: Int, b: Int, a: Double): Color = Color(r, g, b, (a * 255).toInt())

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
            else -> Color.Black
        }
    } catch (e: Exception) {
        Color.Black
    }
}

