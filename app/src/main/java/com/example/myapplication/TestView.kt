package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment.Companion.Rectangle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.zIndex
import androidx.core.view.WindowCompat
import com.example.driftui.*

@Composable
fun test2() {
    var username = Storage(key = "username", defaultValue = "")

    DriftView {
        VStack(spacing = 20) {
            TextField(placeholder = "Username",value = username)
            Text("Name is ${username.value}")
        }
    }
}