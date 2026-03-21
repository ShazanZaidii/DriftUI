package com.example.driftui.core
// This file is Colors.kt
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme

// ---------------------------------------------------------------------------------------------
// COLORS (SwiftUI-style)
// ---------------------------------------------------------------------------------------------

private val Black     = Color(0xFF000000)
private val Blue      = Color(0xFF007AFF)
private val Cyan      = Color(0xFF00FFFF)
private val Gray      = Color(0xFF8E8E93)
private val Green     = Color(0xFF34C759)
private val LightGray = Color(0xFFD1D1D6)
private val Magenta   = Color(0xFFFF2D55)
private val Red       = Color(0xFFFF3B30)
private val White     = Color(0xFFFFFFFF)
private val Yellow    = Color(0xFFFFCC00)

val Color.Companion.black get() = Black
val Color.Companion.blue get() = Blue
val Color.Companion.cyan get() = Cyan
val Color.Companion.gray get() = Gray
val Color.Companion.green get() = Green
val Color.Companion.lightGray get() = LightGray
val Color.Companion.magenta get() = Magenta
val Color.Companion.red get() = Red
val Color.Companion.white get() = White
val Color.Companion.yellow get() = Yellow

val Color.Companion.pink get() = Color(0xFFCF00FF)
val Color.Companion.orange get() = Color(0xFFFF9500)
val Color.Companion.purple get() = Color(0xFF7D00FF)
val Color.Companion.brown get() = Color(0xFFA2845E)
val Color.Companion.mint get() = Color(0xFF00C7BE)
val Color.Companion.teal get() = Color(0xFF30B0C7)
val Color.Companion.indigo get() = Color(0xFF5856D6)
val Color.Companion.shazan get() = Color(0xFF567779)


//Default theme colours for modifiers

data class DriftColors(
    val text: Color,
    val background: Color,
    val fieldBackground: Color,
    val accent: Color
)
val driftColors: DriftColors
    @Composable get() = DriftColors(
        text = MaterialTheme.colorScheme.onBackground,
        background = MaterialTheme.colorScheme.background,
        fieldBackground = MaterialTheme.colorScheme.surface,
        accent = MaterialTheme.colorScheme.primary
    )