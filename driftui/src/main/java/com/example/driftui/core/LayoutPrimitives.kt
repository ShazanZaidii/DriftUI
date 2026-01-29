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
// ... imports ...
import androidx.compose.material3.* // Import Material3 for Scaffold
import androidx.compose.foundation.layout.padding as stdPadding
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
// SMART VSTACK & HSTACK (Default Fill Max Size)
// ---------------------------------------------------------------------------------------------

/**
 * VStack now supports:
 * 1. Modifier First: VStack(padding(10)) { }
 * 2. Alignment First: VStack(leading) { }
 * 3. 2D/Vertical Overloads
 * * FIX: Now prioritizes Modifier.alignment() over parameters.
 * * UPDATE: Now uses fillMaxSize() by default.
 */

// 1. MAIN (Modifier First - Default)
@Composable
fun VStack(
    modifier: Modifier = Modifier,
    alignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    spacing: Int = 0,
    content: @Composable ColumnScope.() -> Unit
) {
    // 1. EXTRACT: Get the raw alignment from the modifier
    val modifierAlign = modifier.getAlignment()

    // 2. RESOLVE HORIZONTAL (Cross Axis)
    val horizontalAlignment = modifierAlign?.toHorizontal() ?: alignment

    // 3. RESOLVE VERTICAL (Main Axis)
    val verticalArrangement = modifierAlign?.toVertical()?.let {
        Arrangement.spacedBy(spacing.dp, it)
    } ?: Arrangement.spacedBy(spacing.dp)

    Column(
        // Force fillMaxSize so alignment modifiers work across the whole screen
        modifier = modifier.fillMaxSize().applyShadowIfNeeded(),
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
    val finalMod = if (modifier.getAlignment() == null) modifier.alignment(Alignment.TopCenter) else modifier

    val modifierAlign = finalMod.getAlignment()
    val horizontal = modifierAlign?.toHorizontal() ?: Alignment.CenterHorizontally
    val vertical = (modifierAlign?.toVertical() ?: alignment).let { Arrangement.spacedBy(spacing.dp, it) }

    Column(
        modifier = finalMod.fillMaxSize().applyShadowIfNeeded(),
        horizontalAlignment = horizontal,
        verticalArrangement = vertical,
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
    val finalMod = if (modifier.getAlignment() == null) modifier.alignment(alignment) else modifier

    VStack(
        modifier = finalMod,
        spacing = spacing,
        content = content
    )
}


@Composable
fun Block(
    modifier: Modifier = Modifier,
    spacing: Int = 0,
    content: @Composable ColumnScope.() -> Unit
) {
    androidx.compose.foundation.layout.Column(
        modifier = modifier,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(spacing.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.Start,
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
    val modifierAlign = modifier.getAlignment()

    // 1. RESOLVE VERTICAL (Cross Axis)
    val verticalAlignment = modifierAlign?.toVertical() ?: alignment

    // 2. RESOLVE HORIZONTAL (Main Axis)
    val horizontalArrangement = modifierAlign?.toHorizontal()?.let {
        Arrangement.spacedBy(spacing.dp, it)
    } ?: Arrangement.spacedBy(spacing.dp)

    Row(
        modifier = modifier.fillMaxSize().applyShadowIfNeeded(),
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
    val finalMod = if (modifier.getAlignment() == null) modifier.alignment(Alignment.CenterStart) else modifier

    val modifierAlign = finalMod.getAlignment()
    val vertical = modifierAlign?.toVertical() ?: Alignment.CenterVertically
    val horizontal = (modifierAlign?.toHorizontal() ?: alignment).let { Arrangement.spacedBy(spacing.dp, it) }

    Row(
        modifier = finalMod.fillMaxSize().applyShadowIfNeeded(),
        verticalAlignment = vertical,
        horizontalArrangement = horizontal,
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
    val finalMod = if (modifier.getAlignment() == null) modifier.alignment(alignment) else modifier

    HStack(
        modifier = finalMod,
        spacing = spacing,
        content = content
    )
}



// ---------------------------------------------------------------------------------------------
// HELPERS (Logic to decode 2D Alignment into 1D rules)
// ---------------------------------------------------------------------------------------------

fun Alignment.toColumnRules(): Pair<Arrangement.Vertical, Alignment.Horizontal> {
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

fun Alignment.toRowRules(): Pair<Alignment.Vertical, Arrangement.Horizontal> {
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

fun Alignment.toVertical(): Alignment.Vertical {
    return when(this) {
        Alignment.TopStart, Alignment.TopCenter, Alignment.TopEnd -> Alignment.Top
        Alignment.BottomStart, Alignment.BottomCenter, Alignment.BottomEnd -> Alignment.Bottom
        else -> Alignment.CenterVertically
    }
}

fun Alignment.toHorizontal(): Alignment.Horizontal {
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
fun ZStack(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable BoxScope.() -> Unit
) {
    val effectiveAlignment = modifier.getAlignment() ?: contentAlignment

    Box(
        modifier = modifier.fillMaxSize().applyShadowIfNeeded(),
        contentAlignment = effectiveAlignment,
        content = content
    )
}

@Composable
fun Group(content: @Composable () -> Unit) {
    content()
}


// ---------------------------------------------------------------------------------------------
// DRIFTVIEW (Manager + Container)
// ---------------------------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriftView(
    // 1. Structural Slots (Standard Scaffold)
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    floatingAction: @Composable () -> Unit = {},

    // 2. Behavior
    blockBackgroundAudio: Boolean = false,
    useSafeArea: Boolean = true,

    // 3. Main Content
    content: @Composable BoxScope.() -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // --- INITIALIZATION ---
    DriftRegistry.context = context.applicationContext
    DriftGlobals.currentActivity = context as? Activity
    DriftGlobals.applicationContext = context.applicationContext

    val config = LocalConfiguration.current
    DriftScale.widthScale = config.screenWidthDp / DriftScale.REFERENCE_WIDTH
    DriftScale.heightScale = config.screenHeightDp / DriftScale.REFERENCE_HEIGHT

    LaunchedEffect(Unit) {
        DriftAudio.initialize(context)
        DriftHaptics.initialize(context)
        DriftStorage.initialize(context)
        DriftNotificationEngine.prepareIfNeeded()
    }

    if (blockBackgroundAudio) {
        DisposableEffect(Unit) {
            DriftAudio.requestSilence(context)
            onDispose { DriftAudio.releaseSilence(context) }
        }
    }

    // --- SCAFFOLDING ---
    // If useSafeArea is false, we ignore WindowInsets to draw under status/nav bars
    val windowInsets = if (useSafeArea) ScaffoldDefaults.contentWindowInsets else WindowInsets(0, 0, 0, 0)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = topBar,
        bottomBar = bottomBar,
        floatingActionButton = floatingAction,
        contentWindowInsets = windowInsets,
        containerColor = driftColors.background
    ) { innerPadding ->
        // The container that hosts your actual code
        Box(
            modifier = Modifier
                .fillMaxSize()
                .stdPadding(innerPadding)
        ) {
            content()
        }

        DriftToastHost()
    }
}

// 1. COLUMN CONTEXT (Vertical Stack)
// -----------------------------------------------------------
// "Spacer()" -> Spring (Pushes content to bottom/top)
@Composable
fun ColumnScope.Spacer() {
    androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))
}

// "Spacer(10)" -> Vertical Gap only (Doesn't affect width)
@Composable
fun ColumnScope.Spacer(size: Int) {
    androidx.compose.foundation.layout.Spacer(Modifier.height(size.dp))
}


// 2. ROW CONTEXT (Horizontal Stack)
// -----------------------------------------------------------
// "Spacer()" -> Spring (Pushes content to left/right)
@Composable
fun RowScope.Spacer() {
    androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))
}

// "Spacer(10)" -> Horizontal Gap only (Doesn't affect height)
@Composable
fun RowScope.Spacer(size: Int) {
    androidx.compose.foundation.layout.Spacer(Modifier.width(size.dp))
}


// 3. BOX/DRIFTVIEW CONTEXT (ZStack)
// -----------------------------------------------------------
// "Spacer()" -> Fill (Expands to fill the container, useful for touch targets or pushing)
@Composable
fun BoxScope.Spacer() {
    androidx.compose.foundation.layout.Spacer(Modifier.fillMaxSize())
}


// 4. GLOBAL FALLBACK
// -----------------------------------------------------------
// "Spacer(10)" -> Square Gap (Used inside Box or generic contexts)
@Composable
fun Spacer(size: Int) {
    androidx.compose.foundation.layout.Spacer(Modifier.size(size.dp))
}

