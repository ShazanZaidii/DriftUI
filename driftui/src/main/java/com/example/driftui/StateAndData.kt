package com.example.driftui
//This file is StateAndData.kt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.reflect.KProperty
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
// Removed RequiresApi import
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.asAndroidPath
import java.util.UUID




//UUID:
fun UUID(): UUID = UUID.randomUUID()

// -----------------------------------------
// STATE<T>
// -----------------------------------------

class Binding<T>(
    val value: T,
    val set: (T) -> Unit
)

class State<T>(initial: T) {
    private val s = mutableStateOf(initial)

    val value get() = s.value

    operator fun getValue(thisRef: Any?, property: KProperty<*>) = s.value
    operator fun setValue(thisRef: Any?, property: KProperty<*>, v: T) { s.value = v }

    fun set(v: T) { s.value = v }

    fun binding() = Binding(s.value) { s.value = it }

    fun toggle() {
        if (s.value is Boolean) {
            // Safe cast and flip the value
            @Suppress("UNCHECKED_CAST")
            s.value = !(s.value as Boolean) as T
        } else {
            // Optional: Log an error or ignore if not a Boolean state
            println("Drift UI Error: .toggle() called on non-Boolean State.")
        }
    }
    fun getSetter(): (T) -> Unit = { newValue ->
        s.value = newValue
    }


}

// -----------------------------------------
// Published<T>
// -----------------------------------------

typealias Published<T> = State<T>


// =============================================================================================
// MVVM (SwiftUI-like)
// =============================================================================================


open class ObservableObject : ViewModel()

@Composable
inline fun <reified T : ObservableObject> StateObject(): T {
    return viewModel<T>()
}

@Composable
inline fun <reified T : ObservableObject> EnvironmentObject(): T {

    val lifecycleOwner = LocalLifecycleOwner.current
    val storeOwner = lifecycleOwner as ViewModelStoreOwner

    // NOTE: Your Compose version supports viewModel(owner)
    return viewModel<T>(storeOwner)
}

@Composable
inline fun <reified T : ObservableObject> ObservedObject(noinline factory: () -> T): T {
    return remember { factory() }
}

// Save to gallery:

fun savePathToGallery(context: Context, path: Path, width: Int, height: Int): Boolean {
    try {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)
        canvas.drawColor(android.graphics.Color.WHITE)

        val paint = android.graphics.Paint().apply {
            style = android.graphics.Paint.Style.STROKE
            strokeCap = android.graphics.Paint.Cap.ROUND
            strokeJoin = android.graphics.Paint.Join.ROUND
            isAntiAlias = true
        }

        // Iterate through strokes and change paint color for each one
        for (stroke in path.strokes) {
            if (stroke.points.isEmpty()) continue

            // Update Paint Color/Width
            paint.color = stroke.color.toArgb()
            paint.strokeWidth = stroke.width

            // Create Path
            val androidPath = android.graphics.Path()
            androidPath.moveTo(stroke.points.first().x, stroke.points.first().y)
            for (p in stroke.points.drop(1)) {
                androidPath.lineTo(p.x, p.y)
            }

            canvas.drawPath(androidPath, paint)
        }

        val filename = "DriftUI_Sketch_${System.currentTimeMillis()}.png"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        if (uri != null) {
            context.contentResolver.openOutputStream(uri).use { out ->
                if (out != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
            }
            return true
        }
        return false
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }
}

// ... (Your existing State/Binding code) ...
// ... (Your existing savePathToGallery function) ...

// =============================================================================================
// DRIFT DRAW CONTROLLER (The Brain)
// =============================================================================================

@Composable
fun DrawController(): DriftDrawController {
    // We capture the Context here so the developer doesn't have to
    val context = androidx.compose.ui.platform.LocalContext.current
    return remember { DriftDrawController(context) }
}

class DriftDrawController(private val context: Context) {

    // --- PUBLIC STATE (For UI binding) ---
    val path = State(Path())
    val color = State(Color.Black)
    val width = State(5f)
    val eraser = State(false)
    val eraserType = State(EraserType.Area) // Default to "Real" eraser

    // --- INTERNAL HISTORY ---
    private val undoStack = mutableListOf<Path>()
    private val redoStack = mutableListOf<Path>()

    // --- ACTIONS ---

    // FIX: Removed @RequiresApi and used standard Kotlin List functions
    fun undo() {
        if (undoStack.isNotEmpty()) {
            // Save current state to Redo stack
            redoStack.add(path.value.copy())

            // Pop from Undo stack using index (Works on ALL Android versions)
            val previous = undoStack.removeAt(undoStack.lastIndex)

            path.set(previous)
        }
    }

    // FIX: Removed @RequiresApi and used standard Kotlin List functions
    fun redo() {
        if (redoStack.isNotEmpty()) {
            // Save current state to Undo stack
            undoStack.add(path.value.copy())

            // Pop from Redo stack using index (Works on ALL Android versions)
            val next = redoStack.removeAt(redoStack.lastIndex)

            path.set(next)
        }
    }

    fun save() {
        val success = savePathToGallery(context, path.value, 1080, 1920)
        val msg = if (success) "Saved to Gallery!" else "Failed to save."
        android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show()
    }

    fun toggleEraser() {
        eraser.toggle()
    }

    fun clear() {
        snapshot() // Save history before clearing
        path.value.clear()
        path.set(Path()) // Trigger refresh
    }

    // --- INTERNAL HELPER ---
    // Called automatically by DriftCanvas when you lift your finger
    fun snapshot() {
        undoStack.add(path.value.copy())
        // Once we draw something new, the "Redo" future is invalid
        redoStack.clear()
    }
}