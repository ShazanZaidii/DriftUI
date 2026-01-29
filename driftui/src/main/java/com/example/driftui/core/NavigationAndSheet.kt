package com.example.driftui.core

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import java.util.UUID

// ---------------------------------------------------------------------------------------------
// 1. TOOLBAR MODELS
// ---------------------------------------------------------------------------------------------
enum class ToolbarPlacement { Leading, Center, Trailing }

data class ToolbarEntry(
    val placement: ToolbarPlacement,
    val content: @Composable () -> Unit
)

val LocalToolbarState = compositionLocalOf<MutableList<ToolbarEntry>?> { null }

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

// ---------------------------------------------------------------------------------------------
// 2. NATIVE CONTROLLER WRAPPER
// ---------------------------------------------------------------------------------------------
class DriftNavController(val nativeController: NavHostController) {

    // Registry to map UUIDs to Composable lambdas
    val screenRegistry = mutableMapOf<String, @Composable () -> Unit>()

    fun push(screen: @Composable () -> Unit) {
        val id = UUID.randomUUID().toString()
        screenRegistry[id] = screen
        nativeController.navigate(id)
    }

    fun pop() {
        nativeController.popBackStack()
    }

    fun dismiss() = pop()

    @Composable
    fun canPopState(): androidx.compose.runtime.State<Boolean> {
        val currentBackStack by nativeController.currentBackStackEntryAsState()
        return remember(currentBackStack) {
            derivedStateOf { nativeController.previousBackStackEntry != null }
        }
    }
}

val LocalNavController = compositionLocalOf<DriftNavController> { error("No NavStack") }

// ---------------------------------------------------------------------------------------------
// 3. THE NAVIGATION STACK (Native Nav + Native Sheet)
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

    // --- STATE COLLECTORS ---
    val toolbarItems = remember { mutableStateListOf<ToolbarEntry>() }
    var navTitle: String? = null
    var hideBackButton = false

    // Style Collectors
    var toolbarFg: Color? = null
    var toolbarBg: Color? = null
    var toolbarGradient: GradientColor? = null

    // Sheet Collector
    var sheetModifier: SheetModifier? = null

    // --- EXTRACT MODIFIERS ---
    modifier.foldIn(Unit) { _, el ->
        if (el is NavigationTitleModifier) navTitle = el.title
        if (el is BackButtonHiddenModifier) hideBackButton = el.hidden
        if (el is SheetModifier) sheetModifier = el // <--- CAPTURE SHEET
        if (el is ToolbarStyleModifier) {
            toolbarFg = el.foreground
            toolbarBg = el.background
        }
        if (el is ToolbarGradientModifier) {
            toolbarGradient = el.gradient
        }
        Unit
    }

    // --- RESOLVE COLORS ---
    val finalFg = toolbarFg ?: driftColors.text
    val finalBg = toolbarBg ?: Color.Transparent

    val canPop by driftNavController.canPopState()

    // --- PROVIDE CONTEXT ---
    CompositionLocalProvider(
        LocalNavController provides driftNavController,
        LocalToolbarState provides toolbarItems
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
                                navigationIconContentColor = finalFg
                            )
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
        // We check if the modifier exists and if the state is true
        if (sheetModifier != null) {
            val isPresented by sheetModifier!!.isPresented
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

            // If the state is true, we mount the ModalBottomSheet
            if (isPresented) {
                ModalBottomSheet(
                    onDismissRequest = {
                        // Sync back to the user's state
                        if (sheetModifier!!.allowDismiss) {
                            sheetModifier!!.isPresented.set(false)
                        }
                    },
                    sheetState = sheetState,
                    containerColor = MaterialTheme.colorScheme.surface,
                    content = {
                        // Render user content
                        sheetModifier!!.content()
                        // Safe area spacer
                        Spacer(Modifier.height(40.dp))
                    }
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// 4. PUBLIC HELPERS
// ---------------------------------------------------------------------------------------------

@Composable
fun useNav() = LocalNavController.current

@Composable
fun Dismiss(): () -> Unit {
    val nav = LocalNavController.current
    return remember { { nav.dismiss() } }
}

@Composable
fun NavigationLink(title: String, destination: @Composable () -> Unit) {
    val nav = useNav()
    Text(
        text = title,
        modifier = Modifier.clickable { nav.push(destination) },
        color = driftColors.accent
    )
}

@Composable
fun NavigationLink(destination: @Composable () -> Unit, label: @Composable () -> Unit) {
    val nav = useNav()
    Box(Modifier.clickable { nav.push(destination) }) {
        label()
    }
}