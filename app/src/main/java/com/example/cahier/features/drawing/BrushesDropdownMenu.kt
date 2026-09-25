package com.example.cahier.features.drawing

import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.ink.brush.BrushFamily
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
        // --- Default Presets ---
        defaultBrushPresets.forEach { preset ->
            DropdownMenuItem(
                text = { Text(preset.name) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(preset.iconRes),
                        contentDescription = preset.name,
                        modifier = Modifier.size(20.dp)
                    )
                },
                onClick = {
                    onBrushSelected(preset.family)
                    onDismissRequest()
                }
            )
        }

        // --- Custom Brushes from ViewModel ---
        if (customBrushes.isNotEmpty()) {
            customBrushes.forEach { customBrush ->
                DropdownMenuItem(
                    text = { Text(customBrush.name) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.brush_24px),
                            contentDescription = customBrush.name,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    onClick = {
                        // Access brushFamily from customBrush
                        onBrushSelected(customBrush.brushFamily)
                        onDismissRequest()
                    }
                )
            }
        }
    }
}