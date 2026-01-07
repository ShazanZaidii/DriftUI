package com.example.driftui.core

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.abs

// 1. New Data Class to hold individual stroke details
data class StrokeData(
    val points: MutableList<Offset>,
    val color: Color,
    val width: Float
)

class Path {
    // 2. Store StrokeData instead of just lists of points
    private val _strokes = mutableListOf<StrokeData>()

    val strokes: List<StrokeData> get() = _strokes

    private var currentStroke: StrokeData? = null

    // 3. Updated start() to accept color and width
    fun start(at: Offset, color: Color, width: Float) {
        val newStroke = StrokeData(mutableListOf(at), color, width)
        _strokes.add(newStroke)
        currentStroke = newStroke
    }

    fun lineTo(p: Offset) {
        currentStroke?.points?.add(p)
    }

    fun finish() {
        currentStroke = null
    }

    fun clear() {
        _strokes.clear()
        currentStroke = null
    }

    fun copy(): Path {
        val newPath = Path()
        for (stroke in _strokes) {
            // Deep copy the points, preserve color and width
            newPath._strokes.add(
                StrokeData(
                    stroke.points.toMutableList(),
                    stroke.color,
                    stroke.width
                )
            )
        }
        return newPath
    }

    fun removeStrokeAt(point: Offset, radius: Float): Boolean {
        val iterator = _strokes.iterator()
        var wasRemoved = false
        val radiusSq = radius * radius

        while (iterator.hasNext()) {
            val stroke = iterator.next()
            val isHit = stroke.points.any { strokePoint ->
                val dx = strokePoint.x - point.x
                val dy = strokePoint.y - point.y
                (dx * dx + dy * dy) <= radiusSq
            }

            if (isHit) {
                iterator.remove()
                wasRemoved = true
            }
        }
        return wasRemoved
    }

    fun eraseAreaAt(eraserPos: Offset, radius: Float): Boolean {
        val newStrokes = mutableListOf<StrokeData>()
        var hasChanged = false
        val radiusSq = radius * radius

        for (stroke in _strokes) {
            // Fast Check
            if (stroke.points.isNotEmpty()) {
                val first = stroke.points.first()
                if (abs(first.x - eraserPos.x) > radius + 500 &&
                    abs(first.y - eraserPos.y) > radius + 500) {
                    newStrokes.add(stroke)
                    continue
                }
            }

            var currentSegmentPoints = mutableListOf<Offset>()

            for (point in stroke.points) {
                val dx = point.x - eraserPos.x
                val dy = point.y - eraserPos.y

                if (dx * dx + dy * dy > radiusSq) {
                    currentSegmentPoints.add(point)
                } else {
                    // Cut happens here
                    if (currentSegmentPoints.isNotEmpty()) {
                        if (currentSegmentPoints.size >= 2) {
                            // Create new stroke preserving ORIGINAL color/width
                            newStrokes.add(StrokeData(currentSegmentPoints, stroke.color, stroke.width))
                        }
                        currentSegmentPoints = mutableListOf()
                    }
                    hasChanged = true
                }
            }

            if (currentSegmentPoints.isNotEmpty() && currentSegmentPoints.size >= 2) {
                newStrokes.add(StrokeData(currentSegmentPoints, stroke.color, stroke.width))
            }
        }

        if (hasChanged) {
            _strokes.clear()
            _strokes.addAll(newStrokes)
        }
        return hasChanged
    }

    fun smoothAllStrokes(): Path {
        val smoothedStrokes = mutableListOf<StrokeData>()

        for (stroke in _strokes) {
            val points = stroke.points
            if (points.size < 3) {
                smoothedStrokes.add(stroke)
                continue
            }

            val smoothed = mutableListOf<Offset>()
            smoothed.add(points.first())

            for (i in 1 until points.size - 1) {
                val prev = points[i - 1]
                val curr = points[i]
                val next = points[i + 1]
                val mid1 = Offset((prev.x + curr.x) / 2, (prev.y + curr.y) / 2)
                val mid2 = Offset((curr.x + next.x) / 2, (curr.y + next.y) / 2)
                smoothed.add(mid1)
                smoothed.add(mid2)
            }
            smoothed.add(points.last())

            // Reconstruct with smoothed points but same color/width
            smoothedStrokes.add(StrokeData(smoothed, stroke.color, stroke.width))
        }

        _strokes.clear()
        _strokes.addAll(smoothedStrokes)
        return this
    }
}