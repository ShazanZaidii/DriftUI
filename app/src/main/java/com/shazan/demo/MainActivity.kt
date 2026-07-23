package com.shazan.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shazan.driftui.core.DriftSetup
import com.shazan.driftui.core.DriftTabNavigator
import com.shazan.driftui.core.StatusBar
import com.shazan.driftui.core.lightMode
import com.shazan.driftui.core.useNav
import com.shazan.driftui.core.useTabNav


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // A clean, monochrome theme base
            MaterialTheme(
                colorScheme = lightColorScheme(
                    background = Color.White,
                    surface = Color.White,
                    onBackground = Color.Black,
                    onSurface = Color.Black,
                    primary = Color.Black,
                    onPrimary = Color.White
                )
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    // 1. Wrap the entire application flow in DriftSetup
                    DriftSetup {
                        MainDashboardScreen()
                    }
                }
            }
        }
    }
}

// Defining tabs to use as enum:
enum class AppTab {
    HOME, SEARCH, PROFILE
}


// MAIN DASHBOARD (TAB NAVIGATION)

@Composable
fun MainDashboardScreen() {

    //To adjust theme on any screen. You can also use darkMode
    // [Optional, if you already have it managed globally then dont use.]
    StatusBar(lightMode)

    // 2. Initialize DriftTabNavigator
    DriftTabNavigator(initialTab = AppTab.HOME) { currentTab, setTab ->
        Column(modifier = Modifier.fillMaxSize()) {

            // Content Area
            Box(modifier = Modifier.weight(1f)) {
                when (currentTab) {
                    AppTab.HOME -> HomeTab()
                    AppTab.SEARCH -> SearchTab()
                    AppTab.PROFILE -> ProfileTab()
                }
            }

            // Clean, native-style Bottom Navigation Bar
            HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 16.dp, horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TabItem(title = "Home", isSelected = currentTab == AppTab.HOME) { setTab(AppTab.HOME) }
                TabItem(title = "Search", isSelected = currentTab == AppTab.SEARCH) { setTab(AppTab.SEARCH) }
                TabItem(title = "Profile", isSelected = currentTab == AppTab.PROFILE) { setTab(AppTab.PROFILE) }
            }
        }
    }
}

@Composable
fun TabItem(title: String, isSelected: Boolean, onClick: () -> Unit) {
    Text(
        text = title,
        color = if (isSelected) Color.Black else Color.Gray,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
        fontSize = 14.sp,
        modifier = Modifier.clickable(onClick = onClick)
    )
}

// TAB CONTENT SCREENS

@Composable
fun HomeTab() {
    // 3. Grab global screen navigation
    val nav = useNav()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Home Tab", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        // Pushing a new screen over the tabs
        Button(
            onClick = {
                nav.push(tag = "Detail") {
                    DetailScreen()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text("Push Detail Screen (nav.push)", color = Color.White)
        }
    }
}

@Composable
fun SearchTab() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Search Tab", fontSize = 28.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ProfileTab() {
    // 4. Grab global tab navigation to jump between tabs programmatically
    val tabNav = useTabNav()
    val nav = useNav()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Profile Tab", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        // Programmatic tab switching
        Button(
            onClick = {
                tabNav.push { AppTab.HOME }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text("Jump back to Home Tab ( nav.pop)", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 5. Replace Root completely (e.g., Logging out)
        Button(
            onClick = {
                nav.replaceRoot(tag = "Auth") {
                    LoggedOutScreen()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
        ) {
            Text("Log Out (Replace Root)", color = Color.White)
        }
    }
}

// FULL-SCREEN OVERLAYS (STANDARD NAVIGATION)

@Composable
fun DetailScreen() {
    val nav = useNav()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), // Solid background so it covers the TabNav below it
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Detail View", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text("Tabs are hidden below this layer.", color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
        Spacer(modifier = Modifier.height(32.dp))

        // 6. Pop back to previous view
        Button(
            onClick = { nav.pop() },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text("Go Back (nav.pop)", color = Color.White)
        }
    }
}

@Composable
fun LoggedOutScreen() {
    val nav = useNav()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Logged Out", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // Re-instantiate the Dashboard as the new root
                nav.replaceRoot(tag = "App") {
                    MainDashboardScreen()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text("Log In Again", color = Color.White)
        }
    }
}