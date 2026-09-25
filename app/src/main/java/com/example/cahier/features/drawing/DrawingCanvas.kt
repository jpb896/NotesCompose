/*
 * Copyright 2025 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.cahier.features.drawing

import android.content.ClipData
import android.content.ClipDescription
import android.net.Uri
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.ink.rendering.android.canvas.CanvasStrokeRenderer
import androidx.ink.strokes.Stroke
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cahier.R
import com.example.cahier.core.ui.ColorPickerDialog
import com.example.cahier.core.ui.ConfirmationDialog
import com.example.cahier.core.ui.DrawingSurface
import com.example.cahier.core.ui.LocalTextureStore
import com.example.cahier.core.ui.theme.CahierAppTheme
import com.example.cahier.core.utils.createDropTarget
import com.example.cahier.features.drawing.viewmodel.DrawingCanvasViewModel

@Composable
fun DrawingCanvas(
    navigateUp: () -> Unit,
    navigateToBrushGraph: () -> Unit,
    modifier: Modifier = Modifier,
    drawingCanvasViewModel: DrawingCanvasViewModel = hiltViewModel(),
) {
    DrawingNoteCanvasScreen(
        onExit = navigateUp,
        imagePickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ) {},
        onEditActiveBrush = navigateToBrushGraph,
        onColorPickerClick = { },
        modifier = modifier,
        drawingCanvasViewModel = drawingCanvasViewModel,
        drawingCanvasContent = {
            DrawingCanvasContent(
                drawingCanvasViewModel = drawingCanvasViewModel,
                onNavigateUp = navigateUp,
                navigateToBrushGraph = navigateToBrushGraph
            )
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DrawingCanvasContent(
    drawingCanvasViewModel: DrawingCanvasViewModel,
    onNavigateUp: () -> Unit,
    navigateToBrushGraph: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by drawingCanvasViewModel.uiState.collectAsStateWithLifecycle()
    var showConfirmationDialog by rememberSaveable { mutableStateOf(false) }
    var pendingImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            if (uiState.note.imageUriList?.isNotEmpty() == true) {
                pendingImageUri = it
                showConfirmationDialog = true
            } else {
                drawingCanvasViewModel.processAndAddImageFromPicker(it)
            }
        }
    }

    if (showConfirmationDialog) {
        ConfirmationDialog(
            onConfirm = {
                drawingCanvasViewModel.replaceImage(pendingImageUri)
                showConfirmationDialog = false
                pendingImageUri = null
            },
            onDismiss = {
                showConfirmationDialog = false
                pendingImageUri = null
            },
            title = stringResource(R.string.replace_image_title),
            text = stringResource(R.string.replace_image_text)
        )
    }

    var showColorPicker by rememberSaveable { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        DrawingSurfaceWithTarget(
            drawingCanvasViewModel = drawingCanvasViewModel,
            modifier = Modifier.fillMaxSize()
        )

        ColorPickerDialog(
            showDialog = showColorPicker,
            onDismissRequest = { showColorPicker = false },
            onColorSelected = { color ->
                drawingCanvasViewModel.changeBrushColor(color)
                showColorPicker = false
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DrawingSurfaceWithTarget(
    drawingCanvasViewModel: DrawingCanvasViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by drawingCanvasViewModel.uiState.collectAsStateWithLifecycle()
    val exportedUri by drawingCanvasViewModel.exportedImageUri.collectAsStateWithLifecycle()
    val currentBrush by drawingCanvasViewModel.currentBrush.collectAsStateWithLifecycle()
    val isEraserMode by drawingCanvasViewModel.isEraserMode.collectAsStateWithLifecycle()
    val strokes = remember { mutableStateListOf<Stroke>() }
    val textureStore = LocalTextureStore.current
    val cacheGen by textureStore.generation.collectAsState()
    val canvasStrokeRenderer = remember(cacheGen) {
        CanvasStrokeRenderer.create(textureStore = textureStore)
    }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    val view = LocalView.current
    val activity = LocalActivity.current as ComponentActivity

    val dropTarget = remember {
        createDropTarget(activity) { uri, permissions ->
            drawingCanvasViewModel.handleDroppedUri(uri, permissions)
        }
    }

    LaunchedEffect(uiState.strokes) {
        if (strokes != uiState.strokes) {
            strokes.clear()
            strokes.addAll(uiState.strokes)
        }
    }

    LaunchedEffect(
        uiState.strokes,
        uiState.note.imageUriList,
        canvasSize
    ) {
        if (canvasSize != IntSize.Zero) {
            drawingCanvasViewModel.createExportedBitmap(
                canvasSize.width,
                canvasSize.height
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { canvasSize = it }
            .dragAndDropTarget(
                shouldStartDragAndDrop = { event ->
                    event.mimeTypes().any { it.startsWith("image/") }
                },
                target = dropTarget
            )
    ) {
        DrawingSurface(
            strokes = strokes,
            canvasStrokeRenderer = canvasStrokeRenderer,
            onStrokesFinished = { newStrokes ->
                strokes.addAll(newStrokes)
                drawingCanvasViewModel.onStrokesFinished(newStrokes)
            },
            onErase = drawingCanvasViewModel::erase,
            onEraseStart = drawingCanvasViewModel::startErase,
            onEraseEnd = drawingCanvasViewModel::endErase,
            onStartDrag = {
                exportedUri?.let { uri ->
                    val clipData = ClipData(
                        ClipDescription(
                            "Image",
                            arrayOf("image/png")
                        ),
                        ClipData.Item(uri)
                    )
                    val dragShadowBuilder = View.DragShadowBuilder(view)
                    view.startDragAndDrop(
                        clipData,
                        dragShadowBuilder,
                        null,
                        View.DRAG_FLAG_GLOBAL or View.DRAG_FLAG_GLOBAL_URI_READ
                    )
                }
            },
            currentBrush = currentBrush,
            onGetNextBrush = drawingCanvasViewModel::getCurrentBrush,
            isEraserMode = isEraserMode,
            backgroundImageUri = uiState.note.imageUriList?.firstOrNull(),
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DrawingCanvasPreview() {
    CahierAppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text("Drawing Surface Preview")
            }
        }
    }
}