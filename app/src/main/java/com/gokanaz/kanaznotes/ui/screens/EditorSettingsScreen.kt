package com.gokanaz.kanaznotes.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp

@Composable
fun EditorSettingsScreen() {
    Column {
        Text(
            text = "Editor Settings",
            fontSize = 14.sp // gunakan Int, bukan Float
        )
    }
}
