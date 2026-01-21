package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import com.example.driftui.core.DriftRegistry
import com.example.driftui.core.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
//        WindowCompat.setDecorFitsSystemWindows(window, true)
        super.onCreate(savedInstanceState)
        DriftRegistry.initialize(applicationContext)
        DriftStorage.initialize(applicationContext)
        setContent {
//                metro()
//                test7()

            hik()
//            MyScreen()
        }
    }}

@Composable
fun hik(){
    var isPresented = State(false)
    DriftView() {
        VStack(Modifier.fillMaxSize().alignment(Alignment.TopStart)) {
            Text("Hey")
            Toggle(value = isPresented) {
                Text("Toggle")
            }
        }

    }
}



