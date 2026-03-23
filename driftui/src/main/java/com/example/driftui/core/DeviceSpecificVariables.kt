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

val statusBarHeight: Dp
    @Composable
    get() = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

val statusBarWidth: Dp
    @Composable
    get() = LocalConfiguration.current.screenWidthDp.dp

val deviceWidth: Dp
    @Composable
    get() = LocalConfiguration.current.screenWidthDp.dp

val deviceHeight: Dp
    @Composable
    get() = LocalConfiguration.current.screenHeightDp.dp

// visual scaling for fonts and icons
val Number.u: Double
    get() = this.toDouble() * DriftScale.visualScale

// width based unit
val Number.wu: Double
    get() = this.toDouble() * DriftScale.widthScale

// height based unit
val Number.hu: Double
    get() = this.toDouble() * DriftScale.heightScale

val xMAx = 170.wu.toInt()
val xMin = -170.wu.toInt()
val yMax = -370.hu.toInt()
val yMin = 370.hu.toInt()

// dynamic text scaling based on base width
@Composable
fun scaledSp(size: Int): TextUnit {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val scaleFactor = screenWidth / 360f
    return (size * scaleFactor).sp
}

// dynamic layout scaling based on base width
@Composable
fun scaledDp(size: Number): Dp {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.toFloat()
    val scaleFactor = screenWidth / 360f

    return (size.toFloat() * scaleFactor).dp
}