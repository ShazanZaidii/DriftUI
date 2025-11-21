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
import com.example.driftui.*

@Composable
fun TestView(){
    val showSheet = State(false)
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
        }.toolbarStyle(backgroundColor = Color.shazan)
    )  {
        val name = State("Shazan")
        DriftView() {
            VStack() {
                Text("Hello", Modifier.font(system(size = 28, weight = bold)))
                TextField(placeholder = "Enter any value", value = name, Modifier.frame(width = 250, height = 45).clipShape(RoundedRectangle(8)).background(Color.shazan))
                Text("${name.value}")
                SecureField(placeholder = "Enter any value", value = name, Modifier.frame(width = 250, height = 45).clipShape(RoundedRectangle(8)).background(Color.shazan))
                Text("${name.value}")
                Text("tap me", Modifier.onTapGesture(action = {showSheet.set(true)}))
            }
        }
        toolbar(Modifier.frame(height = 110)) {
            ToolbarItem(placement = ToolbarPlacement.Center) {
                Text("Toolbar", Modifier
                    .padding(top = 55)
                    .font(system(size = 28, weight = light)).foregroundStyle(Color.white))
            }
        }
    }


}