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
import androidx.navigation.createGraph
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import java.util.UUID

// navigation controller

object DriftRootHost {
    val rootContent = mutableStateOf<(@Composable () -> Unit)?>(null)
}

class DriftNavController(
    private val navController: NavHostController
) {
    val screenRegistry = mutableMapOf<String, @Composable () -> Unit>()
    private val tagRegistry = mutableMapOf<String, String>()

    var currentTag by mutableStateOf<String?>(null)
        private set

    init {
        // listens for base root route and dynamic screen ids
        navController.addOnDestinationChangedListener { _, destination, arguments ->
            if (destination.route == "root") {
                currentTag = tagRegistry["root"]
            } else {
                val currentId = arguments?.getString("id")
                currentTag = tagRegistry[currentId]
            }
        }
    }

    // initial app launch
    fun setRootTag(tag: String) {
        tagRegistry["root"] = tag
        if (currentTag == null) currentTag = tag
    }

    // updates root and clears the backstack
    fun replaceRoot(tag: String? = null, screen: @Composable () -> Unit) {
        DriftRootHost.rootContent.value = screen
        if (tag != null) {
            tagRegistry["root"] = tag
        }
        // clears backstack down to base screen
        navController.popBackStack("root", inclusive = false)
    }

    // pushes a new screen dynamically, tags are completely optional
    fun push(tag: String? = null, screen: @Composable () -> Unit) {
        val id = UUID().toString()
        screenRegistry[id] = screen
        if (tag != null) tagRegistry[id] = tag
        navController.navigate("screen/$id")
    }

    // replaces current screen dynamically
    fun replace(tag: String? = null, screen: @Composable () -> Unit) {
        val id = UUID().toString()
        screenRegistry[id] = screen
        if (tag != null) tagRegistry[id] = tag

        val currentDestinationId = navController.currentDestination?.id

        navController.navigate("screen/$id") {
            if (currentDestinationId != null) {
                popUpTo(currentDestinationId) { inclusive = true }
            }
            launchSingleTop = true
        }
    }

    fun pop() = navController.popBackStack()

    fun currentScreenIs(tag: String): Boolean {
        return currentTag == tag
    }
}

val LocalNavController = compositionLocalOf<DriftNavController> { error("No NavStack") }

@Composable
fun useNav() = LocalNavController.current

class NavigationAction(private val state: MutableState<(() -> Unit)?>) {
    fun set(action: () -> Unit) { state.value = action }
    operator fun invoke() { state.value?.invoke() }
}

@Composable
fun useNavigationAction(): NavigationAction {
    val state = remember { mutableStateOf<(() -> Unit)?>(null) }
    return remember { NavigationAction(state) }
}