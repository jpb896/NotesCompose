package com.example.cahier.features.drawing

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.cahier.features.drawing.viewmodel.DrawingCanvasViewModel

@Composable
fun DrawingToolbox(
    drawingCanvasViewModel: DrawingCanvasViewModel,
    imagePickerLauncher: ActivityResultLauncher<PickVisualMediaRequest>,
    canUndo: Boolean,
    canRedo: Boolean,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onExit: () -> Unit,
    onEditActiveBrush: () -> Unit,
    onColorPickerClick: () -> Unit,
    modifier: Modifier = Modifier,
    isVertical: Boolean = false
) {
    if (isVertical) {
        // Keeps your existing vertical layout logic for landscape mode
        VerticalToolbox(
            drawingCanvasViewModel = drawingCanvasViewModel,
            imagePickerLauncher = imagePickerLauncher,
            canUndo = canUndo,
            canRedo = canRedo,
            onUndo = onUndo,
            onRedo = onRedo,
            onExit = onExit,
            onEditActiveBrush = onEditActiveBrush,
            onColorPickerClick = onColorPickerClick,
            modifier = modifier
        )
    } else {
        // Modern floating horizontal bar for portrait mode
        ModernDrawingToolbox(
            drawingCanvasViewModel = drawingCanvasViewModel,
            imagePickerLauncher = imagePickerLauncher,
            canUndo = canUndo,
            canRedo = canRedo,
            onUndo = onUndo,
            onRedo = onRedo,
            onExit = onExit,
            onEditActiveBrush = onEditActiveBrush,
            onColorPickerClick = onColorPickerClick,
            modifier = modifier
        )
    }
}