package com.example.driftui.core

import android.content.ContentValues
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.UUID

// =============================================================================================
// UTILS
// =============================================================================================

fun UUID(): UUID = UUID.randomUUID()

// =============================================================================================
// SWAP
// =============================================================================================

// Safely swap the values of two standard native states
fun <T> swap(a: MutableState<T>, b: MutableState<T>) {
    val temp = a.value
    a.value = b.value
    b.value = temp
}

// =============================================================================================
// MVVM (SwiftUI-like ViewModels)
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
    return viewModel<T>(storeOwner)
}

@Composable
inline fun <reified T : ObservableObject> ObservedObject(noinline factory: () -> T): T {
    return remember { factory() }
}

@Composable
inline fun <reified T : ObservableObject> SharedObject(): T {
    val context = LocalContext.current
    val activity = remember(context) { findActivity(context) }
    return viewModel(activity as ViewModelStoreOwner)
}

// Helper to find the Activity from any Context
fun findActivity(context: Context): ComponentActivity {
    var ctx = context
    while (ctx is ContextWrapper) {
        if (ctx is ComponentActivity) return ctx
        ctx = ctx.baseContext
    }
    error("SharedObject must be used inside an Activity")
}

// =============================================================================================
// SAVING UTILS (Gallery)
// =============================================================================================

fun savePathToGallery(context: Context, path: Path, width: Int, height: Int): Boolean {
    try {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(android.graphics.Color.WHITE)

        val paint = Paint().apply {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            isAntiAlias = true
        }

        for (stroke in path.strokes) {
            if (stroke.points.isEmpty()) continue

            paint.color = stroke.color.toArgb()
            paint.strokeWidth = stroke.width

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

// =============================================================================================
// DRIFT DRAW CONTROLLER (Rewritten for Standard Compose State)
// =============================================================================================

@Composable
fun DrawController(): DriftDrawController {
    val context = LocalContext.current
    return remember { DriftDrawController(context) }
}

class DriftDrawController(private val context: Context) {

    // 1. Replaced State<T> with Compose's standard 'var by mutableStateOf'
    // This allows you to interact with these variables directly without needing '.value' or '.set()'
    var path by mutableStateOf(Path())
    var color by mutableStateOf(Color.Black)
    var width by mutableStateOf(5f)
    var eraser by mutableStateOf(false)
    var eraserType by mutableStateOf(EraserType.Area)

    private val undoStack = mutableListOf<Path>()
    private val redoStack = mutableListOf<Path>()

    fun undo() {
        if (undoStack.isNotEmpty()) {
            redoStack.add(path.copy())
            path = undoStack.removeAt(undoStack.lastIndex)
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            undoStack.add(path.copy())
            path = redoStack.removeAt(redoStack.lastIndex)
        }
    }

    fun save() {
        val success = savePathToGallery(context, path, 1080, 1920)
        val msg = if (success) "Saved to Gallery!" else "Failed to save."
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    fun toggleEraser() {
        eraser = !eraser
    }

    fun clear() {
        snapshot()
        path.clear()
        path = Path() // Trigger refresh
    }

    fun snapshot() {
        undoStack.add(path.copy())
        redoStack.clear()
    }
}