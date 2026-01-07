package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.driftui.core.DriftRegistry
import com.example.driftui.core.DriftStorage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
//        WindowCompat.setDecorFitsSystemWindows(window, true)
        super.onCreate(savedInstanceState)
        DriftRegistry.initialize(applicationContext)
        DriftStorage.initialize(applicationContext)
        setContent {
                metro()
//                test7()

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



