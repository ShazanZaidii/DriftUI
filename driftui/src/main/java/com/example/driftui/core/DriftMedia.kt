package com.example.driftui.core

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.util.ArrayList
import java.util.UUID
import kotlin.math.max

// =============================================================================================
// PART 1: THE BACKEND LOGIC (Media & PDF)
// =============================================================================================

object DriftMedia {

    fun save(context: Context, uri: Uri, isVideo: Boolean): String? {
        return try {
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(uri)
            val defaultExt = if (isVideo) "mp4" else "jpg"
            val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: defaultExt
            val fileName = "drift_${UUID.randomUUID()}.$extension"
            val file = File(context.filesDir, fileName)

            contentResolver.openInputStream(uri)?.use { input ->
                file.outputStream().use { output -> input.copyTo(output) }
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun view(context: Context, path: String, isVideo: Boolean) {
        val file = File(path)
        if (!file.exists()) return
        val extension = file.extension
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            ?: if (isVideo) "video/*" else "image/*"

        val uri = try {
            FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        } catch (e: Exception) { return }

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Open with..."))
    }
}

// =============================================================================================
// DSL HOOKS (Pickers)
// =============================================================================================

@Composable
fun useImagePicker(onResult: (String) -> Unit): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val path = DriftMedia.save(context, it, isVideo = false)
            if (path != null) onResult(path)
        }
    }
    return remember { { launcher.launch("image/*") } }
}

@Composable
fun useVideoPicker(onResult: (String) -> Unit): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val path = DriftMedia.save(context, it, isVideo = true)
            if (path != null) onResult(path)
        }
    }
    return remember { { launcher.launch("video/*") } }
}

@Composable
fun useMediaViewer(): (String, Boolean) -> Unit {
    val context = LocalContext.current
    return remember { { path, isVideo -> DriftMedia.view(context, path, isVideo) } }
}


// =============================================================================================
// PDF ENGINE (ROBUST WRAPPING & LAYOUT)
// =============================================================================================

object DriftPdf {

    class PdfSession(
        private val document: PdfDocument,
        val width: Int,
        val height: Int
    ) {
        private var pageNumber = 0
        private var currentPage: PdfDocument.Page? = null

        // Cursor tracking
        var y = 40f
        val margin = 40f
        val contentWidth = width - (margin * 2)

        val canvas: Canvas
            get() {
                if (currentPage == null) newPage()
                return currentPage!!.canvas
            }

        fun newPage() {
            currentPage?.let { document.finishPage(it) }
            pageNumber++
            val info = PdfDocument.PageInfo.Builder(width, height, pageNumber).create()
            currentPage = document.startPage(info)
            y = margin
        }

        fun ensureSpace(neededHeight: Float) {
            if (y + neededHeight > height - margin) {
                newPage()
            }
        }

        fun space(pixels: Float) {
            y += pixels
            if (y > height - margin) newPage()
        }

        fun finish() {
            currentPage?.let {
                document.finishPage(it)
                currentPage = null
            }
        }

        fun drawTextLine(text: String, paint: Paint) {
            val lineHeight = paint.textSize + 4f
            ensureSpace(lineHeight)
            canvas.drawText(text, margin, y + lineHeight - 4, paint)
            y += lineHeight + 4f
        }

        /**
         * Draws a fully wrapped table.
         * Calculates row heights dynamically based on content.
         */
        fun drawTable(
            headers: List<String>,
            data: List<List<String>>,
            headerPaint: Paint,
            textPaint: Paint,
            linePaint: Paint
        ) {
            val colCount = headers.size
            val colWidth = contentWidth / colCount.toFloat()
            val padding = 4f

            // 1. Headers
            val headerHeight = headerPaint.textSize + 10f
            ensureSpace(headerHeight)
            var x = margin
            headers.forEach { header ->
                canvas.drawText(header, x + padding, y + headerPaint.textSize, headerPaint)
                x += colWidth
            }
            y += headerHeight
            canvas.drawLine(margin, y, margin + contentWidth, y, linePaint)
            y += 5f

            // 2. Data Rows
            val lineHeight = textPaint.textSize + 5f

            data.forEach { row ->
                // A. Calculate Max Height for this row
                // We split each cell into lines based on wrapping width
                val cellLines = row.map { cellText ->
                    val lines = mutableListOf<String>()
                    val safeText = if (cellText.isBlank()) "-" else cellText
                    val paragraphs = safeText.split("\n") // Handle explicit newlines

                    paragraphs.forEach { para ->
                        var start = 0
                        while (start < para.length) {
                            // Calculate how many chars fit in one column line
                            val count = textPaint.breakText(
                                para,
                                start,
                                para.length,
                                true,
                                colWidth - (padding * 2),
                                null
                            )
                            lines.add(para.substring(start, start + count))
                            start += count
                        }
                    }
                    lines
                }

                // The row height is determined by the cell with the most lines
                val maxLines = cellLines.maxOfOrNull { it.size } ?: 1
                val rowHeight = (maxLines * lineHeight) + 10f

                ensureSpace(rowHeight)

                // B. Draw Cells
                var colX = margin
                cellLines.forEach { lines ->
                    var lineY = y + lineHeight - 5f // baseline adjustment
                    lines.forEach { line ->
                        canvas.drawText(line, colX + padding, lineY, textPaint)
                        lineY += lineHeight
                    }
                    colX += colWidth
                }

                // C. Draw Divider & Advance
                y += rowHeight
                canvas.drawLine(margin, y, margin + contentWidth, y, linePaint)
                y += 5f
            }
        }

        fun drawField(label: String, value: String, labelPaint: Paint, valuePaint: Paint) {
            val labelColWidth = 140f
            val colSpacing = 15f
            val valueColX = margin + labelColWidth + colSpacing
            val valueMaxWidth = contentWidth - labelColWidth - colSpacing

            val labelLineHeight = labelPaint.textSize + 6f
            val valueLineHeight = valuePaint.textSize + 6f

            val labelLines = label.split("\n")
            val valueLines = mutableListOf<String>()
            val paragraphs = (if (value.isBlank()) "N/A" else value).split("\n")

            paragraphs.forEach { para ->
                if (para.isEmpty()) {
                    valueLines.add("")
                    return@forEach
                }
                var start = 0
                while (start < para.length) {
                    val count = valuePaint.breakText(para, start, para.length, true, valueMaxWidth, null)
                    valueLines.add(para.substring(start, start + count))
                    start += count
                }
            }

            val totalLines = max(labelLines.size, valueLines.size)

            for (i in 0 until totalLines) {
                val currentLineHeight = max(
                    if (i < labelLines.size) labelLineHeight else 0f,
                    if (i < valueLines.size) valueLineHeight else 0f
                )
                ensureSpace(currentLineHeight)

                if (i < labelLines.size) {
                    canvas.drawText(labelLines[i], margin, y + labelLineHeight - 6, labelPaint)
                }
                if (i < valueLines.size) {
                    canvas.drawText(valueLines[i], valueColX, y + valueLineHeight - 6, valuePaint)
                }
                y += currentLineHeight
            }
            space(12f)
        }
    }

    // --- SINGLE SHARE ---
    fun <T> share(
        context: Context,
        fileName: String,
        item: T,
        onDraw: PdfSession.(T) -> Unit
    ) {
        val pdfDocument = PdfDocument()
        val session = PdfSession(pdfDocument, 595, 842)

        try {
            session.onDraw(item)
            session.finish()

            val file = File(context.filesDir, fileName)
            pdfDocument.writeTo(FileOutputStream(file))

            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share PDF"))

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            pdfDocument.close()
        }
    }

    // --- BATCH SHARE ---
    fun <T> shareMultiple(
        context: Context,
        items: List<T>,
        nameGenerator: (T) -> String,
        onDraw: PdfSession.(T) -> Unit
    ) {
        if (items.isEmpty()) return
        val uris = ArrayList<Uri>()

        items.forEach { item ->
            val pdfDocument = PdfDocument()
            val session = PdfSession(pdfDocument, 595, 842)
            try {
                session.onDraw(item)
                session.finish()

                val safeName = nameGenerator(item).replace("[^a-zA-Z0-9.-]".toRegex(), "_") + ".pdf"
                val file = File(context.filesDir, safeName)

                pdfDocument.writeTo(FileOutputStream(file))

                val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                uris.add(uri)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pdfDocument.close()
            }
        }

        if (uris.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                type = "application/pdf"
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share ${uris.size} PDFs"))
        }
    }
}