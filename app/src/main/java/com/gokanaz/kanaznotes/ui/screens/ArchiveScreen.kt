package com.gokanaz.kanaznotes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Archive
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchiveScreen(
    noteViewModel: NoteViewModel,
    navController: NavHostController
) {
    val archivedNotes by noteViewModel.archivedNotes.collectAsState(initial = emptyList())
    var showRestoreDialog by remember { mutableStateOf(false) }
    var noteToRestore by remember<MutableState<NoteEntity?>>(mutableStateOf(null))
    var showDeleteDialog by remember { mutableStateOf(false) }
    var noteToDelete by remember<MutableState<NoteEntity?>>(mutableStateOf(null))

    if (showRestoreDialog && noteToRestore != null) {
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            title = { Text("Pulihkan Catatan") },
            text = { Text("Catatan ini akan dipulihkan ke daftar utama.") },
            confirmButton = {
                TextButton(onClick = {
                    noteViewModel.unarchiveNote(noteToRestore!!)
                    showRestoreDialog = false
                    noteToRestore = null
                }) { Text("Pulihkan") }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreDialog = false }) { Text("Batal") }
            }
        )
    }

    if (showDeleteDialog && noteToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Selamanya") },
            text = { Text("Catatan ini akan dihapus secara permanen dan tidak dapat dipulihkan.") },
            confirmButton = {
                TextButton(onClick = {
                    noteViewModel.deleteNote(noteToDelete!!)
                    showDeleteDialog = false
                    noteToDelete = null
                }) { Text("Hapus", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Arsip") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        if (archivedNotes.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Outlined.Archive, null, modifier = Modifier.size(56.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Arsip kosong", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Catatan yang diarsip akan muncul di sini", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalItemSpacing = 8.dp
            ) {
                items(archivedNotes, key = { it.id }) { note ->
                    val colorIndex = note.color.coerceIn(0, noteColors.size - 1)
                    Card(
                        colors = CardColors(
                            containerColor = noteColors[colorIndex],
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            disabledContainerColor = noteColors[colorIndex],
                            disabledContentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(note.title.ifBlank { "(Tanpa Judul)" }, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                                Row {
                                    IconButton(onClick = { noteToRestore = note; showRestoreDialog = true }, modifier = Modifier.size(36.dp)) {
                                        Icon(Icons.Default.Unarchive, null, modifier = Modifier.size(20.dp))
                                    }
                                    IconButton(onClick = { noteToDelete = note; showDeleteDialog = true }, modifier = Modifier.size(36.dp)) {
                                        Icon(Icons.Default.Delete, null, modifier = Modifier.size(20.dp))
                                    }
                                }
                            }
                            if (note.content.isNotBlank()) {
                                Text(note.content, style = MaterialTheme.typography.bodyMedium, maxLines = 2, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                SimpleDateFormat("dd MMM yyyy").format(Date(note.timestamp)),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
