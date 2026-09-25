package com.example.cahier.features.drawing

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

import android.content.res.Configuration
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cahier.R
import com.example.cahier.features.drawing.viewmodel.DrawingCanvasViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawingNoteCanvasScreen(
    onExit: () -> Unit,
    imagePickerLauncher: ActivityResultLauncher<PickVisualMediaRequest>,
    onEditActiveBrush: () -> Unit,
    onColorPickerClick: () -> Unit,
    modifier: Modifier = Modifier,
    drawingCanvasViewModel: DrawingCanvasViewModel = hiltViewModel(),
    drawingCanvasContent: @Composable () -> Unit = {}
) {
    val uiState by drawingCanvasViewModel.uiState.collectAsStateWithLifecycle()
    val canUndo by drawingCanvasViewModel.canUndo.collectAsStateWithLifecycle()
    val canRedo by drawingCanvasViewModel.canRedo.collectAsStateWithLifecycle()

    var titleState by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(uiState.note.title))
    }

    LaunchedEffect(uiState.note.id) {
        if (titleState.text != uiState.note.title) {
            titleState = TextFieldValue(uiState.note.title)
        }
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val formattedDate = remember {
        SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault()).format(Date())
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header TopAppBar matching Text Note Screen layout
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(
                        onClick = onExit,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.close_24px),
                            contentDescription = stringResource(R.string.exit)
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = onExit,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Save", style = MaterialTheme.typography.labelLarge)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )

            // Header Section: Title Input and Date Indicator
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                BasicTextField(
                    value = titleState,
                    onValueChange = {
                        titleState = it
                        drawingCanvasViewModel.updateNoteTitle(it.text)
                    },
                    textStyle = TextStyle(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    keyboardOptions = KeyboardOptions(
                        autoCorrectEnabled = true,
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                    decorationBox = { innerTextField ->
                        if (titleState.text.isEmpty()) {
                            Text(
                                text = stringResource(R.string.title),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                        innerTextField()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.calendar_month_24px),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Body Area: Adapts layout for vertical vs landscape orientations
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                if (isLandscape) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 16.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize(),
                            shape = RoundedCornerShape(28.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerLowest,
                            tonalElevation = 1.dp
                        ) {
                            drawingCanvasContent()
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        DrawingToolbox(
                            drawingCanvasViewModel = drawingCanvasViewModel,
                            imagePickerLauncher = imagePickerLauncher,
                            canUndo = canUndo,
                            canRedo = canRedo,
                            onUndo = { drawingCanvasViewModel.undo() },
                            onRedo = { drawingCanvasViewModel.redo() },
                            onExit = onExit,
                            onEditActiveBrush = onEditActiveBrush,
                            isVertical = true,
                            onColorPickerClick = onColorPickerClick,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 16.dp)
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            shape = RoundedCornerShape(28.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerLowest,
                            tonalElevation = 1.dp
                        ) {
                            drawingCanvasContent()
                        }

                        // Floating toolbox centered over the canvas bottom
                        DrawingToolbox(
                            drawingCanvasViewModel = drawingCanvasViewModel,
                            imagePickerLauncher = imagePickerLauncher,
                            canUndo = canUndo,
                            canRedo = canRedo,
                            onUndo = { drawingCanvasViewModel.undo() },
                            onRedo = { drawingCanvasViewModel.redo() },
                            onExit = onExit,
                            onEditActiveBrush = onEditActiveBrush,
                            isVertical = false,
                            onColorPickerClick = onColorPickerClick,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 8.dp)
                        )
                    }
                }
            }
        }
    }
}