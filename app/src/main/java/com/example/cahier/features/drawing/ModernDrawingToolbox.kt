package com.example.cahier.features.drawing

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
fun ModernDrawingToolbox(
    drawingCanvasViewModel: DrawingCanvasViewModel,
    imagePickerLauncher: ActivityResultLauncher<PickVisualMediaRequest>,
    canUndo: Boolean,
    canRedo: Boolean,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onExit: () -> Unit,
    onEditActiveBrush: () -> Unit,
    onColorPickerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val isEraserMode by drawingCanvasViewModel.isEraserMode.collectAsStateWithLifecycle()
    val customBrushes by drawingCanvasViewModel.customBrushes.collectAsStateWithLifecycle()

    var brushesMenuExpanded by rememberSaveable { mutableStateOf(false) }
    var sizeMenuExpanded by rememberSaveable { mutableStateOf(false) }

    // Floating Capsule Surface
    Surface(
        modifier = modifier.padding(16.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 6.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // --- BRUSH SELECTOR ---
            Box {
                val brushContainerColor by animateColorAsState(
                    if (!isEraserMode) MaterialTheme.colorScheme.primaryContainer
                    else Color.Transparent,
                    label = "brushColor"
                )
                val brushIconColor by animateColorAsState(
                    if (!isEraserMode) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    label = "brushIconColor"
                )

                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        drawingCanvasViewModel.setEraserMode(false)
                        brushesMenuExpanded = true
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(brushContainerColor)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.brush_24px),
                        contentDescription = stringResource(R.string.brush),
                        tint = brushIconColor
                    )
                }

                BrushesDropdownMenu(
                    expanded = brushesMenuExpanded,
                    onDismissRequest = { brushesMenuExpanded = false },
                    onBrushChange = { selectedCustomBrush ->
                        // Pass the BrushFamily property from CustomBrush
                        drawingCanvasViewModel.changeBrush(selectedCustomBrush.brushFamily)
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
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.palette_24px),
                    contentDescription = stringResource(R.string.color),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // --- BRUSH SIZE ---
            Box {
                IconButton(
                    onClick = { sizeMenuExpanded = true },
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.line_weight_24px),
                        contentDescription = stringResource(R.string.brush_size),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
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

            // --- ERASER TOGGLE ---
            val eraserContainerColor by animateColorAsState(
                if (isEraserMode) MaterialTheme.colorScheme.primaryContainer
                else Color.Transparent,
                label = "eraserColor"
            )
            val eraserIconColor by animateColorAsState(
                if (isEraserMode) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurfaceVariant,
                label = "eraserIconColor"
            )

            IconButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    drawingCanvasViewModel.setEraserMode(!isEraserMode)
                },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(eraserContainerColor)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ink_eraser_24px),
                    contentDescription = stringResource(R.string.eraser),
                    tint = eraserIconColor
                )
            }

            VerticalDivider(
                modifier = Modifier
                    .height(24.dp)
                    .padding(horizontal = 4.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // --- UNDO / REDO ---
            IconButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onUndo()
                },
                enabled = canUndo,
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.undo_24px),
                    contentDescription = stringResource(R.string.undo)
                )
            }

            IconButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onRedo()
                },
                enabled = canRedo,
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.redo_24px),
                    contentDescription = stringResource(R.string.redo)
                )
            }
        }
    }
}