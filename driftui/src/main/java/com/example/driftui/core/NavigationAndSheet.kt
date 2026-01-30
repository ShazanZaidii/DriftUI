package com.example.driftui.core

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
// 3. NAVIGATION CONTROLLER (Native Wrapper with Legacy API)
// ---------------------------------------------------------------------------------------------

class DriftNavController(val nativeController: NavHostController) {

    // Registry to map UUIDs to Composable lambdas
    val screenRegistry = mutableMapOf<String, @Composable () -> Unit>()

    fun push(screen: @Composable () -> Unit) {
        val id = UUID.randomUUID().toString()
        screenRegistry[id] = screen
        nativeController.navigate(id)
    }

    fun pushReplacement(screen: @Composable () -> Unit) {
        val id = UUID.randomUUID().toString()
        screenRegistry[id] = screen
        val startId = nativeController.graph.startDestinationId
        nativeController.navigate(id) {
            popUpTo(0) { inclusive = true } // Clear entire stack
            launchSingleTop = true
        }
    }

    fun pop() {
        val currentRoute = nativeController.currentBackStackEntry?.destination?.route
        if (currentRoute != null && screenRegistry.containsKey(currentRoute)) {
            screenRegistry.remove(currentRoute)
        }
        nativeController.popBackStack()
    }

    fun clear() {
        screenRegistry.clear()
        nativeController.popBackStack(nativeController.graph.startDestinationId, true)
    }

    fun dismiss() = pop()

    @Composable
    fun canPopState(): androidx.compose.runtime.State<Boolean> {
        val currentBackStack by nativeController.currentBackStackEntryAsState()
        return remember(currentBackStack) {
            derivedStateOf { nativeController.previousBackStackEntry != null }
        }
    }

    fun canPop(): Boolean {
        return nativeController.previousBackStackEntry != null
    }

    fun current(): (@Composable () -> Unit)? {
        val route = nativeController.currentBackStackEntry?.destination?.route ?: return null
        return screenRegistry[route]
    }

    // ✅ Restored: Compatibility for 'currentEntry()'
    fun currentEntry(): StackEntry? {
        val content = current() ?: return null
        // We infer root status roughly by backstack count
        val isRoot = nativeController.previousBackStackEntry == null
        return StackEntry(isRoot, content)
    }
}

val LocalNavController = compositionLocalOf<DriftNavController> { error("No NavStack") }

@Composable
fun useNav() = LocalNavController.current

// ---------------------------------------------------------------------------------------------
// 4. NAVIGATION ACTIONS (Restored)
// ---------------------------------------------------------------------------------------------

class NavigationAction(private val state: MutableState<(() -> Unit)?>) {
    fun set(action: () -> Unit) { state.value = action }
    operator fun invoke() { state.value?.invoke() }
}

@Composable
fun useNavigationAction(): NavigationAction {
    val state = remember { mutableStateOf<(() -> Unit)?>(null) }
    return remember { NavigationAction(state) }
}

@Composable
fun Dismiss(): () -> Unit {
    val nav = LocalNavController.current
    return remember { { nav.dismiss() } }
}

// ---------------------------------------------------------------------------------------------
// 5. NAVIGATION LINKS
// ---------------------------------------------------------------------------------------------

@Composable
fun NavigationLink(
    title: String,
    destination: @Composable () -> Unit
) {
    val nav = LocalNavController.current
    Text(
        title,
        Modifier.clickable { nav.push(destination) },
        color = driftColors.accent // Assuming driftColors exists in your project context
    )
}

@Composable
fun NavigationLink(
    destination: @Composable () -> Unit,
    label: @Composable () -> Unit
) {
    val nav = LocalNavController.current
    Box(
        Modifier.clickable { nav.push(destination) }
    ) {
        label()
    }
}

// ---------------------------------------------------------------------------------------------
// 6. MAIN NAVIGATION STACK
// ---------------------------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationStack(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // --- SETUP NATIVE NAV ---
    val nativeNavController = rememberNavController()
    val driftNavController = remember { DriftNavController(nativeNavController) }
    val context = LocalContext.current

    // --- STATE COLLECTORS ---
    val toolbarItems = remember { mutableStateListOf<ToolbarEntry>() }
    val toolbarLayoutState = remember { mutableStateOf<Modifier?>(null) } // Restored

    // Config
    var navTitle: String? = null
    var hideBackButton = false
    var overrideScheme: DriftColorScheme? = null

    // Lifecycle
    var onAppearAction: (() -> Unit)? = null
    var onDisappearAction: (() -> Unit)? = null

    // Style Collectors
    var toolbarFg: Color? = null
    var toolbarBg: Color? = null
    var toolbarGradient: GradientColor? = null
    var toolbarElev: Dp? = null
    var toolbarPadding: Dp? = null

    // Sheet Collector
    var sheetModifier: SheetModifier? = null

    // --- EXTRACT MODIFIERS ---
    modifier.foldIn(Unit) { _, el ->
        if (el is NavigationTitleModifier) navTitle = el.title
        else if (el is BackButtonHiddenModifier) hideBackButton = el.hidden
        else if (el is PreferredColorSchemeModifier) overrideScheme = el.scheme
        else if (el is LifecycleAppearModifier) onAppearAction = el.action
        else if (el is LifecycleDisappearModifier) onDisappearAction = el.action
        else if (el is SheetModifier) sheetModifier = el
        else if (el is ToolbarStyleModifier) {
            toolbarFg = el.foreground
            toolbarBg = el.background
            toolbarElev = el.elevation
            toolbarPadding = el.contentPadding
        }
        else if (el is ToolbarGradientModifier) {
            toolbarGradient = el.gradient
        }
        Unit
    }

    // --- LIFECYCLE ---
    DisposableEffect(Unit) {
        onAppearAction?.invoke()
        onDispose { onDisappearAction?.invoke() }
    }

    // --- THEME ---
    val isDark = when (overrideScheme) {
        DriftColorScheme.Dark -> true
        DriftColorScheme.Light -> false
        null -> isSystemInDarkTheme()
    }

    // Fallback colors if DriftTheme isn't wrapping this
    val colors = if (isDark) darkColorScheme(surface = Color(0xFF1E1E1E)) else lightColorScheme(surface = Color.White)

    MaterialTheme(colorScheme = colors) {

        // --- RESOLVE COLORS ---
        val finalFg = toolbarFg ?: LocalToolbarForeground.current ?: driftColors.text
        val finalBg = toolbarBg ?: LocalToolbarBackground.current ?: Color.Transparent
        val finalElev = toolbarElev ?: LocalToolbarElevation.current ?: 0.dp

        val canPop by driftNavController.canPopState()

        // --- PROVIDE CONTEXT ---
        CompositionLocalProvider(
            LocalNavController provides driftNavController,
            LocalToolbarState provides toolbarItems,
            LocalToolbarLayoutState provides toolbarLayoutState,
            // Pass styles down so children know how to color themselves
            LocalToolbarForeground provides finalFg,
            LocalToolbarBackground provides finalBg,
            LocalToolbarElevation provides finalElev
        ) {
            Scaffold(
                // --- NATIVE TOOLBAR ---
                topBar = {
                    val showBar = navTitle != null || toolbarItems.isNotEmpty() || (canPop && !hideBackButton)

                    if (showBar) {
                        val containerColor = if (toolbarGradient != null) Color.Transparent else finalBg

                        Box {
                            // Gradient Background Layer
                            if (toolbarGradient != null) {
                                Box(
                                    Modifier
                                        .matchParentSize()
                                        .background(Brush.linearGradient(toolbarGradient!!.colors))
                                )
                            }

                            TopAppBar(
                                title = {
                                    if (navTitle != null) {
                                        Text(navTitle!!, style = MaterialTheme.typography.titleMedium, color = finalFg)
                                    }
                                },
                                navigationIcon = {
                                    if (canPop && !hideBackButton) {
                                        TextButton(onClick = { driftNavController.pop() }) {
                                            Text("< Back", color = finalFg)
                                        }
                                    } else {
                                        // Leading Item
                                        toolbarItems.firstOrNull { it.placement == ToolbarPlacement.Leading }?.content?.invoke()
                                    }
                                },
                                actions = {
                                    toolbarItems.filter { it.placement == ToolbarPlacement.Trailing }
                                        .forEach { it.content() }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = containerColor,
                                    titleContentColor = finalFg,
                                    actionIconContentColor = finalFg,
                                    navigationIconContentColor = finalFg,
                                    scrolledContainerColor = containerColor
                                ),
                                modifier = Modifier.shadow(finalElev)
                            )
                        }
                    }
                },
                content = { paddingValues ->
                    // --- NATIVE NAV HOST ---
                    Box(Modifier.padding(paddingValues)) {
                        NavHost(
                            navController = nativeNavController,
                            startDestination = "root"
                        ) {
                            composable("root") {
                                DisposableEffect(Unit) {
                                    toolbarItems.clear()
                                    onDispose {}
                                }
                                content()
                            }

                            composable("{id}") { backStackEntry ->
                                val id = backStackEntry.arguments?.getString("id")
                                val screenContent = driftNavController.screenRegistry[id]

                                if (screenContent != null) {
                                    DisposableEffect(Unit) {
                                        toolbarItems.clear()
                                        onDispose {}
                                    }
                                    screenContent()
                                }
                            }
                        }
                    }
                }
            )

            // --- NATIVE SHEET IMPLEMENTATION ---
            // Replaces the old 200-line drag gesture implementation with M3 ModalBottomSheet
            if (sheetModifier != null) {
                val isPresented by sheetModifier!!.isPresented

                val skipPartial = !sheetModifier!!.detents.any { it is SheetDetent.Fraction }
                val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = skipPartial)
                val shape = RoundedCornerShape(
                    topStart = sheetModifier!!.cornerRadius.dp,
                    topEnd = sheetModifier!!.cornerRadius.dp
                )

                if (isPresented) {
                    ModalBottomSheet(
                        onDismissRequest = {
                            if (sheetModifier!!.allowDismiss) {
                                sheetModifier!!.isPresented.set(false)
                            }
                        },
                        sheetState = sheetState,
                        shape = shape,
                        containerColor = MaterialTheme.colorScheme.surface,
                        dragHandle = {
                            if(sheetModifier!!.showGrabber) {
                                BottomSheetDefaults.DragHandle()
                            }
                        },
                        content = {
                            sheetModifier!!.content()
                            Spacer(Modifier.height(40.dp))
                        }
                    )
                }
            }
        }
    }
}

// Helper for shadow if not using standard M3 elevation modifiers
fun Modifier.shadow(elevation: Dp): Modifier =
    if (elevation > 0.dp) this.then(Modifier.shadow(elevation)) else this

