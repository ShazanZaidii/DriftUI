package com.example.driftui.core
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// This allows you to use 'statusBarHeight' directly in any Composable
val statusBarHeight: Dp
    @Composable
    get() = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

val statusBarWidth: Dp
    @Composable
    get() = LocalConfiguration.current.screenWidthDp.dp

val deviceWidth: Dp
    @Composable
    get() = LocalConfiguration.current.screenWidthDp.dp

// 2. Device Screen Height
val deviceHeight: Dp
    @Composable
    get() = LocalConfiguration.current.screenHeightDp.dp

//unit:

// Perceptual (fonts, icons, radius)
val Number.u: Double
    get() = this.toDouble() * DriftScale.visualScale

// Width-based unit
val Number.wu: Double
    get() = this.toDouble() * DriftScale.widthScale

// Height-based unit
val Number.hu: Double
    get() = this.toDouble() * DriftScale.heightScale


val xMAx = 170.wu.toInt()
val xMin = -170.wu.toInt()
val yMax = -370.hu.toInt()
val yMin = 370.hu.toInt()


@Composable
fun scaledSp(size: Int): TextUnit {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    // 360dp is the standard "base" width for most designs.
    // This calculates a multiplier. e.g. Tablet (720dp) = 2.0x multiplier.
    val scaleFactor = screenWidth / 360f
    return (size * scaleFactor).sp
}

@Composable
fun scaledDp(size: Number): Dp {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.toFloat()

    // 360dp is our reference "Normal Phone" width.
    // If the screen is wider (e.g. 720dp tablet), the multiplier becomes 2.0x.
    val scaleFactor = screenWidth / 360f

    return (size.toFloat() * scaleFactor).dp
}