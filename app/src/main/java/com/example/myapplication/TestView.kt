package com.example.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.driftui.core.Button
import com.example.driftui.core.DriftView
import com.example.driftui.core.HStack
import com.example.driftui.core.Image
import com.example.driftui.core.RoundedRectangle
import com.example.driftui.core.State
import com.example.driftui.core.Storage
import com.example.driftui.core.Text
import com.example.driftui.core.TextField
import com.example.driftui.core.Toast
import com.example.driftui.core.UUID
import com.example.driftui.core.VStack
import com.example.driftui.core.blue
import com.example.driftui.core.clipShape
import com.example.driftui.core.duration
import com.example.driftui.core.foregroundStyle
import com.example.driftui.core.onEnd
import com.example.driftui.core.scaleEffect
import com.example.driftui.core.toastColor
import com.example.driftui.core.yellow

@Composable
fun test2() {
    var username = Storage(key = "username", defaultValue = "")
    val id = UUID()

    DriftView {
        VStack(spacing = 20) {
            TextField(placeholder = "Username", value = username)
            Text("Name is ${id}", foregroundStyle(Color.blue))
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
                        .onEnd { current.set(0) }
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