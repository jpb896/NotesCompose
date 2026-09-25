package com.example.cahier.features.drawing

import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.ink.brush.BrushFamily
import androidx.ink.brush.StockBrushes
import com.example.cahier.R
import com.example.cahier.core.data.CustomBrush

@Composable
fun BrushesDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onBrushSelected: (BrushFamily) -> Unit,
    customBrushes: List<CustomBrush>,
    modifier: Modifier = Modifier
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        // --- Core Brushes ---
        DropdownMenuItem(
            text = { Text("Pen") },
            leadingIcon = { Icon(painterResource(R.drawable.brush_24px), null, Modifier.size(20.dp)) },
            onClick = { onBrushSelected(StockBrushes.marker()); onDismissRequest() }
        )
        DropdownMenuItem(
            text = { Text("Marker") },
            leadingIcon = { Icon(painterResource(R.drawable.brush_24px), null, Modifier.size(20.dp)) },
            onClick = { onBrushSelected(StockBrushes.marker()); onDismissRequest() }
        )
        DropdownMenuItem(
            text = { Text("Highlighter") },
            leadingIcon = { Icon(painterResource(R.drawable.brush_24px), null, Modifier.size(20.dp)) },
            onClick = { onBrushSelected(StockBrushes.highlighter()); onDismissRequest() }
        )
        DropdownMenuItem(
            text = { Text("Dashed Line") },
            leadingIcon = { Icon(painterResource(R.drawable.brush_24px), null, Modifier.size(20.dp)) },
            onClick = { onBrushSelected(StockBrushes.marker()); onDismissRequest() }
        )

        HorizontalDivider()

        // --- Emoji Highlighter Sub-Options ---
        val emojiSubOptions = listOf("Star ⭐", "Heart ❤️", "Sparkle ✨", "Fire 🔥")
        emojiSubOptions.forEach { label ->
            DropdownMenuItem(
                text = { Text(label) },
                leadingIcon = { Icon(painterResource(R.drawable.brush_24px), null, Modifier.size(20.dp)) },
                onClick = {
                    onBrushSelected(StockBrushes.highlighter())
                    onDismissRequest()
                }
            )
        }

        // --- Custom Brushes ---
        if (customBrushes.isNotEmpty()) {
            HorizontalDivider()
            customBrushes.forEach { customBrush ->
                DropdownMenuItem(
                    text = { Text(customBrush.name) },
                    onClick = {
                        onBrushSelected(customBrush.brushFamily)
                        onDismissRequest()
                    }
                )
            }
        }
    }
}