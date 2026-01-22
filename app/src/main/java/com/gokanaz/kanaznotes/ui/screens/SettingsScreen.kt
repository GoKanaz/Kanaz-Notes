package com.gokanaz.kanaznotes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    onStyle: () -> Unit,
    onLanguage: () -> Unit,
    onCalendar: () -> Unit,
    onSecurity: () -> Unit,
    onCloudUpload: () -> Unit,
    onCloud: () -> Unit
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingItem(
                icon = Icons.Filled.Palette,
                text = "Style Settings",
                onClick = onStyle
            )
            SettingItem(
                icon = Icons.Filled.Language,
                text = "Language",
                onClick = onLanguage
            )
            SettingItem(
                icon = Icons.Filled.CalendarToday,
                text = "Calendar",
                onClick = onCalendar
            )
            SettingItem(
                icon = Icons.Filled.Security,
                text = "Security",
                onClick = onSecurity
            )
            SettingItem(
                icon = Icons.Filled.CloudUpload,
                text = "Upload to Cloud",
                onClick = onCloudUpload
            )
            SettingItem(
                icon = Icons.Filled.Cloud,
                text = "Cloud Settings",
                onClick = onCloud
            )
        }
    }
}

@Composable
fun SettingItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            Icon(imageVector = icon, contentDescription = text)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text)
        }
        IconButton(onClick = onClick) {
            Icon(imageVector = Icons.Filled.ChevronRight, contentDescription = "Go")
        }
    }
}
