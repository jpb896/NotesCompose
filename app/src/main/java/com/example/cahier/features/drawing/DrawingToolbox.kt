package com.example.cahier.features.drawing

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cahier.R
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
    val haptic = LocalHapticFeedback.current
    val isEraserMode by drawingCanvasViewModel.isEraserMode.collectAsStateWithLifecycle()
    val customBrushes by drawingCanvasViewModel.customBrushes.collectAsStateWithLifecycle()

    var brushesMenuExpanded by rememberSaveable { mutableStateOf(false) }
    var sizeMenuExpanded by rememberSaveable { mutableStateOf(false) }

    // Surface pill capsule
    Surface(
        modifier = modifier.wrapContentSize(),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 6.dp,
        shadowElevation = 8.dp
    ) {
        val controls = @Composable {
            // --- BRUSH SELECTOR ---
            Box {
                val brushContainerColor by animateColorAsState(
                    if (!isEraserMode) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                    label = "brushColor"
                )
                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        drawingCanvasViewModel.setEraserMode(false)
                        brushesMenuExpanded = true
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(brushContainerColor)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.brush_24px),
                        contentDescription = stringResource(R.string.brush)
                    )
                }

                BrushesDropdownMenu(
                    expanded = brushesMenuExpanded,
                    onDismissRequest = { brushesMenuExpanded = false },
                    onBrushSelected = { selectedFamily ->
                        drawingCanvasViewModel.changeBrush(selectedFamily)
                        brushesMenuExpanded = false
                    },
                    customBrushes = customBrushes
                )
            }

            // --- COLOR PICKER ---
            IconButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    drawingCanvasViewModel.setEraserMode(false)
                    onColorPickerClick()
                },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.palette_24px),
                    contentDescription = stringResource(R.string.color)
                )
            }

            // --- BRUSH SIZE ---
            Box {
                IconButton(
                    onClick = { sizeMenuExpanded = true },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.line_weight_24px),
                        contentDescription = stringResource(R.string.brush_size)
                    )
                }
                SizeDropdownMenu(
                    expanded = sizeMenuExpanded,
                    onDismissRequest = { sizeMenuExpanded = false },
                    onSizeChange = { newSize ->
                        drawingCanvasViewModel.changeBrushSize(newSize)
                        sizeMenuExpanded = false
                    }
                )
            }

            // --- ERASER ---
            val eraserContainerColor by animateColorAsState(
                if (isEraserMode) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                label = "eraserColor"
            )
            IconButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    drawingCanvasViewModel.setEraserMode(!isEraserMode)
                },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(eraserContainerColor)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ink_eraser_24px),
                    contentDescription = stringResource(R.string.eraser)
                )
            }

            // --- DIVIDER WITH ABSOLUTE CONSTRAINTS ---
            if (isVertical) {
                HorizontalDivider(
                    modifier = Modifier
                        .width(20.dp)
                        .padding(vertical = 2.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            } else {
                VerticalDivider(
                    modifier = Modifier
                        .height(20.dp)
                        .padding(horizontal = 2.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }

            // --- UNDO / REDO ---
            IconButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onUndo()
                },
                enabled = canUndo,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(painterResource(R.drawable.undo_24px), contentDescription = stringResource(R.string.undo))
            }

            IconButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onRedo()
                },
                enabled = canRedo,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(painterResource(R.drawable.redo_24px), contentDescription = stringResource(R.string.redo))
            }

            // --- CLEAR ALL ---
            IconButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    drawingCanvasViewModel.clearScreen()
                },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.delete_24px),
                    contentDescription = "Clear All",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }

        // Orientation layout
        if (isVertical) {
            Column(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                controls()
            }
        } else {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                controls()
            }
        }
    }
}