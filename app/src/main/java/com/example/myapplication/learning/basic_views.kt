package com.example.myapplication.learning


import android.graphics.Paint
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.*

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.*

import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.material3.*
import androidx.compose.runtime.remember
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale

import com.example.myapplication.R

import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import coil.compose.AsyncImage
import com.example.driftui.core.*




@Composable
fun learning() {
    val progress = remember { State(0) }
    DriftView(useSafeArea = false) {

        Box(Modifier.fillMaxSize()) {
            // 1. Background (Unchanged)
            Rectangle(
                width = 45, height = 45, // These values don't matter due to fillMaxSize
                modifier = Modifier
                    .fillMaxSize()
                    .foregroundStyle(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Red.copy(0.1f),
                                Color.Gray.copy(0.5f),
                                Color.White.copy(0.3f)
                            )
                        )
                    )
            )

            // 2. The Main Layout Column
            // Instead of a Box with padding, we use a Column that fills the screen.
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = scaledDp(24), vertical = scaledDp(16)), // Global breathing room
                horizontalAlignment = Alignment.CenterHorizontally, // Center everything L/R
                verticalArrangement = Arrangement.SpaceBetween // Push content to edges or center dynamically
            ) {

                // --- HEADER ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween // Pushes icons to far edges
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Back",
                        tint = Color.White.copy(0.78f),
                        modifier = Modifier.rotationEffect(-90).size(scaledDp(30)) // Slightly smaller icons for safety
                    )
                    Text(
                        "Playing From Album",
                        style = TextStyle(
                            color = Color.White.copy(0.78f),
                            fontSize = scaledSp(22), // Reduced font size for smaller screens
                            fontWeight = bold
                        )
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.PlaylistPlay,
                        contentDescription = "Playlist",
                        tint = Color.White.copy(0.78f),
                        modifier = Modifier.size(scaledDp(30))
                    )
                }

                // --- THE FIX: WEIGHTED SPACER ---
                // This invisible spring pushes the image down, but shrinks if screen is small
                Spacer(modifier = Modifier.weight(1f))

                // --- ALBUM ART ---
                AsyncImage(
                    model = "https://picsum.photos/1400", // Request higher res, let Compose scale it down
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth(1f) // Take 100% of available width (minus padding)
                        .aspectRatio(1f)  // FORCE it to stay Square (1:1 ratio)
                        .clip(RoundedRectangle(radius = 12))
                    // No fixed size! It grows/shrinks with the phone width.
                )

                // --- INFO SECTION ---
                Spacer(modifier = Modifier.height(scaledDp(24))) // Fixed spacing between image and text is okay

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("+", style = TextStyle(color = Color.White, fontSize = scaledSp(32)))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Chernobyl",
                            style = TextStyle(color = Color.White, fontSize = scaledSp(28), fontWeight = bold),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            "Apalca Blue",
                            style = TextStyle(color = Color.White.copy(0.7f), fontSize = scaledSp(16)),
                            textAlign = TextAlign.Center
                        )
                    }

                    Text("...", style = TextStyle(color = Color.White, fontSize = scaledSp(32)))
                }

                // --- SLIDER ---
                Slider(
                    value = progress,
                    range = 0..100,
                    thickness = 6, // Slightly thinner looks cleaner
                    trackColor = gaugeColor(Color.White.copy(0.2f)),
                    fillColor = gaugeColor(Color.White),
                    tracker = {
                        Circle(
                            radius = 8,
                            modifier = Modifier.foregroundStyle(Color.White)
                        )
                    },
                    modifier = Modifier.padding(vertical = scaledDp(24))
                )

                // --- CONTROLS ---
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = scaledDp(20)),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly // Distributes buttons perfectly
                ) {
                    Icon(Icons.Default.Shuffle, "Shuffle", tint = Color.White, modifier = Modifier.size(scaledDp(28)))
                    Icon(Icons.Default.SkipPrevious, "Prev", tint = Color.White, modifier = Modifier.size(scaledDp(45)))

                    // Play Button Circle
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.PauseCircleFilled, "Pause", tint = Color.White, modifier = Modifier.size(scaledDp(75)))
                    }

                    Icon(Icons.Default.SkipNext, "Next", tint = Color.White, modifier = Modifier.size(scaledDp(45)))
                    Icon(Icons.Default.Loop, "Loop", tint = Color.White, modifier = Modifier.size(scaledDp(28)))
                }

                Spacer(modifier = Modifier.weight(1f)) // Bottom spring
            }
        }
    }
}
