package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment.Companion.Rectangle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.zIndex
import androidx.core.view.WindowCompat
import com.example.driftui.DriftView
import com.example.driftui.NavigationStack
import com.example.driftui.VStack
import com.example.driftui.darkMode
import com.example.driftui.lightMode
import com.example.driftui.*

@Composable
fun play(){
    DriftView(blockBackgroundAudio = true){
        VStack() {
            Button(action = {
                playSound(file = "chaloo.mp3", pitch = 1.0, pan = 1.0, panEnd = -1.0,backgroundPlay = false, loop = false, override = true)

            }) {
                Text("Play 1")
            }
            Button(action = {
                stopSound(file = "chaloo.mp3")
                playSound(file = "lofi.mp3", pitch = 1.0, speed = 1.2, pan = 1.0, panEnd = -1.0,backgroundPlay = false, loop = true)

            }) {

                Text("Play 2")
            }
            Button(action = {
                stopAllSounds()
            }, Modifier.padding(top = 120)) {
                Text("Stop All Sounds")
            }

            Button(action = {
                haptic(Haptic.Selection)
            }, Modifier.padding(top = 120)) {
                Text("Haptic1")
            }

            Button(action = {
                haptic(Haptic.Light)
            }) {
                Text("Haptic2")
            }


            Button(action = {
                haptic(Haptic.Medium)
            }) {
                Text("Haptic3")
            }

            Button(action = {
                haptic(Haptic.Heavy)
            }) {
                Text("Haptic4")
            }
            Button(action = {
                haptic(Haptic.Success)
            }) {
                Text("Haptic5")
            }
            Button(action = {
                haptic(warning)
            }) {
                Text("Haptic6")
            }
            Button(action = {
                haptic(Haptic.Error)
            }) {
                Text("Haptic7")
            }



        }

    }


}