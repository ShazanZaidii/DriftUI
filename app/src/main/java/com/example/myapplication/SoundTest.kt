package com.example.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.driftui.core.DriftView
import com.example.driftui.core.VStack
import com.example.driftui.core.Button
import com.example.driftui.core.Haptic
import com.example.driftui.core.Text
import com.example.driftui.core.haptic
import com.example.driftui.core.padding
import com.example.driftui.core.playSound
import com.example.driftui.core.stopAllSounds
import com.example.driftui.core.stopSound
import com.example.driftui.core.warning

@Composable
fun play(){
    DriftView(blockBackgroundAudio = true){
        VStack() {
            Button(action = {
                playSound(
                    file = "chaloo.mp3",
                    pitch = 1.0,
                    pan = 1.0,
                    panEnd = -1.0,
                    backgroundPlay = false,
                    loop = false,
                    override = true
                )

            }) {
                Text("Play 1")
            }
            Button(action = {
                stopSound(file = "chaloo.mp3")
                playSound(
                    file = "lofi.mp3",
                    pitch = 1.0,
                    speed = 1.2,
                    pan = 1.0,
                    panEnd = -1.0,
                    backgroundPlay = false,
                    loop = true
                )

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