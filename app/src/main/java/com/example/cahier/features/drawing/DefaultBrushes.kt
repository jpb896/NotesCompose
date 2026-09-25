package com.example.cahier.features.drawing

import androidx.annotation.DrawableRes
import androidx.ink.brush.StockBrushes
import com.example.cahier.R

data class PresetBrushItem(
    val name: String,
    val family: androidx.ink.brush.BrushFamily,
    @DrawableRes val iconRes: Int
)

val defaultBrushPresets = listOf(
    PresetBrushItem("Pen", StockBrushes.pressurePen(), R.drawable.stylus_pen_24px), // Or StockBrushes.pressurePenLatest
    PresetBrushItem("Marker", StockBrushes.marker(), R.drawable.ink_marker_24px),
    PresetBrushItem("Highlighter", StockBrushes.highlighter(), R.drawable.ink_highlighter_24px),
    PresetBrushItem("Dashed Line", StockBrushes.marker(), R.drawable.ink_marker_24px),
    PresetBrushItem("Emoji Highlighter", StockBrushes.highlighter(), R.drawable.ic_emoji_highlighter)
)