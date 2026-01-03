package com.example.driftui
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

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

val unit: Double
    get() = DriftScale.scaleFactor.toDouble()

val Number.u: Double
    get() = this.toDouble() * unit