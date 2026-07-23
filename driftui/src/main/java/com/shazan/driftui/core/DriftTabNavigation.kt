package com.shazan.driftui.core

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable

/**
 * 1. The Global Controller (Now supports Lambda Syntax)
 */
class TabController {
    // Holds the routing logic, dynamically attached when a Navigator mounts.
    @PublishedApi
    internal var pushAction: ((Any) -> Unit)? = null

    // Allows: tabNav.push { DashboardTab.PROFILE }
    fun push(tabBuilder: () -> Any) {
        val newTab = tabBuilder()
        pushAction?.invoke(newTab)
    }
}

/**
 * 2. The Global Radio Station
 * By providing a default TabController here, it guarantees useTabNav()
 * will NEVER throw a crash, regardless of where it is called in the app.
 */
@PublishedApi
internal val LocalTabController = staticCompositionLocalOf { TabController() }

/**
 * 3. The Hook
 * Usable anywhere in the project, exactly like useNav().
 */
@Composable
fun useTabNav(): TabController {
    return LocalTabController.current
}

/**
 * 4. The Smart Wrapper
 */
@Composable
inline fun <reified T : Enum<T>> DriftTabNavigator(
    initialTab: T,
    crossinline content: @Composable (currentTab: T, setTab: (T) -> Unit) -> Unit
) {
    // Save the tab state natively
    var currentTab by rememberSaveable { mutableStateOf(initialTab) }

    // Grab the global controller
    val controller = useTabNav()

    // Safely attach this specific navigator's state to the global controller.
    DisposableEffect(controller) {
        controller.pushAction = { newTab -> currentTab = newTab as T }

        // Clean up when the dashboard is destroyed to prevent memory leaks
        onDispose { controller.pushAction = null }
    }

    // Smart BackStack
    BackHandler(enabled = currentTab != initialTab) {
        currentTab = initialTab
    }

    // Inject the wired controller down the tree
    CompositionLocalProvider(LocalTabController provides controller) {
        content(currentTab) { currentTab = it }
    }
}