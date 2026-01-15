package com.example.driftui.core

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
    CompositionLocalProvider(LocalMenuScope provides scope) { content() }

    Box(Modifier.fillMaxSize().zIndex(100f)) {
        // Scrim
        Box(Modifier.fillMaxSize().background(scrimColor).clickable { onDismiss() })

        // Menu Surface
        Column(modifier.fillMaxHeight().align(Alignment.CenterStart)) {

            // 1. TOP SECTION (No weight -> Hugs content)
            Column(Modifier.fillMaxWidth()) {
                scope.top.forEach { it() }
            }

            // 2. SPACER (Pushes Center down slightly, or expands if empty)
            Spacer(Modifier.weight(1f))

            // 3. CENTER SECTION (No weight -> Expands as needed)
            Column(Modifier.fillMaxWidth()) {
                scope.center.forEach { it() }
            }

            // 4. SPACER (Pushes Bottom down)
            Spacer(Modifier.weight(1f))

            // 5. BOTTOM SECTION
            Column(Modifier.fillMaxWidth()) {
                scope.bottom.forEach { it() }
            }
        }
    }
}