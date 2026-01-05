package com.example.myapplication

import android.app.Notification
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposableOpenTarget
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
//        WindowCompat.setDecorFitsSystemWindows(window, true)
        super.onCreate(savedInstanceState)
        DriftRegistry.initialize(applicationContext)
        DriftStorage.initialize(applicationContext)
        setContent {
//                metro()
                test7()

//            hik()
//            MyScreen()
        }
    }}

//@Composable
//fun hik(){
//    var isPresented = State(false)
//    DriftView() {
//        VStack() {
//            Text("Hey")
//            Toggle(value = isPresented) {
//                Text("Toggle")
//            }
//        }
//        SideMenu(isPresented = isPresented ) {
//            MenuItem() { }
//        }
//    }
//}



