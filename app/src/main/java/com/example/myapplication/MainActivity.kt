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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
//        WindowCompat.setDecorFitsSystemWindows(window, true)
        super.onCreate(savedInstanceState)
        setContent {

        play()

        }
    }
}




@Composable
fun DrawingScreen2() {

    // 1. INITIALIZATION: One line. No 'remember', no 'Context'.
    val controller = DrawController()

    DriftView {
        VStack(spacing = 20) {

            // -----------------------------
            // TOP BAR
            // -----------------------------
            HStack(Modifier.padding(horizontal = 20, vertical = 120)) {
                Text("Simple Draw", Modifier.font(system(24, bold)))
                Spacer()

                // Human-readable actions
                Button(action = { controller.undo() }) {
                    Text("Undo", Modifier.foregroundStyle(Color.blue))
                }

                Button(action = { controller.redo() }) {
                    Text("Redo", Modifier.foregroundStyle(Color.blue))
                }

                // "save()" handles permissions, bitmap creation, and context automatically
                Button(action = { controller.save() }) {
                    Text("Save", Modifier.foregroundStyle(Color.green))
                }
            }

            // -----------------------------
            // THE CANVAS
            // -----------------------------
            // The controller handles the Pen/Eraser layering logic internally.
            DriftCanvas(
                controller = controller,
                Modifier
                    .weight(1f)
                    .frame(450, height = 800)
                    .padding(bottom = 120)
                    .background(Color.white)
                    .cornerRadius(20)
                    .shadow(2)
            )

            // -----------------------------
// CONTROLS
// -----------------------------
            HStack(spacing = 20, modifier = Modifier.padding(bottom = 30)) {

                // 1. Color Picker (Visible when drawing)
                if (!controller.eraser.value) {
                    ColorPicker(selectedColor = controller.color)
                } else {
                    // 2. Eraser Type Selector (Visible ONLY when erasing)
                    // This is where you use the Types!
                    HStack(spacing = 10) {
                        Button(action = { controller.eraserType.set(EraserType.Area) }) {
                            Text(
                                "Real Eraser",
                                // Highlight if selected
                                Modifier.foregroundStyle(if(controller.eraserType.value == EraserType.Area) Color.black else Color.gray)
                            )
                        }

                        // Divider
                        Capsule(width = 1, height = 20, Modifier.background(Color.lightGray))

                        Button(action = { controller.eraserType.set(EraserType.Line) }) {
                            Text(
                                "Line Eraser",
                                // Highlight if selected
                                Modifier.foregroundStyle(if(controller.eraserType.value == EraserType.Line) Color.black else Color.gray)
                            )
                        }
                    }
                }

                Spacer()

                // 3. Main Toggle (The On/Off Switch)
                Button(action = { controller.toggleEraser() }) {
                    VStack {
                        Capsule(width = 40, height = 4,
                            Modifier.foregroundStyle(if (controller.eraser.value) Color.red else Color.gray))
                        Text("Eraser", Modifier.foregroundStyle(Color.gray))
                    }
                }
            }
        }
    }
}



