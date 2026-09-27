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

package com.example.cahier.features.text

import android.content.ClipData
import android.content.ClipDescription
import android.net.Uri
import android.view.DragAndDropPermissions
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.cahier.R
import com.example.cahier.core.data.Note
import com.example.cahier.core.ui.CahierUiState
import com.example.cahier.core.ui.FocusedFieldEnum
import com.example.cahier.core.ui.theme.CahierAppTheme
import com.example.cahier.core.utils.createDropTarget
import com.example.cahier.features.home.NotePreviewParameterProvider
import com.example.cahier.features.text.viewmodel.CanvasScreenViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun TextNoteCanvasScreen(
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
    canvasScreenViewModel: CanvasScreenViewModel = hiltViewModel()
) {
    val uiState by canvasScreenViewModel.uiState.collectAsStateWithLifecycle()

    var titleState by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(uiState.note.title))
    }

    var bodyState by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(uiState.note.text ?: ""))
    }

    LaunchedEffect(uiState.note.id) {
        if (titleState.text != uiState.note.title) {
            titleState = TextFieldValue(uiState.note.title)
        }
        if (bodyState.text != (uiState.note.text ?: "")) {
            bodyState = TextFieldValue(uiState.note.text ?: "")
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            canvasScreenViewModel.handlePickedImageUri(it)
        }
    }

    NoteCanvasContent(
        uiState = uiState,
        titleState = titleState,
        onTitleChange = {
            titleState = it
            canvasScreenViewModel.updateNoteTitle(it.text)
        },
        bodyState = bodyState,
        onBodyChange = {
            bodyState = it
            canvasScreenViewModel.updateNoteText(it.text)
        },
        onExit = onExit,
        imagePickerLauncher = imagePickerLauncher,
        onToggleFavorite = { canvasScreenViewModel.toggleFavorite() },
        onDroppedUri = { uri, permissions ->
            canvasScreenViewModel.handleDroppedUri(
                uri,
                permissions
            )
        },
        onCreateShareableUri = { uriString ->
            canvasScreenViewModel.createShareableUri(uriString)
        },
        modifier = modifier,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteCanvasContent(
    uiState: CahierUiState,
    titleState: TextFieldValue,
    onTitleChange: (TextFieldValue) -> Unit,
    bodyState: TextFieldValue,
    onBodyChange: (TextFieldValue) -> Unit,
    onExit: () -> Unit,
    imagePickerLauncher: ActivityResultLauncher<PickVisualMediaRequest>,
    onToggleFavorite: () -> Unit,
    onDroppedUri: (Uri, DragAndDropPermissions?) -> Unit,
    onCreateShareableUri: suspend (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var focusedFieldEnum by rememberSaveable { mutableStateOf(FocusedFieldEnum.None) }
    var showAllMediaScreen by rememberSaveable { mutableStateOf(false) }

    val titleFocusRequester = remember { FocusRequester() }
    val bodyFocusRequester = remember { FocusRequester() }
    val activity = LocalActivity.current as? ComponentActivity

    LaunchedEffect(focusedFieldEnum) {
        when (focusedFieldEnum) {
            FocusedFieldEnum.Title -> titleFocusRequester.requestFocus()
            FocusedFieldEnum.Body -> {
                if (uiState.note.text != null) {
                    bodyFocusRequester.requestFocus()
                } else {
                    focusedFieldEnum = FocusedFieldEnum.None
                }
            }
            FocusedFieldEnum.None -> {
                /* Do nothing. */
            }
        }
    }

    val dropTarget: DragAndDropTarget = remember {
        createDropTarget(activity, onDroppedUri)
    }

    if (showAllMediaScreen) {
        BackHandler { showAllMediaScreen = false }

        JournalMediaGridScreen(
            imageUris = uiState.note.imageUriList ?: emptyList(),
            onBackClick = { showAllMediaScreen = false },
            modifier = modifier
        )
    } else {
        Surface(
            modifier = modifier
                .fillMaxSize()
                .dragAndDropTarget(
                    shouldStartDragAndDrop = { event ->
                        event.mimeTypes().any { it.startsWith("image/") }
                    },
                    target = dropTarget
                ),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .imePadding()
            ) {
                NoteCanvasTopBar(
                    imagePickerLauncher = imagePickerLauncher,
                    isFavorite = uiState.note.isFavorite,
                    onToggleFavorite = onToggleFavorite,
                    onExit = onExit
                )

                NoteCanvasBody(
                    note = uiState.note,
                    titleState = titleState,
                    onTitleChange = onTitleChange,
                    titleFocusRequester = titleFocusRequester,
                    onTitleFocusChanged = { if (it.isFocused) focusedFieldEnum = FocusedFieldEnum.Title },
                    bodyState = bodyState,
                    onBodyChange = onBodyChange,
                    bodyFocusRequester = bodyFocusRequester,
                    onBodyFocusChanged = { if (it.isFocused) focusedFieldEnum = FocusedFieldEnum.Body },
                    onShowAllMediaClick = { showAllMediaScreen = true }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoteCanvasTopBar(
    imagePickerLauncher: ActivityResultLauncher<PickVisualMediaRequest>,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    var optionsMenuExpanded by rememberSaveable { mutableStateOf(false) }

    TopAppBar(
        modifier = modifier,
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
            IconButton(
                onClick = { /* Formatting options */ },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.format_list_bulleted_24px),
                    contentDescription = null
                )
            }

            IconButton(
                onClick = {
                    imagePickerLauncher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.add_24px),
                    contentDescription = stringResource(R.string.add_image)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onExit,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Save", style = MaterialTheme.typography.labelLarge)
            }

            Box {
                IconButton(onClick = { optionsMenuExpanded = true }) {
                    Icon(
                        painter = painterResource(R.drawable.more_vert_24px),
                        contentDescription = stringResource(R.string.more_options)
                    )
                }
                NoteCanvasDropdownMenu(
                    expanded = optionsMenuExpanded,
                    onDismissRequest = { optionsMenuExpanded = false },
                    onUploadImage = {
                        imagePickerLauncher.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    },
                    isFavorite = isFavorite,
                    onToggleFavorite = onToggleFavorite,
                    onExit = onExit
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@Composable
private fun NoteCanvasDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onUploadImage: () -> Unit,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        DropdownMenuItem(
            text = { Text(stringResource(R.string.upload_image)) },
            onClick = {
                onDismissRequest()
                onUploadImage()
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.image_24px),
                    contentDescription = stringResource(R.string.add_image)
                )
            }
        )

        DropdownMenuItem(
            text = {
                Text(
                    if (isFavorite) stringResource(R.string.unfavorite)
                    else stringResource(R.string.favorite)
                )
            },
            onClick = {
                onDismissRequest()
                onToggleFavorite()
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(
                        if (isFavorite) R.drawable.favorite_24px_filled
                        else R.drawable.favorite_24px
                    ),
                    contentDescription = if (isFavorite)
                        stringResource(R.string.unfavorite) else stringResource(
                        R.string.favorite
                    ),
                    tint = if (isFavorite)
                        MaterialTheme.colorScheme.primary else LocalContentColor.current
                )
            }
        )

        DropdownMenuItem(
            text = { Text(stringResource(R.string.exit)) },
            onClick = {
                onDismissRequest()
                onExit()
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.exit_to_app_24px),
                    contentDescription = null
                )
            }
        )
    }
}

@Composable
private fun NoteCanvasBody(
    note: Note,
    titleState: TextFieldValue,
    onTitleChange: (TextFieldValue) -> Unit,
    titleFocusRequester: FocusRequester,
    onTitleFocusChanged: (FocusState) -> Unit,
    bodyState: TextFieldValue,
    onBodyChange: (TextFieldValue) -> Unit,
    bodyFocusRequester: FocusRequester,
    onBodyFocusChanged: (FocusState) -> Unit,
    onShowAllMediaClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        // Google Journal Media Carousel Header
        item {
            val imageList = note.imageUriList ?: emptyList()
            if (imageList.isNotEmpty()) {
                TextNoteMediaHeader(
                    imageUris = imageList,
                    onShowAllClick = onShowAllMediaClick
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        // Title Field ("Add title")
        item {
            BasicTextField(
                value = titleState,
                onValueChange = onTitleChange,
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
                    .padding(horizontal = 20.dp, vertical = 4.dp)
                    .focusRequester(titleFocusRequester)
                    .onFocusChanged(onTitleFocusChanged)
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Formatted Date Bar
        item {
            val formattedDate = remember {
                SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault()).format(Date())
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(vertical = 4.dp)
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

        // Note Content Editor Body
        item {
            note.text?.let {
                BasicTextField(
                    value = bodyState,
                    onValueChange = onBodyChange,
                    textStyle = TextStyle(
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        lineHeight = 26.sp
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    keyboardOptions = KeyboardOptions(
                        autoCorrectEnabled = true,
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                    decorationBox = { innerTextField ->
                        if (bodyState.text.isEmpty()) {
                            Text(
                                text = stringResource(R.string.note),
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                        innerTextField()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp)
                        .focusRequester(bodyFocusRequester)
                        .onFocusChanged(onBodyFocusChanged)
                )
            }
        }
    }
}

@Composable
private fun TextNoteMediaHeader(
    imageUris: List<String>,
    onShowAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (imageUris.isEmpty()) return

    val pages = remember(imageUris) { imageUris.chunked(3) }
    val pagerState = rememberPagerState(pageCount = { pages.size })

    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            pageSpacing = 12.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) { pageIndex ->
            val pageImages = pages[pageIndex]

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(24.dp)),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when (pageImages.size) {
                    1 -> {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            AsyncImage(
                                model = pageImages[0],
                                contentDescription = stringResource(R.string.uploaded_image),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                    2 -> {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        ) {
                            AsyncImage(
                                model = pageImages[0],
                                contentDescription = stringResource(R.string.uploaded_image),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        ) {
                            AsyncImage(
                                model = pageImages[1],
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                    else -> {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            modifier = Modifier
                                .weight(1.8f)
                                .fillMaxHeight()
                        ) {
                            AsyncImage(
                                model = pageImages[0],
                                contentDescription = stringResource(R.string.uploaded_image),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) {
                                AsyncImage(
                                    model = pageImages[1],
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) {
                                AsyncImage(
                                    model = pageImages[2],
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 6.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = "Show all",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.clickable { onShowAllClick() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun JournalMediaGridScreen(
    imageUris: List<String>,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateText = remember {
        SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault()).format(Date())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Your journal media",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(imageUris) { uri ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    AsyncImage(
                        model = uri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(20.dp))
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Photos",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = dateText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NoteImage(
    imageUriString: String,
    onCreateShareableUri: suspend (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var clipData by remember { mutableStateOf<ClipData?>(null) }

    LaunchedEffect(imageUriString) {
        val shareableUri = onCreateShareableUri(imageUriString)
        shareableUri.let {
            clipData =
                ClipData(
                    ClipDescription("Image", arrayOf("image/*")),
                    ClipData.Item(shareableUri.toString()),
                )
        }
    }

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .dragAndDropSource { _ ->
                    clipData?.let {
                        DragAndDropTransferData(
                            clipData = it,
                            flags = View.DRAG_FLAG_GLOBAL or View.DRAG_FLAG_GLOBAL_URI_READ,
                        )
                    }
                },
    ) {
        AsyncImage(
            model = imageUriString,
            contentDescription = stringResource(R.string.uploaded_image),
            contentScale = ContentScale.FillWidth,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NoteCanvasScreenPreview(
    @PreviewParameter(NotePreviewParameterProvider::class) note: Note
) {
    CahierAppTheme {
        NoteCanvasContent(
            uiState = CahierUiState(note = note),
            titleState = TextFieldValue(note.title),
            onTitleChange = {},
            bodyState = TextFieldValue(note.text ?: ""),
            onBodyChange = {},
            onExit = {},
            imagePickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickVisualMedia(),
                onResult = {}
            ),
            onToggleFavorite = {},
            onDroppedUri = { _, _ -> },
            onCreateShareableUri = { _ -> }
        )
    }
}