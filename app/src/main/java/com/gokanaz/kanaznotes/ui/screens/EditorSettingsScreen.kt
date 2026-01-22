package com.gokanaz.kanaznotes.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gokanaz.kanaznotes.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorSettingsScreen(
    settingsViewModel: SettingsViewModel,
    onBack: () -> Unit
) {
    val textSize by settingsViewModel.textSize.collectAsState()
    val dateFormat by settingsViewModel.dateFormat.collectAsState()
    
    var showTextSizeDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editor") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            item {
                ListItem(
                    headlineContent = { Text("Text size") },
                    supportingContent = { Text(textSize.toString()) },
                    modifier = Modifier.clickable { showTextSizeDialog = true }
                )
            }
            item {
                ListItem(
                    headlineContent = { Text("Date format") },
                    supportingContent = { Text(dateFormat) }
                )
            }
        }
    }

    if (showTextSizeDialog) {
        AlertDialog(
            onDismissRequest = { showTextSizeDialog = false },
            title = { Text("Text size") },
            text = {
                Column {
                    listOf("Small", "Medium", "Large", "Extra Large").forEach { size ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val sizeValue = when(size) {
                                        "Small" -> 14f
                                        "Medium" -> 16f
                                        "Large" -> 18f
                                        "Extra Large" -> 20f
                                        else -> 16f
                                    }
                                    settingsViewModel.setTextSize(sizeValue)
                                    showTextSizeDialog = false
                                }
                                .padding(16.dp)
                        ) { 
                            Text(text = size) 
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTextSizeDialog = false }) { Text("Cancel") }
            }
        )
    }
}
