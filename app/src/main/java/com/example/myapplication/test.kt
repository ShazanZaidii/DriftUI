package com.example.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.driftui.*

@Composable
fun MyScreen() {
    val showSheet = State(false)

    NavigationStack(
        Modifier.sheet(
            isPresented = showSheet,
            detents = listOf(0.2, 0.5, 0.95),
            initialDetent = 0.2,
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
        DriftView {
            VStack(spacing = 20) {
                Button(action = { showSheet.toggle() }) {
                    Text("Show Sheet", Modifier.font(system(size = 28, weight = bold)))
                }

                if (showSheet.value) {
                    Text("Sheet is open!", Modifier.padding(top = 20))
                }

                Text("State: ${showSheet.value}")
            }
        }
    }
}

@Composable
fun test7(){
    DriftView() {
        val viewModel: MetroViewModel = StateObject()
        VStack(Modifier.frame(width = deviceWidth.value, height = deviceHeight.value).alignment(alignment = center)) {
            Text("heyyy", Modifier.foregroundStyle(Color.purple))
        }



    }
}