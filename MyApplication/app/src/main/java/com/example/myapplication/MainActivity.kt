package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
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
        setContent {

        TestView()

        }
    }
}



@Composable
fun MyScreen() {
    val showSheet = State(false)
    var quantity = State(50)


    NavigationStack(
        Modifier.sheet(
            isPresented = showSheet,
            detents = listOf( 0.5, 0.95),
            initialDetent = 0.5,
            showGrabber = true,
            cornerRadius = 20,
            allowDismiss = true
        ) {
            // Sheet content
            VStack(spacing = 20) {
                Text("Sheet Content", Modifier.font(system(size = 24, weight = bold)))
                Text("Current state: ${showSheet.value}")

                Button(action = { showSheet.set(false) }, Modifier.offset(x = 160, y = -100)) {
                    Text("CLOSE X", Modifier.font(system(size = 18)))
                }

                Text("Swipe down or tap outside to dismiss")

            }
        }
    ) {
        // Main screen content
        DriftView() {


                DriftView {
                    val color = Color.magenta
                    var value by State(50)
                    HStack {
                        ZStack {
                            RoundedRectangle(width = 50, height = 50, cornerRadius = 8, Modifier.foregroundStyle(color))
                            Button(action = {
//                        value.set(value.value + 1)
                                value = value - 1
                            }) {
                                Text("-", Modifier.font(system(size = 27, weight = light)))
                            }
                        }

                        Text("${value}")
                        ZStack {
                            RoundedRectangle(width = 50, height = 50, cornerRadius = 8, Modifier.foregroundStyle(color))
                            Button(action = {
//                        value.set(value.value + 1)
                                value = value + 1
                            }) {
                                Text("+")
                            }
                        }
                    }

                }
            }
        }

        }




