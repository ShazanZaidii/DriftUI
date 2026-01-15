package com.example.driftui.core

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import kotlin.math.sqrt
import androidx.compose.ui.platform.LocalContext
import androidx.core.util.rangeTo


enum class Screen {
    SmallPhone, // < 380dp
    Phone,      // 380dp - 600dp (Standard)
    Tablet,     // 600dp - 840dp
    Desktop     // > 840dp
}

@Composable
fun getScreenSizes(): Screen {
    val config = LocalConfiguration.current
    val width = config.screenWidthDp.dp

    return when {
        width < 380.dp -> Screen.SmallPhone
        width < 600.dp -> Screen.Phone  // Acts as your "StrictMediumPhone"
        width < 840.dp -> Screen.Tablet
        else -> Screen.Desktop
    }
}


object DriftScale {

    // Reference device you designed on
    const val REFERENCE_WIDTH = 390f
    const val REFERENCE_HEIGHT = 780f

    var widthScale = 1f
    var heightScale = 1f

    // Perceptual scale (for fonts, icons, radius)
    val visualScale: Float
        get() = sqrt(widthScale * heightScale)
}


// ---------------------------------------------------------------------------------------------
// GLOBAL ALIGNMENT VARIABLES (For easy access like 'top', 'leading')
// ---------------------------------------------------------------------------------------------

val top: Alignment = Alignment.TopCenter
val bottom: Alignment = Alignment.BottomCenter
val center: Alignment = Alignment.Center
val leading: Alignment = Alignment.CenterStart
val trailing: Alignment = Alignment.CenterEnd
val topLeading: Alignment = Alignment.TopStart
val topTrailing: Alignment = Alignment.TopEnd
val bottomLeading: Alignment = Alignment.BottomStart
val bottomTrailing: Alignment = Alignment.BottomEnd

// ---------------------------------------------------------------------------------------------
// SMART VSTACK & HSTACK
// ---------------------------------------------------------------------------------------------

/**
 * VStack now supports:
 * 1. Modifier First: VStack(padding(10)) { }
 * 2. Alignment First: VStack(leading) { }
 * 3. 2D/Vertical Overloads
 */

// 1. MAIN (Modifier First - Default)
@Composable
fun VStack(
    modifier: Modifier = Modifier,
    alignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    spacing: Int = 0,
    content: @Composable ColumnScope.() -> Unit
) {
    val modifierAlignment = modifier.getAlignment()

    val horizontalAlignment =
        modifierAlignment?.toHorizontal() ?: alignment

    val verticalArrangement =
        modifierAlignment?.toVertical()?.let {
            Arrangement.spacedBy(spacing.dp, it)
        } ?: Arrangement.spacedBy(spacing.dp)

    Column(
        modifier = modifier.fillMaxWidth().applyShadowIfNeeded(),
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = verticalArrangement,
        content = content
    )
}


// 2. CONVENIENCE (Alignment First - Supports VStack(leading))
@Composable
fun VStack(
    alignment: Alignment.Horizontal,
    spacing: Int = 0,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    VStack(modifier, alignment, spacing, content)
}

// 3. VERTICAL OVERLOAD (Handles .top, .bottom)
@Composable
fun VStack(
    alignment: Alignment.Vertical,
    spacing: Int = 0,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val modifierAlignment = modifier.getAlignment()

    val vertical =
        modifierAlignment?.toVertical() ?: alignment

    val horizontal =
        modifierAlignment?.toHorizontal() ?: Alignment.CenterHorizontally

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = horizontal,
        verticalArrangement = Arrangement.spacedBy(spacing.dp, vertical),
        content = content
    )
}


// 4. 2D ALIGNMENT OVERLOAD (Handles .topLeading, .center)
@Composable
fun VStack(
    alignment: Alignment,
    spacing: Int = 0,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val modifierAlignment = modifier.getAlignment()
    val effectiveAlignment = modifierAlignment ?: alignment

    val (vertArrangement, horizAlign) = effectiveAlignment.toColumnRules()

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = horizAlign,
        verticalArrangement =
            if (vertArrangement == Arrangement.Top)
                Arrangement.spacedBy(spacing.dp)
            else
                Arrangement.spacedBy(spacing.dp, effectiveAlignment.toVertical()),
        content = content
    )
}


// Add this to LayoutPrimitives.kt
@Composable
fun Block(
    modifier: Modifier = Modifier,
    spacing: Int = 0,
    content: @Composable ColumnScope.() -> Unit
) {
    // A standard column that doesn't force fillMaxWidth()
    androidx.compose.foundation.layout.Column(
        modifier = modifier, // Allows the block to be centered by a parent VStack
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(spacing.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.Start, // Default to leading text
        content = content
    )
}

// --- HSTACK OVERLOADS ---

// 1. MAIN (Modifier First - Default)
@Composable
fun HStack(
    modifier: Modifier = Modifier,
    alignment: Alignment.Vertical = Alignment.CenterVertically,
    spacing: Int = 0,
    content: @Composable RowScope.() -> Unit
) {
    val modifierAlignment = modifier.getAlignment()

    val verticalAlignment =
        modifierAlignment?.toVertical() ?: alignment

    val horizontalArrangement =
        modifierAlignment?.toHorizontal()?.let {
            Arrangement.spacedBy(spacing.dp, it)
        } ?: Arrangement.spacedBy(spacing.dp)

    Row(
        modifier = modifier.applyShadowIfNeeded(),
        verticalAlignment = verticalAlignment,
        horizontalArrangement = horizontalArrangement,
        content = content
    )
}


// 2. CONVENIENCE (Alignment First - Supports HStack(top))
@Composable
fun HStack(
    alignment: Alignment.Vertical,
    spacing: Int = 0,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    HStack(modifier, alignment, spacing, content)
}


// 3. HORIZONTAL OVERLOAD (Handles .leading, .trailing)
@Composable
fun HStack(
    alignment: Alignment.Horizontal,
    spacing: Int = 0,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val modifierAlignment = modifier.getAlignment()

    val horizontal =
        modifierAlignment?.toHorizontal() ?: alignment

    val vertical =
        modifierAlignment?.toVertical() ?: Alignment.CenterVertically

    Row(
        modifier = modifier,
        verticalAlignment = vertical,
        horizontalArrangement = Arrangement.spacedBy(spacing.dp, horizontal),
        content = content
    )
}


// 4. 2D ALIGNMENT OVERLOAD
@Composable
fun HStack(
    alignment: Alignment,
    spacing: Int = 0,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val modifierAlignment = modifier.getAlignment()
    val effectiveAlignment = modifierAlignment ?: alignment

    val (vertAlign, horizArrangement) = effectiveAlignment.toRowRules()

    Row(
        modifier = modifier,
        verticalAlignment = vertAlign,
        horizontalArrangement =
            if (horizArrangement == Arrangement.Start)
                Arrangement.spacedBy(spacing.dp)
            else
                Arrangement.spacedBy(spacing.dp, effectiveAlignment.toHorizontal()),
        content = content
    )
}



// ---------------------------------------------------------------------------------------------
// HELPERS (Logic to decode 2D Alignment into 1D rules)
// ---------------------------------------------------------------------------------------------

private fun Alignment.toColumnRules(): Pair<Arrangement.Vertical, Alignment.Horizontal> {
    return when (this) {
        Alignment.TopStart -> Arrangement.Top to Alignment.Start
        Alignment.TopCenter -> Arrangement.Top to Alignment.CenterHorizontally
        Alignment.TopEnd -> Arrangement.Top to Alignment.End
        Alignment.CenterStart -> Arrangement.Center to Alignment.Start
        Alignment.Center -> Arrangement.Center to Alignment.CenterHorizontally
        Alignment.CenterEnd -> Arrangement.Center to Alignment.End
        Alignment.BottomStart -> Arrangement.Bottom to Alignment.Start
        Alignment.BottomCenter -> Arrangement.Bottom to Alignment.CenterHorizontally
        Alignment.BottomEnd -> Arrangement.Bottom to Alignment.End
        else -> Arrangement.Top to Alignment.CenterHorizontally
    }
}

private fun Alignment.toRowRules(): Pair<Alignment.Vertical, Arrangement.Horizontal> {
    return when (this) {
        Alignment.TopStart -> Alignment.Top to Arrangement.Start
        Alignment.TopCenter -> Alignment.Top to Arrangement.Center
        Alignment.TopEnd -> Alignment.Top to Arrangement.End
        Alignment.CenterStart -> Alignment.CenterVertically to Arrangement.Start
        Alignment.Center -> Alignment.CenterVertically to Arrangement.Center
        Alignment.CenterEnd -> Alignment.CenterVertically to Arrangement.End
        Alignment.BottomStart -> Alignment.Bottom to Arrangement.Start
        Alignment.BottomCenter -> Alignment.Bottom to Arrangement.Center
        Alignment.BottomEnd -> Alignment.Bottom to Arrangement.End
        else -> Alignment.CenterVertically to Arrangement.Start
    }
}

private fun Alignment.toVertical(): Alignment.Vertical {
    return when(this) {
        Alignment.TopStart, Alignment.TopCenter, Alignment.TopEnd -> Alignment.Top
        Alignment.BottomStart, Alignment.BottomCenter, Alignment.BottomEnd -> Alignment.Bottom
        else -> Alignment.CenterVertically
    }
}

private fun Alignment.toHorizontal(): Alignment.Horizontal {
    return when(this) {
        Alignment.TopStart, Alignment.CenterStart, Alignment.BottomStart -> Alignment.Start
        Alignment.TopEnd, Alignment.CenterEnd, Alignment.BottomEnd -> Alignment.End
        else -> Alignment.CenterHorizontally
    }
}


// ---------------------------------------------------------------------------------------------
// DRIFTVIEW & OTHERS
// ---------------------------------------------------------------------------------------------

@Composable
private fun BoxScope.ApplyAlignmentIfNeeded(
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    val alignment = modifier.getAlignment()

    if (alignment != null) {
        Box(
            modifier = Modifier.align(alignment)
        ) {
            content()
        }
    } else {
        content()
    }
}

@Composable
fun ZStack(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.applyShadowIfNeeded(),
        contentAlignment = contentAlignment
    ) {
        content()
    }
}

@Composable
fun Group(content: @Composable () -> Unit) {
    content()
}


@Composable
fun DriftView(
    modifier: Modifier = Modifier,
    blockBackgroundAudio: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val context = LocalContext.current
    DriftRegistry.context = context.applicationContext
    val lifecycleOwner = LocalLifecycleOwner.current
    val activity = context as? Activity
    DriftGlobals.currentActivity = activity

    val config = LocalConfiguration.current

    DriftScale.widthScale =
        config.screenWidthDp / DriftScale.REFERENCE_WIDTH

    DriftScale.heightScale =
        config.screenHeightDp / DriftScale.REFERENCE_HEIGHT


    LaunchedEffect(Unit) {
        DriftAudio.initialize(context)
        DriftHaptics.initialize(context)
        DriftStorage.initialize(context)
        DriftGlobals.applicationContext = context.applicationContext
        DriftNotificationEngine.prepareIfNeeded()
    }

    if (blockBackgroundAudio) {
        DisposableEffect(Unit) {
            DriftAudio.requestSilence(context)
            onDispose { DriftAudio.releaseSilence(context) }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) DriftAudio.onAppPause()
            else if (event == Lifecycle.Event.ON_RESUME) DriftAudio.onAppResume()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Box(modifier = modifier.fillMaxSize().background(driftColors.background),
        contentAlignment = Alignment.Center) {
        content()
        DriftToastHost()
    }
}

@Composable
fun ColumnScope.Spacer() = Spacer(Modifier.weight(1f))
@Composable
fun RowScope.Spacer() = Spacer(Modifier.weight(1f))
@Composable
fun Spacer(size: Int) = Spacer(Modifier.size(size.dp))
