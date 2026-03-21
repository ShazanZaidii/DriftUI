package com.example.driftui.core

//This file is LayoutPrimitives.kt
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.padding as stdPadding
import androidx.core.util.rangeTo
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument


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


@Composable
fun Group(content: @Composable () -> Unit) {
    content()
}


// ---------------------------------------------------------------------------------------------
// DriftSetup
// ---------------------------------------------------------------------------------------------


@Composable
fun DriftSetup(
    blockBackgroundAudio: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val config = LocalConfiguration.current

    // --- INIT ---
    remember {
        DriftRegistry.context = context.applicationContext
        DriftGlobals.applicationContext = context.applicationContext
        DriftGlobals.currentActivity = context as? Activity
        true
    }

    remember(config) {
        DriftScale.widthScale = config.screenWidthDp / DriftScale.REFERENCE_WIDTH
        DriftScale.heightScale = config.screenHeightDp / DriftScale.REFERENCE_HEIGHT
        true
    }

    LaunchedEffect(Unit) {
        DriftAudio.initialize(context)
        DriftHaptics.initialize(context)
        DriftStorage.initialize(context)
        DriftNotificationEngine.prepareIfNeeded()
    }

    if (blockBackgroundAudio) {
        DisposableEffect(lifecycleOwner) {
            DriftAudio.requestSilence(context)
            onDispose { DriftAudio.releaseSilence(context) }
        }
    }

    val rootOverride = DriftRootHost.rootContent.value
    val effectiveRoot = rootOverride ?: content

    DriftNavRoot(effectiveRoot)
}

@Composable
private fun DriftNavRoot(root: @Composable () -> Unit) {
    val navController = rememberNavController()
    val driftNavController = remember { DriftNavController(navController) }

    CompositionLocalProvider(
        LocalNavController provides driftNavController
    ) {
        NavHost(
            navController = navController,
            startDestination = "root"
        ) {
            composable("root") {
                root()
            }

            // FIX: Changed from "{id}" to "screen/{id}"
            // This ensures we parse ONLY the UUID, matching the registry key.
            composable(
                route = "screen/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")
                // Now lookup works: "123" matches "123"
                driftNavController.screenRegistry[id]?.invoke()
            }
        }
        DriftToastHost()
    }
}



// 1. COLUMN CONTEXT (Vertical Stack)
// -----------------------------------------------------------
// "Spacer()" -> Spring (Pushes content to bottom/top)
@Composable
fun ColumnScope.Spacer() {
    Spacer(Modifier.weight(1f))
}

// "Spacer(10)" -> Vertical Gap only (Doesn't affect width)
@Composable
fun ColumnScope.Spacer(size: Int) {
    Spacer(Modifier.height(size.dp))
}


// 2. ROW CONTEXT (Horizontal Stack)
// -----------------------------------------------------------
// "Spacer()" -> Spring (Pushes content to left/right)
@Composable
fun RowScope.Spacer() {
    Spacer(Modifier.weight(1f))
}

// "Spacer(10)" -> Horizontal Gap only (Doesn't affect height)
@Composable
fun RowScope.Spacer(size: Int) {
    Spacer(Modifier.width(size.dp))
}


// 3. BOX/DRIFTVIEW CONTEXT (ZStack)
// -----------------------------------------------------------
// "Spacer()" -> Fill (Expands to fill the container, useful for touch targets or pushing)
@Composable
fun BoxScope.Spacer() {
    Spacer(Modifier.fillMaxSize())
}


// 4. GLOBAL FALLBACK
// -----------------------------------------------------------
// "Spacer(10)" -> Square Gap (Used inside Box or generic contexts)
@Composable
fun Spacer(size: Int) {
    Spacer(Modifier.size(size.dp))
}
