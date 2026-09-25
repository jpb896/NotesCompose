package com.example.cahier.features.drawing

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.cahier.R
import com.example.cahier.core.data.CustomBrush

@Composable
fun BrushesDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onBrushChange: (CustomBrush) -> Unit,
    customBrushes: List<CustomBrush>,
    modifier: Modifier = Modifier
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        customBrushes.forEach { customBrush ->
            DropdownMenuItem(
                text = { Text(customBrush.name) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.brush_24px),
                        contentDescription = null
                    )
                },
                onClick = {
                    onBrushChange(customBrush)
                    onDismissRequest()
                }
            )
        }
    }
}