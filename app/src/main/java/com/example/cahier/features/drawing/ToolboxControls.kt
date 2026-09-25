package com.example.cahier.features.drawing

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cahier.R
import com.example.cahier.features.drawing.viewmodel.DrawingCanvasViewModel

@Composable
fun ToolboxBrushControls(
    drawingCanvasViewModel: DrawingCanvasViewModel,
    isVertical: Boolean,
    onColorPickerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isEraserMode by drawingCanvasViewModel.isEraserMode.collectAsStateWithLifecycle()

    if (isVertical) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = { drawingCanvasViewModel.setEraserMode(false) }) {
                Icon(
                    painter = painterResource(R.drawable.brush_24px),
                    contentDescription = stringResource(R.string.brush)
                )
            }
            IconButton(onClick = {
                drawingCanvasViewModel.setEraserMode(false)
                onColorPickerClick()
            }) {
                Icon(
                    painter = painterResource(R.drawable.palette_24px),
                    contentDescription = stringResource(R.string.color)
                )
            }
            IconButton(onClick = { drawingCanvasViewModel.setEraserMode(!isEraserMode) }) {
                Icon(
                    painter = painterResource(R.drawable.ink_eraser_24px),
                    contentDescription = stringResource(R.string.eraser)
                )
            }
        }
    } else {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = { drawingCanvasViewModel.setEraserMode(false) }) {
                Icon(
                    painter = painterResource(R.drawable.brush_24px),
                    contentDescription = stringResource(R.string.brush)
                )
            }
            IconButton(onClick = {
                drawingCanvasViewModel.setEraserMode(false)
                onColorPickerClick()
            }) {
                Icon(
                    painter = painterResource(R.drawable.palette_24px),
                    contentDescription = stringResource(R.string.color)
                )
            }
            IconButton(onClick = { drawingCanvasViewModel.setEraserMode(!isEraserMode) }) {
                Icon(
                    painter = painterResource(R.drawable.ink_eraser_24px),
                    contentDescription = stringResource(R.string.eraser)
                )
            }
        }
    }
}

@Composable
fun ToolboxHistoryControls(
    canUndo: Boolean,
    canRedo: Boolean,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onClear: () -> Unit,
    drawingCanvasViewModel: DrawingCanvasViewModel,
    isVertical: Boolean,
    modifier: Modifier = Modifier
) {
    if (isVertical) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = onUndo, enabled = canUndo) {
                Icon(
                    painter = painterResource(R.drawable.undo_24px),
                    contentDescription = stringResource(R.string.undo)
                )
            }
            IconButton(onClick = onRedo, enabled = canRedo) {
                Icon(
                    painter = painterResource(R.drawable.redo_24px),
                    contentDescription = stringResource(R.string.redo)
                )
            }
            IconButton(onClick = onClear) {
                Icon(
                    painter = painterResource(R.drawable.delete_24px), // or clear icon resource
                    contentDescription = "Clear Canvas"
                )
            }
        }
    } else {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = onUndo, enabled = canUndo) {
                Icon(
                    painter = painterResource(R.drawable.undo_24px),
                    contentDescription = stringResource(R.string.undo)
                )
            }
            IconButton(onClick = onRedo, enabled = canRedo) {
                Icon(
                    painter = painterResource(R.drawable.redo_24px),
                    contentDescription = stringResource(R.string.redo)
                )
            }
            IconButton(onClick = onClear) {
                Icon(
                    painter = painterResource(R.drawable.delete_24px),
                    contentDescription = "Clear Canvas"
                )
            }
        }
    }
}

@Composable
fun ToolboxNoteActions(
    imagePickerLauncher: ActivityResultLauncher<PickVisualMediaRequest>,
    onExit: () -> Unit,
    onEditActiveBrush: () -> Unit,
    drawingCanvasViewModel: DrawingCanvasViewModel,
    isVertical: Boolean,
    modifier: Modifier = Modifier
) {
    if (isVertical) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = onEditActiveBrush) {
                Icon(
                    painter = painterResource(R.drawable.tune_24px), // or edit brush icon
                    contentDescription = "Edit Active Brush"
                )
            }
            IconButton(onClick = onExit) {
                Icon(
                    painter = painterResource(R.drawable.close_24px),
                    contentDescription = null
                )
            }
        }
    } else {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = onEditActiveBrush) {
                Icon(
                    painter = painterResource(R.drawable.tune_24px),
                    contentDescription = "Edit Active Brush"
                )
            }
            IconButton(onClick = onExit) {
                Icon(
                    painter = painterResource(R.drawable.close_24px),
                    contentDescription = null
                )
            }
        }
    }
}