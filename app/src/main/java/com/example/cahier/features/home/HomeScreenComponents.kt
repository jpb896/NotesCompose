/*
 * Copyright 2025 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.cahier.features.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.cahier.R
import com.example.cahier.core.data.Note
import com.example.cahier.core.data.NoteType
import com.example.cahier.features.drawing.DrawingDetailThumbnail
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun JournalHomeScreen(
    recentNote: Note?,
    otherNotes: List<Note>,
    bookmarks: List<Note>,
    onNoteClick: (Note) -> Unit,
    onBookmarkToggle: (Note) -> Unit,
    onDeleteNote: (Note) -> Unit,
    onNewTextNoteClick: () -> Unit,
    onNewDrawingNoteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFabExpanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AnimatedVisibility(
                    visible = isFabExpanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Drawing Note Action
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Drawing Note",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            SmallFloatingActionButton(
                                onClick = {
                                    isFabExpanded = false
                                    onNewDrawingNoteClick()
                                },
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                shape = CircleShape
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_drawing_mode),
                                    contentDescription = "New Drawing Note",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Text Note Action
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Text Note",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            SmallFloatingActionButton(
                                onClick = {
                                    isFabExpanded = false
                                    onNewTextNoteClick()
                                },
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                shape = CircleShape
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.edit_24px),
                                    contentDescription = "New Text Note",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // Primary FAB toggle button
                FloatingActionButton(
                    onClick = { isFabExpanded = !isFabExpanded },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = RoundedCornerShape(16.dp),
                    elevation = FloatingActionButtonDefaults.elevation(6.dp)
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (isFabExpanded) R.drawable.close_24px else R.drawable.edit_24px
                        ),
                        contentDescription = "Add Journal Entry"
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Recent Entry Section
            recentNote?.let { note ->
                Text(
                    text = "Recent",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Use key so that when recentNote changes or gets recreated, its dismiss state resets cleanly
                key(note.id) {
                    HeroJournalCard(
                        note = note,
                        onClick = { onNoteClick(note) },
                        onBookmarkToggle = { onBookmarkToggle(note) },
                        onDelete = { onDeleteNote(note) }
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Bookmarks Section
            if (bookmarks.isNotEmpty()) {
                Text(
                    text = "Bookmarks",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                bookmarks.forEach { note ->
                    key(note.id) {
                        CompactJournalRow(
                            note = note,
                            onClick = { onNoteClick(note) },
                            onBookmarkToggle = { onBookmarkToggle(note) },
                            onDelete = { onDeleteNote(note) }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            if (otherNotes.isNotEmpty()) {
                Text(
                    text = "All notes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                otherNotes.forEach { note ->
                    key(note.id) {
                        CompactJournalRow(
                            note = note,
                            onClick = { onNoteClick(note) },
                            onBookmarkToggle = { onBookmarkToggle(note) },
                            onDelete = { onDeleteNote(note) }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

/**
 * Large featured card utilizing swipe-to-dismiss deletion.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeroJournalCard(
    note: Note,
    onClick: () -> Unit,
    onBookmarkToggle: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart || dismissValue == SwipeToDismissBoxValue.StartToEnd) {
                onDelete()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val color = MaterialTheme.colorScheme.errorContainer
            val contentColor = MaterialTheme.colorScheme.onErrorContainer
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(28.dp))
                    .background(color)
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.close_24px),
                    contentDescription = "Delete note",
                    tint = contentColor
                )
            }
        },
        modifier = modifier.fillMaxWidth()
    ) {
        val monthFormat = remember { SimpleDateFormat("MMM", Locale.getDefault()) }
        val dayFormat = remember { SimpleDateFormat("d", Locale.getDefault()) }

        val noteDate = remember(note.dateCreated) { Date(note.dateCreated) }
        val monthText = monthFormat.format(noteDate)
        val dayText = dayFormat.format(noteDate)
        val images = note.imageUriList ?: emptyList()

        Card(
            onClick = onClick,
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Date Header + Title Row
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Text(
                            text = monthText,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = dayText,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Entry Title
                    Text(
                        text = note.title.ifBlank { "Untitled Entry" },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    // Bookmark Toggle Button
                    IconButton(
                        onClick = onBookmarkToggle,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (note.isFavorite) R.drawable.bookmark_24px_filled else R.drawable.bookmark_24px
                            ),
                            contentDescription = if (note.isFavorite) "Remove bookmark" else "Bookmark note",
                            tint = if (note.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Dynamic Media Grid
                if (note.type == NoteType.Drawing || images.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                    ) {
                        if (note.type == NoteType.Drawing) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                DrawingDetailThumbnail(
                                    strokesData = note.strokesData,
                                    modifier = Modifier.fillMaxSize(),
                                    onClick = {}
                                )
                            }
                        } else {
                            when (images.size) {
                                1 -> {
                                    Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        AsyncImage(
                                            model = images.first(),
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }
                                2 -> {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(20.dp),
                                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxHeight()
                                        ) {
                                            AsyncImage(
                                                model = images[0],
                                                contentDescription = null,
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
                                                model = images[1],
                                                contentDescription = null,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        }
                                    }
                                }
                                else -> {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(20.dp),
                                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                            modifier = Modifier
                                                .weight(1.8f)
                                                .fillMaxHeight()
                                        ) {
                                            AsyncImage(
                                                model = images[0],
                                                contentDescription = null,
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
                                                    model = images[1],
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
                                                    model = images[2],
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
                    }
                }

                // Embedded "Reflection" Prompt Container (Only for Text Notes)
                if (note.type != NoteType.Drawing && !note.text.isNullOrBlank())  {
                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.auto_awesome_24px),
                                    contentDescription = "Reflection",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Reflection",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = note.text
                                        ?: "What a fantastic day! Keep capturing these moments.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Icon(
                                painter = painterResource(id = R.drawable.expand_more_24px),
                                contentDescription = "Expand",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Compact horizontal item utilizing swipe-to-dismiss deletion.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompactJournalRow(
    note: Note,
    onClick: () -> Unit,
    onBookmarkToggle: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart || dismissValue == SwipeToDismissBoxValue.StartToEnd) {
                onDelete()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val color = MaterialTheme.colorScheme.errorContainer
            val contentColor = MaterialTheme.colorScheme.onErrorContainer
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.close_24px),
                    contentDescription = "Delete note",
                    tint = contentColor
                )
            }
        },
        modifier = modifier.fillMaxWidth()
    ) {
        val formattedDate = remember(note.dateCreated) {
            val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            formatter.format(Date(note.dateCreated))
        }

        val firstImageUri = remember(note.imageUriList) { note.imageUriList?.firstOrNull() }

        Card(
            onClick = onClick,
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Thumbnail Box
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    modifier = Modifier.size(56.dp)
                ) {
                    when {
                        note.type == NoteType.Drawing -> {
                            DrawingDetailThumbnail(
                                strokesData = note.strokesData,
                                modifier = Modifier.fillMaxSize(),
                                onClick = {}
                            )
                        }
                        !firstImageUri.isNullOrEmpty() -> {
                            AsyncImage(
                                model = firstImageUri,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        else -> {
                            Icon(
                                painter = painterResource(id = R.drawable.text_fields_24px),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Details
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = note.title.ifBlank { "Add title" },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (note.type == NoteType.Drawing) {
                        Text(
                            text = "Drawing",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        note.text?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Bookmark Icon Toggle
                IconButton(onClick = onBookmarkToggle) {
                    Icon(
                        painter = painterResource(
                            id = if (note.isFavorite) R.drawable.bookmark_24px_filled else R.drawable.bookmark_24px
                        ),
                        contentDescription = if (note.isFavorite) "Remove bookmark" else "Bookmark note",
                        tint = if (note.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}