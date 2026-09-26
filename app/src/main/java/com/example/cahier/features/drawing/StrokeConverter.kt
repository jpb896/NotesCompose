package com.example.cahier.features.drawing

import android.util.Base64
import androidx.ink.brush.Brush
import androidx.ink.brush.StockBrushes
import androidx.ink.storage.StrokeInputBatchSerialization
import androidx.ink.strokes.Stroke
import androidx.ink.strokes.StrokeInputBatch
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.util.zip.GZIPInputStream

fun ByteArray.toStrokes(
    color: Int, defaultBrush: Brush = Brush.createWithColorIntArgb(
        family = StockBrushes.pressurePen(),
        colorIntArgb = color,
        size = 10f,
        epsilon = 1f
    )
): List<Stroke> {
    if (this.isEmpty()) return emptyList()

    return try {
        // Decode the raw byte array into a StrokeInputBatch using the Ink Storage API
        val inputBatch = StrokeInputBatchSerialization.decode(this)
        listOf(
            Stroke(
                brush = defaultBrush,
                inputs = inputBatch
            )
        )
    } catch (e: Exception) {
        emptyList()
    }
}

public fun String.toStrokes(
    color: Int = android.graphics.Color.BLACK,
    defaultBrush: Brush = Brush.createWithColorIntArgb(
        family = StockBrushes.pressurePen(),
        colorIntArgb = color,
        size = 10f,
        epsilon = 0.1f
    )
): List<Stroke> {
    if (this.isBlank()) return emptyList()

    val strokeList = mutableListOf<Stroke>()

    try {
        val jsonArray = JSONArray(this)

        for (i in 0 until jsonArray.length()) {
            val strokeObjectString = jsonArray.getString(i)
            val strokeJson = JSONObject(strokeObjectString)

            if (strokeJson.has("inputs")) {
                val inputsArray = strokeJson.getJSONArray("inputs")
                val rawBytes = ByteArray(inputsArray.length()) { index ->
                    inputsArray.getInt(index).toByte()
                }

                // Decode directly without pre-decompressing
                try {
                    val inputBatch: StrokeInputBatch = StrokeInputBatchSerialization.decode(rawBytes)
                    if (inputBatch.size > 0) {
                        val stroke = Stroke(
                            brush = defaultBrush,
                            inputs = inputBatch
                        )
                        strokeList.add(stroke)
                    }
                } catch (e: Exception) {
                    android.util.Log.e("DEBUG_THUMBNAIL", "Failed to decode batch item $i: ${e.message}")
                }
            }
        }
    } catch (e: Exception) {
        android.util.Log.e("DEBUG_THUMBNAIL", "Failed to parse JSON stroke data: ${e.message}", e)
    }

    return strokeList
}