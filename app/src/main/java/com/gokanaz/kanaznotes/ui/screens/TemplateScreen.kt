package com.gokanaz.kanaznotes.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TemplateScreen(
    noteViewModel: NoteViewModel,
    navController: NavHostController
) {
    val templates by noteViewModel.templates.collectAsState(initial = emptyList())
    val isDark = isSystemInDarkTheme()
    var showUseTemplateDialog by remember { mutableStateOf(false) }
    var selectedTemplate by remember { mutableStateOf<NoteEntity?>(null) }

    if (showUseTemplateDialog && selectedTemplate != null) {
        AlertDialog(
            onDismissRequest = { showUseTemplateDialog = false },
            title = { Text("Gunakan Template") },
            text = { Text("Buat catatan baru dari template ini?") },
            confirmButton = {
                TextButton(onClick = {
                    val newNote = selectedTemplate!!.copy(
                        id = 0,
                        isTemplate = false,
                        timestamp = System.currentTimeMillis()
                    )
                    noteViewModel.insertNote(newNote)
                    showUseTemplateDialog = false
                    selectedTemplate = null
                    navController.popBackStack()
                }) {
                    Text("Gunakan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUseTemplateDialog = false }) {
                    Text("Batal")
                }
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
                Icon(
                    Icons.Outlined.Description,
                    null,
                    modifier = Modifier.size(56.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Belum ada template",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Template akan muncul di sini",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(templates, key = { it.id }) { template ->
                    val cardColor = getCardColor(template.color, isDark)
                    val textColor = getTextColor(template.color, isDark)
                    
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable(
                                onClick = {
                                    selectedTemplate = template
                                    showUseTemplateDialog = true
                                },
                                onLongClick = {
                                    navController.navigate("edit_note/${template.id}")
                                }
                            ),
                        colors = CardColors(
                            containerColor = cardColor,
                            contentColor = textColor,
                            disabledContainerColor = cardColor,
                            disabledContentColor = textColor
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    template.title.ifBlank { "(Tanpa Judul)" },
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.weight(1f),
                                    color = textColor
                                )
                                Icon(
                                    Icons.Outlined.Description,
                                    null,
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            if (template.content.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    template.content,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 2,
                                    color = textColor.copy(alpha = 0.8f)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                SimpleDateFormat("dd MMM yyyy").format(Date(template.timestamp)),
                                style = MaterialTheme.typography.labelSmall,
                                color = textColor.copy(alpha = 0.6f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
