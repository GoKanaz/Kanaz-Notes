package com.gokanaz.kanaznotes.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.gokanaz.kanaznotes.ui.viewmodel.SettingsViewModel

@Composable
fun SecuritySettingsScreen(settingsViewModel: SettingsViewModel) {
    Column {
        Text(text = "Security Settings")
        settingsViewModel.setPassword("1234")
    }
}
