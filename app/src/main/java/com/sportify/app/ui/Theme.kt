package com.sportify.app.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object SColors {
    val Bg = Color(0xFF0E0E12)
    val Surface = Color(0xFF1A1A22)
    val Accent = Color(0xFF8F7BFF)
    val Pink = Color(0xFFFF6BA8)
    val Text = Color(0xFFF4F4F8)
    val Muted = Color(0xFFA0A0B0)
}

@Composable
fun SportifyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            background = SColors.Bg, surface = SColors.Surface,
            primary = SColors.Accent, onBackground = SColors.Text, onSurface = SColors.Text
        ),
        content = content
    )
}
