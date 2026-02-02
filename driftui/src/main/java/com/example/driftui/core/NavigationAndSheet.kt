

package com.example.driftui.core

//This file is NavigationAndSheet
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import java.util.UUID

// ---------------------------------------------------------------------------------------------
// 1. DATA MODELS (Restored for Compatibility)
// ---------------------------------------------------------------------------------------------

enum class ToolbarPlacement { Leading, Center, Trailing }

data class ToolbarEntry(
    val placement: ToolbarPlacement,
    val content: @Composable () -> Unit
)

// ✅ Restored StackEntry for API compatibility
data class StackEntry(
    val isRoot: Boolean = false,
    val content: @Composable () -> Unit
)

// ✅ Restored SheetState class
data class SheetState(
    var isPresented: MutableState<Boolean>,
    val content: MutableState<(@Composable () -> Unit)?>
)

// ---------------------------------------------------------------------------------------------
// 2. TOOLBAR LOCALS & WRAPPERS
// ---------------------------------------------------------------------------------------------

val LocalToolbarState = compositionLocalOf<MutableList<ToolbarEntry>?> { null }
val LocalToolbarLayoutState = compositionLocalOf<MutableState<Modifier?>?> { null }

val LocalToolbarForeground = compositionLocalOf<Color?> { null }
val LocalToolbarBackground = compositionLocalOf<Color?> { null }
val LocalToolbarElevation = compositionLocalOf<Dp?> { null }
val LocalToolbarContentPadding = compositionLocalOf<Dp?> { null }

@Composable
fun ToolbarItem(
    placement: ToolbarPlacement,
    content: @Composable () -> Unit
) {
    val state = LocalToolbarState.current ?: return
    val entry = remember { ToolbarEntry(placement, content) }
    DisposableEffect(entry) {
        state.add(entry)
        onDispose { state.remove(entry) }
    }
}

@Composable
fun toolbar(
    modifier: Modifier = Modifier,
    foreground: Color? = null,
    background: Color? = null,
    elevation: Dp? = null,
    contentPadding: Dp? = null,
    content: @Composable () -> Unit
) {
    // We update the LocalToolbarLayoutState if it exists (legacy support)
    val layoutState = LocalToolbarLayoutState.current

    // We create a simpler modifier representation for the native scaffold to read if needed
    // Note: In the Native implementation, direct modifier passing via CompositionLocal
    // is complex, but we maintain the API signature and pass style Locals down.

    CompositionLocalProvider(
        LocalToolbarForeground provides foreground,
        LocalToolbarBackground provides background,
        LocalToolbarElevation provides elevation,
        LocalToolbarContentPadding provides contentPadding
    ) {
        Box(modifier = modifier) {
            content()
        }
    }
}

// ---------------------------------------------------------------------------------------------
// 3. NAVIGATION CONTROLLER
// ---------------------------------------------------------------------------------------------

object DriftRootHost {
    val rootContent = mutableStateOf<(@Composable () -> Unit)?>(null)
}

class DriftNavController(
    private val navController: NavHostController
) {
    val screenRegistry = mutableMapOf<String, @Composable () -> Unit>()

    fun register(id: String, screen: @Composable () -> Unit) {
        screenRegistry[id] = screen
    }

    fun push(screen: @Composable () -> Unit) {
        val id = UUID().toString()
        screenRegistry[id] = screen
        // Matches the "screen/{id}" route in LayoutPrimitives
        navController.navigate("screen/$id")
    }

    fun replace(screen: @Composable () -> Unit) {
        val id = UUID().toString()
        screenRegistry[id] = screen

        // 1. Get the ID of the screen we are standing on right now
        val currentDestinationId = navController.currentDestination?.id

        navController.navigate("screen/$id") {
            // 2. Pop it safely
            if (currentDestinationId != null) {
                popUpTo(currentDestinationId) { inclusive = true }
            }
            launchSingleTop = true
        }
    }

    fun replaceRoot(screen: @Composable () -> Unit) {
        DriftRootHost.rootContent.value = screen
    }

    fun pop() {
        navController.popBackStack()
    }

    fun dismiss() = pop()

    @Composable
    fun canPopState(): androidx.compose.runtime.State<Boolean> {
        val entry by navController.currentBackStackEntryAsState()
        return remember(entry) {
            derivedStateOf {
                navController.previousBackStackEntry != null
            }
        }
    }

    fun resolve(id: String?): (@Composable () -> Unit)? = screenRegistry[id]
}

val LocalNavController = compositionLocalOf<DriftNavController> { error("No NavStack") }

@Composable
fun useNav() = LocalNavController.current

// ... (Keep Navigation Actions, Dismiss, etc.) ...
@Composable
fun Dismiss(): () -> Unit {
    val nav = LocalNavController.current
    return remember { { nav.dismiss() } }
}

class NavigationAction(private val state: MutableState<(() -> Unit)?>) {
    fun set(action: () -> Unit) { state.value = action }
    operator fun invoke() { state.value?.invoke() }
}

@Composable
fun useNavigationAction(): NavigationAction {
    val state = remember { mutableStateOf<(() -> Unit)?>(null) }
    return remember { NavigationAction(state) }
}