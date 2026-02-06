package com.gokanaz.kanaznotes.ui.theme

import androidx.compose.ui.graphics.Color

val noteColors = listOf(
    Color(0xFFF5F5F5),
    Color(0xFFFFF9C4),
    Color(0xFFE8F5E9),
    Color(0xFFE3F2FD),
    Color(0xFFFCE4EC),
    Color(0xFFEDE7F6),
    Color(0xFFF3E0D0),
    Color(0xFFE0F7FA)
)

val noteColorsDark = listOf(
    Color(0xFF2C2C2C),
    Color(0xFF3E3A2F),
    Color(0xFF2D3A2E),
    Color(0xFF2A3441),
    Color(0xFF3D2E35),
    Color(0xFF352F3D),
    Color(0xFF3A332C),
    Color(0xFF2C3839)
)

fun getCardColor(colorIndex: Int, isDark: Boolean): Color {
    val index = colorIndex.coerceIn(0, noteColors.size - 1)
    return if (isDark) noteColorsDark[index] else noteColors[index]
}

fun getTextColor(colorIndex: Int, isDark: Boolean): Color {
    return if (isDark) Color(0xFFE0E0E0) else Color(0xFF1C1C1C)
}
