package com.example.driftui

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.zIndex
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable

// =============================================================================================
// PUBLIC API: PLACEMENT (Toolbar-style)
// =============================================================================================

enum class MenuPlacement {
    Top,
    Center,
    Bottom
}

// =============================================================================================
// INTERNAL SCOPE (Collects items like Toolbar)
// =============================================================================================

private class MenuScope {
    val top = mutableListOf<@Composable () -> Unit>()
    val center = mutableListOf<@Composable () -> Unit>()
    val bottom = mutableListOf<@Composable () -> Unit>()
}

private val LocalMenuScope = staticCompositionLocalOf<MenuScope> {
    error("MenuItem must be used inside SideMenu")
}

// =============================================================================================
// PUBLIC API: MENU ITEM (ToolbarItem equivalent)
// =============================================================================================

@Composable
fun MenuItem(
    placement: MenuPlacement,
    content: @Composable () -> Unit
) {
    val scope = LocalMenuScope.current
    when (placement) {
        MenuPlacement.Top -> scope.top += content
        MenuPlacement.Center -> scope.center += content
        MenuPlacement.Bottom -> scope.bottom += content
    }
}

// =============================================================================================
// PUBLIC API: SIDE MENU (Overlay Toolbar)
// =============================================================================================

@Composable
fun SideMenu(
    isOpen: Boolean,
    modifier: Modifier = Modifier,
    scrimColor: Color = Color.Black.copy(alpha = 0.4f),
    onDismiss: () -> Unit = {},
    content: @Composable () -> Unit
) {
    if (!isOpen) return

    val scope = remember { MenuScope() }

    // Collect menu items (toolbar-style declaration phase)
    CompositionLocalProvider(LocalMenuScope provides scope) {
        content()
    }

    // Overlay layer (draws over toolbar)
    Box(
        Modifier
            .fillMaxSize()
            .zIndex(100f)
    ) {

        // Scrim (dismiss on tap)
        Box(
            Modifier
                .fillMaxSize()
                .background(scrimColor)
                .clickable { onDismiss() }
        )

        // Menu surface (vertical toolbar)
        Column(
            modifier
                .fillMaxHeight()
                .align(Alignment.CenterStart)
        ) {

            // TOP
            Column(Modifier.weight(1f)) {
                scope.top.forEach { it() }
            }

            // CENTER
            Column(
                Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                scope.center.forEach { it() }
            }

            // BOTTOM
            Column(
                Modifier.weight(1f),
                verticalArrangement = Arrangement.Bottom
            ) {
                scope.bottom.forEach { it() }
            }
        }
    }
}
