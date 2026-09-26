package com.example.cahier.features.drawing

import android.graphics.Matrix
import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.ink.brush.Brush
import androidx.ink.brush.compose.createWithComposeColor
import androidx.ink.rendering.android.canvas.CanvasStrokeRenderer
import androidx.ink.strokes.Stroke
import com.example.cahier.core.ui.LocalTextureStore
import androidx.core.graphics.withMatrix

@Composable
fun DrawingDetailThumbnail(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    strokes: List<Stroke>? = null,
    strokesData: String? = null,
    overrideStrokeColor: Color? = null,
    paddingDp: Dp = 12.dp,
) {
    val textureStore = LocalTextureStore.current
    val cacheGen by textureStore.generation.collectAsState()
    val canvasStrokeRenderer = remember(cacheGen) {
        CanvasStrokeRenderer.create(textureStore)
    }

    val strokeColor = overrideStrokeColor ?: if (isSystemInDarkTheme()) Color.White else Color.Black

    val rawStrokes = remember(strokes, strokesData, strokeColor) {
        strokes ?: strokesData?.toStrokes(color = strokeColor.toArgb()) ?: emptyList()
    }

    val density = LocalDensity.current
    val paddingPx = remember(paddingDp, density) {
        with(density) { paddingDp.toPx() }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(onClick = onClick, role = Role.Button)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvas = drawContext.canvas.nativeCanvas
            val canvasWidth = size.width
            val canvasHeight = size.height

            if (canvasWidth > 0f && canvasHeight > 0f && rawStrokes.isNotEmpty()) {
                var minX = Float.MAX_VALUE
                var minY = Float.MAX_VALUE
                var maxX = -Float.MAX_VALUE
                var maxY = -Float.MAX_VALUE

                // 1. Accumulate stroke bounding boxes
                rawStrokes.forEach { stroke ->
                    val box = stroke.shape.computeBoundingBox()
                    if (box != null) {
                        minX = minOf(minX, box.xMin)
                        minY = minOf(minY, box.yMin)
                        maxX = maxOf(maxX, box.xMax)
                        maxY = maxOf(maxY, box.yMax)
                    }
                }

                val boundsWidth = maxX - minX
                val boundsHeight = maxY - minY

                if (boundsWidth > 0f && boundsHeight > 0f && minX < Float.MAX_VALUE) {
                    val availableWidth = (canvasWidth - paddingPx * 2f).coerceAtLeast(1f)
                    val availableHeight = (canvasHeight - paddingPx * 2f).coerceAtLeast(1f)

                    // Compute uniform scale
                    val scaleX = availableWidth / boundsWidth
                    val scaleY = availableHeight / boundsHeight
                    val scale = minOf(scaleX, scaleY)

                    // Compute offsets to center inside thumbnail
                    val offsetX = paddingPx + (availableWidth - boundsWidth * scale) / 2f - (minX * scale)
                    val offsetY = paddingPx + (availableHeight - boundsHeight * scale) / 2f - (minY * scale)

                    // Construct matrix manually via values matrix array
                    val transform = Matrix().apply {
                        setValues(
                            floatArrayOf(
                                scale, 0f, offsetX,
                                0f, scale, offsetY,
                                0f, 0f, 1f
                            )
                        )
                    }

                    // 2. Save canvas state and apply transform to canvas directly
                    canvas.withMatrix(transform) {
                        rawStrokes.forEach { stroke ->
                            val scaledBrushSize = (stroke.brush.size * scale).coerceAtLeast(1.5f)

                            val scaledStroke = Stroke(
                                brush = Brush.createWithComposeColor(
                                    family = stroke.brush.family,
                                    color = strokeColor,
                                    size = scaledBrushSize,
                                    epsilon = stroke.brush.epsilon
                                ),
                                inputs = stroke.inputs
                            )

                            // Pass Identity matrix so CanvasStrokeRenderer relies on native Canvas matrix
                            canvasStrokeRenderer.draw(
                                stroke = scaledStroke,
                                canvas = this,
                                strokeToScreenTransform = Matrix()
                            )
                        }

                    }
                }
            }
        }
    }
}