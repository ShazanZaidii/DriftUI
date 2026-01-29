package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import com.example.driftui.core.DriftRegistry
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.foundation.layout.padding
import com.example.driftui.core.*
import com.example.myapplication.learning.learning

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
//        WindowCompat.setDecorFitsSystemWindows(window, true)
        super.onCreate(savedInstanceState)
        DriftRegistry.initialize(applicationContext)
        DriftStorage.initialize(applicationContext)
        setContent {
//                metro()
//                test7()

            learning()
//            MyScreen()
        }
    }}

// In MainActivity.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun hik() {
    DriftView(
        // 1. The Top Bar Slot
        topBar = {
            // You can use standard TopAppBar here safely now
            TopAppBar(
                title = { Text("Drift UI", style = MaterialTheme.typography.titleLarge) },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.shazan // Example: Transparent bar
                )

            )

        },

        // 2. The FAB Slot
        floatingAction = {
            FloatingActionButton(onClick = { /* ... */ }) {
                Text("+")
            }
        }
    ) {
        // 3. The Content (Automatically padded & safe)
        VStack(spacing = 20) {
            Text("This content is safe from the status bar.")
            Text("And it doesn't overlap the TopAppBar.")
        }
    }
}


