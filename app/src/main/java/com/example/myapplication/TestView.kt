package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
    val id = UUID()

    DriftView {
        VStack(spacing = 20) {
            TextField(placeholder = "Username",value = username)
            Text("Name is ${id}", Modifier.foregroundStyle(Color.blue))
        }
    }
}

@Composable
fun test3(){
    DriftView() {
        var current = remember { State(0) }
        VStack() {

            Button(action = {
                current.set(1)
            }) {
                Text("Button")

            }
            Button(action = {
                current.set(2)
            }) {
                Text("Button")

            }
            if (current.value == 1) {
                Toast("Hey, this is Shazan", Modifier.onEnd { current.set(0) })

            }
            if (current.value == 2) {
                Toast(
                    Modifier.clipShape(RoundedRectangle(radius = 12))
                        .toastColor(Color.yellow.copy(alpha = 0.2f))
                        .onEnd{ current.set(0)}
                        .duration(2.0)
                ) {
                    HStack() {
                        Image("pip_swap", Modifier.scaleEffect(2))
                        Text("This is an advanced toast")
                    }
                }

            }
        }
    }
}