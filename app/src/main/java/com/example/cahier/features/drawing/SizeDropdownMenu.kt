package com.example.cahier.features.drawing

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SizeDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onSizeChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val sizes = listOf(
        "Fine" to 2f,
        "Medium" to 5f,
        "Bold" to 10f,
        "Extra Bold" to 20f
    )

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        sizes.forEach { (label, sizeValue) ->
            DropdownMenuItem(
                text = { Text(label) },
                onClick = { onSizeChange(sizeValue) }
            )
        }
    }
}