package com.gokanaz.kanaznotes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.gokanaz.kanaznotes.data.local.NoteEntity
import com.gokanaz.kanaznotes.ui.viewmodel.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateScreen(
    noteViewModel: NoteViewModel,
    navController: NavHostController
) {
    val templates by noteViewModel.templates.collectAsState(initial = emptyList())
    var showUseDialog by remember { mutableStateOf(false) }
    var selectedTemplate by remember { mutableStateOf<NoteEntity?>(null) }

    if (showUseDialog && selectedTemplate != null) {
        AlertDialog(
            onDismissRequest = { showUseDialog = false },
            title = { Text("Gunakan Template") },
            text = { Text("Buat catatan baru dari template \"${selectedTemplate!!.title}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    val newNote = NoteEntity(
                        title = selectedTemplate!!.title,
                        content = selectedTemplate!!.content,
                        color = selectedTemplate!!.color,
                        timestamp = System.currentTimeMillis(),
                        isTemplate = false
                    )
                    noteViewModel.insertNote(newNote)
                    showUseDialog = false
                    selectedTemplate = null
                    navController.popBackStack()
                }) { Text("Buat") }
            },
            dismissButton = {
                TextButton(onClick = { showUseDialog = false }) { Text("Batal") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Template") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        if (templates.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Outlined.Description, null, modifier = Modifier.size(56.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Belum ada template", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Simpan catatan sebagai template dari layar edit", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(templates, key = { it.id }) { template ->
                    val colorIndex = template.color.coerceIn(0, noteColors.size - 1)
                    Card(
                        colors = CardColors(
                            containerColor = noteColors[colorIndex],
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            disabledContainerColor = noteColors[colorIndex],
                            disabledContentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(template.title, style = MaterialTheme.typography.titleMedium)
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(
                                        onClick = { noteViewModel.deleteNote(template) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(Icons.Filled.Delete, null, modifier = Modifier.size(18.dp))
                                    }
                                    Button(
                                        onClick = { selectedTemplate = template; showUseDialog = true },
                                        modifier = Modifier.height(32.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp)
                                    ) {
                                        Text("Gunakan", style = MaterialTheme.typography.labelMedium)
                                    }
                                }
                            }
                            if (template.content.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(template.content, style = MaterialTheme.typography.bodyMedium, maxLines = 2, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

