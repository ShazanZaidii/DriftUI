package com.example.driftui.core

// layout primitives
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
import androidx.compose.material3.* import androidx.compose.runtime.CompositionLocalProvider
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
    Phone,      // 380dp to 600dp
    Tablet,     // 600dp to 840dp
    Desktop     // > 840dp
}

@Composable
fun getScreenSizes(): Screen {
    val config = LocalConfiguration.current
    val width = config.screenWidthDp.dp

    return when {
        width < 380.dp -> Screen.SmallPhone
        width < 600.dp -> Screen.Phone
        width < 840.dp -> Screen.Tablet
        else -> Screen.Desktop
    }
}

object DriftScale {
    // reference device dimensions
    const val REFERENCE_WIDTH = 390f
    const val REFERENCE_HEIGHT = 780f

    var widthScale = 1f
    var heightScale = 1f

    // perceptual scale for fonts and icons
    val visualScale: Float
        get() = sqrt(widthScale * heightScale)
}

// global alignment variables
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

// drift setup
@Composable
fun DriftSetup(
    blockBackgroundAudio: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val config = LocalConfiguration.current

    // initialization
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

            // route pattern matches registry keys
            composable(
                route = "screen/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")
                driftNavController.screenRegistry[id]?.invoke()
            }
        }
        DriftToastHost()
    }
}

// vertical stack spacer
@Composable
fun ColumnScope.Spacer() {
    Spacer(Modifier.weight(1f))
}

@Composable
fun ColumnScope.Spacer(size: Int) {
    Spacer(Modifier.height(size.dp))
}

// horizontal stack spacer
@Composable
fun RowScope.Spacer() {
    Spacer(Modifier.weight(1f))
}

@Composable
fun RowScope.Spacer(size: Int) {
    Spacer(Modifier.width(size.dp))
}

// z stack spacer
@Composable
fun BoxScope.Spacer() {
    Spacer(Modifier.fillMaxSize())
}

// generic spacer
@Composable
fun Spacer(size: Int) {
    Spacer(Modifier.size(size.dp))
}