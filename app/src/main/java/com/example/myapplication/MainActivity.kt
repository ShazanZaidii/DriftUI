package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
// 1. CLEAN IMPORTS (Remove com.example.driftui.*)
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)
        setContent {
            // This starts the Voyager system
            Navigator(HomeScreen())
        }
    }
}

// ---------------------------------------------------------
// SCREENS
// ---------------------------------------------------------

class HomeScreen : Screen {
    @Composable
    override fun Content() {
        // Just calling the helper composable
        MySettingsButton()
    }
}

class SettingsScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Settings") }) }
        ) { padding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("You are on Settings Page")

                // Add a Back Button for clarity
                val navigator = LocalNavigator.currentOrThrow
                Button(onClick = { navigator.pop() }) {
                    Text("Go Back")
                }
            }
        }
    }
}

// ---------------------------------------------------------
// THE BUTTON (Fixed)
// ---------------------------------------------------------

@Composable
fun MySettingsButton() {
    // 2. FIX: Use Voyager's Navigator, NOT 'useNav()'
    val navigator = LocalNavigator.currentOrThrow

    Scaffold { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    // 3. DECISION TIME:
                    // Use .push() if you want to go BACK to Home later.
                    // Use .replace() if you want Home to be DESTROYED (Exit on back).

                    navigator.replaceAll(SettingsScreen())

                }
            ) {
                Text("Go to Settings")
            }
        }
    }
}