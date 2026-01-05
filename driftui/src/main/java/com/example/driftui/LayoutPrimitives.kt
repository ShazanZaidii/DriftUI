//package com.example.driftui
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.Lifecycle
//import androidx.lifecycle.LifecycleEventObserver
//import androidx.lifecycle.compose.LocalLifecycleOwner
//import androidx.compose.runtime.DisposableEffect
//import androidx.compose.runtime.LaunchedEffect
//
//// ---------------------------------------------------------------------------------------------
//// GLOBAL ALIGNMENT VARIABLES (For easy access like 'top', 'leading')
//// ---------------------------------------------------------------------------------------------
//
//val top: Alignment = Alignment.TopCenter
//val bottom: Alignment = Alignment.BottomCenter
//val center: Alignment = Alignment.Center
//val leading: Alignment = Alignment.CenterStart
//val trailing: Alignment = Alignment.CenterEnd
//val topLeading: Alignment = Alignment.TopStart
//val topTrailing: Alignment = Alignment.TopEnd
//val bottomLeading: Alignment = Alignment.BottomStart
//val bottomTrailing: Alignment = Alignment.BottomEnd
//
//// ---------------------------------------------------------------------------------------------
//// SMART VSTACK & HSTACK
//// ---------------------------------------------------------------------------------------------
//
///**
// * VStack now supports:
// * 1. Horizontal Alignment (Cross Axis): .leading, .trailing, .centerH
// * 2. Vertical Alignment (Main Axis): .top, .bottom, .centerV
// * 3. 2D Alignment: .topLeading, .bottomTrailing (Configures BOTH axes)
// */
//
//// 1. ORIGINAL (Horizontal Only - Default)
//@Composable
//fun VStack(
//    alignment: Alignment.Horizontal = Alignment.CenterHorizontally,
//    spacing: Int = 0,
//    modifier: Modifier = Modifier,
//    content: @Composable ColumnScope.() -> Unit
//) {
//    Column(
//        modifier = modifier.fillMaxWidth(),
//        horizontalAlignment = alignment,
//        verticalArrangement = Arrangement.spacedBy(spacing.dp),
//        content = content
//    )
//}
//
//// 2. NEW: VERTICAL OVERLOAD (Handles .top, .bottom - Main Axis)
//@Composable
//fun VStack(
//    alignment: Alignment.Vertical,
//    spacing: Int = 0,
//    modifier: Modifier = Modifier,
//    content: @Composable ColumnScope.() -> Unit
//) {
//    // Maps Alignment.Top -> Arrangement.Top, etc.
//    Column(
//        modifier = modifier.fillMaxWidth(),
//        horizontalAlignment = Alignment.CenterHorizontally, // Default
//        verticalArrangement = Arrangement.spacedBy(spacing.dp, alignment),
//        content = content
//    )
//}
//
//// 3. NEW: 2D ALIGNMENT OVERLOAD (Handles .topLeading, .center, etc.)
//// This is the smartest one: it extracts both vertical and horizontal rules.
//@Composable
//fun VStack(
//    alignment: Alignment,
//    spacing: Int = 0,
//    modifier: Modifier = Modifier,
//    content: @Composable ColumnScope.() -> Unit
//) {
//    val (vertArrangement, horizAlign) = alignment.toColumnRules()
//
//    Column(
//        modifier = modifier.fillMaxWidth(),
//        horizontalAlignment = horizAlign,
//        verticalArrangement = if (vertArrangement == Arrangement.Top) Arrangement.spacedBy(spacing.dp)
//        else Arrangement.spacedBy(spacing.dp, alignment.toVertical()),
//        content = content
//    )
//}
//
//// --- HSTACK OVERLOADS ---
//
//// 1. ORIGINAL (Vertical Only - Default)
//@Composable
//fun HStack(
//    alignment: Alignment.Vertical = Alignment.CenterVertically,
//    spacing: Int = 0,
//    modifier: Modifier = Modifier,
//    content: @Composable RowScope.() -> Unit
//) {
//    Row(
//        modifier = modifier, // HStack wraps width by default
//        verticalAlignment = alignment,
//        horizontalArrangement = Arrangement.spacedBy(spacing.dp),
//        content = content
//    )
//}
//
//// 2. NEW: HORIZONTAL OVERLOAD (Handles .leading, .trailing - Main Axis)
//@Composable
//fun HStack(
//    alignment: Alignment.Horizontal,
//    spacing: Int = 0,
//    modifier: Modifier = Modifier,
//    content: @Composable RowScope.() -> Unit
//) {
//    Row(
//        modifier = modifier,
//        verticalAlignment = Alignment.CenterVertically, // Default
//        horizontalArrangement = Arrangement.spacedBy(spacing.dp, alignment),
//        content = content
//    )
//}
//
//// 3. NEW: 2D ALIGNMENT OVERLOAD
//@Composable
//fun HStack(
//    alignment: Alignment,
//    spacing: Int = 0,
//    modifier: Modifier = Modifier,
//    content: @Composable RowScope.() -> Unit
//) {
//    val (vertAlign, horizArrangement) = alignment.toRowRules()
//
//    Row(
//        modifier = modifier,
//        verticalAlignment = vertAlign,
//        horizontalArrangement = if (horizArrangement == Arrangement.Start) Arrangement.spacedBy(spacing.dp)
//        else Arrangement.spacedBy(spacing.dp, alignment.toHorizontal()),
//        content = content
//    )
//}
//
//
//// ---------------------------------------------------------------------------------------------
//// HELPERS (Logic to decode 2D Alignment into 1D rules)
//// ---------------------------------------------------------------------------------------------
//
//// Helper to convert 2D Alignment to specific Column rules
//private fun Alignment.toColumnRules(): Pair<Arrangement.Vertical, Alignment.Horizontal> {
//    return when (this) {
//        Alignment.TopStart -> Arrangement.Top to Alignment.Start
//        Alignment.TopCenter -> Arrangement.Top to Alignment.CenterHorizontally
//        Alignment.TopEnd -> Arrangement.Top to Alignment.End
//        Alignment.CenterStart -> Arrangement.Center to Alignment.Start
//        Alignment.Center -> Arrangement.Center to Alignment.CenterHorizontally
//        Alignment.CenterEnd -> Arrangement.Center to Alignment.End
//        Alignment.BottomStart -> Arrangement.Bottom to Alignment.Start
//        Alignment.BottomCenter -> Arrangement.Bottom to Alignment.CenterHorizontally
//        Alignment.BottomEnd -> Arrangement.Bottom to Alignment.End
//        else -> Arrangement.Top to Alignment.CenterHorizontally
//    }
//}
//
//// Helper to convert 2D Alignment to specific Row rules
//private fun Alignment.toRowRules(): Pair<Alignment.Vertical, Arrangement.Horizontal> {
//    return when (this) {
//        Alignment.TopStart -> Alignment.Top to Arrangement.Start
//        Alignment.TopCenter -> Alignment.Top to Arrangement.Center
//        Alignment.TopEnd -> Alignment.Top to Arrangement.End
//        Alignment.CenterStart -> Alignment.CenterVertically to Arrangement.Start
//        Alignment.Center -> Alignment.CenterVertically to Arrangement.Center
//        Alignment.CenterEnd -> Alignment.CenterVertically to Arrangement.End
//        Alignment.BottomStart -> Alignment.Bottom to Arrangement.Start
//        Alignment.BottomCenter -> Alignment.Bottom to Arrangement.Center
//        Alignment.BottomEnd -> Alignment.Bottom to Arrangement.End
//        else -> Alignment.CenterVertically to Arrangement.Start
//    }
//}
//
//private fun Alignment.toVertical(): Alignment.Vertical {
//    return when(this) {
//        Alignment.TopStart, Alignment.TopCenter, Alignment.TopEnd -> Alignment.Top
//        Alignment.BottomStart, Alignment.BottomCenter, Alignment.BottomEnd -> Alignment.Bottom
//        else -> Alignment.CenterVertically
//    }
//}
//
//private fun Alignment.toHorizontal(): Alignment.Horizontal {
//    return when(this) {
//        Alignment.TopStart, Alignment.CenterStart, Alignment.BottomStart -> Alignment.Start
//        Alignment.TopEnd, Alignment.CenterEnd, Alignment.BottomEnd -> Alignment.End
//        else -> Alignment.CenterHorizontally
//    }
//}
//
//
//// ---------------------------------------------------------------------------------------------
//// DRIFTVIEW & OTHERS
//// ---------------------------------------------------------------------------------------------
//
//@Composable
//fun ZStack(
//    modifier: Modifier = Modifier,
//    contentAlignment: Alignment = Alignment.Center,
//    content: @Composable BoxScope.() -> Unit
//) {
//    Box(modifier = modifier, contentAlignment = contentAlignment, content = content)
//}
//
//@Composable
//fun DriftView(
//    modifier: Modifier = Modifier,
//    blockBackgroundAudio: Boolean = false,
//    content: @Composable BoxScope.() -> Unit
//) {
//    val context = androidx.compose.ui.platform.LocalContext.current
//    DriftRegistry.context = context.applicationContext
//    val lifecycleOwner = LocalLifecycleOwner.current
//
//    val activity = context as? android.app.Activity
//    DriftGlobals.currentActivity = activity
//
//    LaunchedEffect(Unit) {
//        DriftAudio.initialize(context)
//        DriftHaptics.initialize(context)
//        DriftStorage.initialize(context)
//        DriftGlobals.applicationContext = context.applicationContext
//        DriftNotificationEngine.prepareIfNeeded()
//    }
//
//    if (blockBackgroundAudio) {
//        DisposableEffect(Unit) {
//            DriftAudio.requestSilence(context)
//            onDispose { DriftAudio.releaseSilence(context) }
//        }
//    }
//
//    DisposableEffect(lifecycleOwner) {
//        val observer = LifecycleEventObserver { _, event ->
//            if (event == Lifecycle.Event.ON_PAUSE) DriftAudio.onAppPause()
//            else if (event == Lifecycle.Event.ON_RESUME) DriftAudio.onAppResume()
//        }
//        lifecycleOwner.lifecycle.addObserver(observer)
//        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
//    }
//
//    Box(modifier = modifier.fillMaxSize().background(driftColors.background),
//        contentAlignment = Alignment.Center) {
//        content()
//        DriftToastHost()
//    }
//}
//
//@Composable
//fun ColumnScope.Spacer() = androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))
//@Composable
//fun RowScope.Spacer() = androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))
//@Composable
//fun Spacer(size: Int) = androidx.compose.foundation.layout.Spacer(Modifier.size(size.dp))

package com.example.driftui

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


object DriftScale {
    var scaleFactor = 1f
    // The width you designed for (e.g., iPhone 13 / Pixel 6 is usually 390 or 375)
    const val REFERENCE_WIDTH = 390f
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
fun DriftView(
    modifier: Modifier = Modifier,
    blockBackgroundAudio: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    DriftRegistry.context = context.applicationContext
    val lifecycleOwner = LocalLifecycleOwner.current
    val activity = context as? android.app.Activity
    DriftGlobals.currentActivity = activity

    val config = LocalConfiguration.current
    DriftScale.scaleFactor = config.screenWidthDp / DriftScale.REFERENCE_WIDTH

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
fun ColumnScope.Spacer() = androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))
@Composable
fun RowScope.Spacer() = androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))
@Composable
fun Spacer(size: Int) = androidx.compose.foundation.layout.Spacer(Modifier.size(size.dp))

