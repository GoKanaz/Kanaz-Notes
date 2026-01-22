package com.gokanaz.kanaznotes.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.gokanaz.kanaznotes.ui.viewmodel.SettingsViewModel

@Composable
fun KanazNotesTheme(
    settingsViewModel: SettingsViewModel,
    content: @Composable () -> Unit
) {
    val darkMode by settingsViewModel.darkMode.collectAsState()
    val isAmoledMode by settingsViewModel.isAmoledMode.collectAsState()
    val isDynamicColor by settingsViewModel.isDynamicColor.collectAsState()
    val selectedPalette by settingsViewModel.selectedColorPalette.collectAsState()

    val darkTheme = when (darkMode) {
        "on" -> true
        "off" -> false
        else -> isSystemInDarkTheme()
    }

    val colorScheme = when {
        isDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) {
                val dynamicDark = dynamicDarkColorScheme(context)
                if (isAmoledMode) dynamicDark.copy(surface = Color.Black, background = Color.Black) else dynamicDark
            } else {
                dynamicLightColorScheme(context)
            }
        }
        darkTheme -> {
            val palette = settingsViewModel.colorPalettes[selectedPalette]
            darkColorScheme(
                primary = palette[1],
                secondary = palette[2],
                tertiary = palette[0],
                surface = if (isAmoledMode) Color.Black else Color(0xFF1C1B1F),
                background = if (isAmoledMode) Color.Black else Color(0xFF1C1B1F)
            )
        }
        else -> {
            val palette = settingsViewModel.colorPalettes[selectedPalette]
            lightColorScheme(
                primary = palette[1],
                secondary = palette[2],
                tertiary = palette[0]
            )
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
