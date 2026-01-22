package com.gokanaz.kanaznotes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gokanaz.kanaznotes.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    settingsViewModel: SettingsViewModel,
    onBack: () -> Unit
) {
    val isSingleColumn by settingsViewModel.isSingleColumn.collectAsState()
    val maxLines by settingsViewModel.maxLines.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("List") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
            item {
                ListItem(
                    headlineContent = { Text("Single column") },
                    supportingContent = { Text("Show notes in a single column list") },
                    trailingContent = {
                        Switch(checked = isSingleColumn, onCheckedChange = { settingsViewModel.setSingleColumn(it) })
                    }
                )
            }
            item {
                Text(
                    "Max lines: $maxLines",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Slider(
                    value = maxLines.toFloat(),
                    onValueChange = { settingsViewModel.setMaxLines(it.toInt()) },
                    valueRange = 1f..20f,
                    steps = 19
                )
            }
        }
    }
}
