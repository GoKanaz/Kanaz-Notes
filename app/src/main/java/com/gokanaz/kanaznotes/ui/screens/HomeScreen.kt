package com.gokanaz.kanaznotes.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.gokanaz.kanaznotes.data.local.NoteEntity
import com.gokanaz.kanaznotes.ui.viewmodel.NoteViewModel
import com.gokanaz.kanaznotes.ui.viewmodel.SettingsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import kotlinx.coroutines.launch

val noteColors = listOf(
    Color(0xFFFFFBFE),
    Color(0xFFFFF9C4),
    Color(0xFFE8F5E9),
    Color(0xFFE3F2FD),
    Color(0xFFFCE4EC),
    Color(0xFFEDE7F6),
    Color(0xFFF3E0D0),
    Color(0xFFE0F7FA)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    noteViewModel: NoteViewModel,
    settingsViewModel: SettingsViewModel,
    navController: NavHostController
) {
    val notes by noteViewModel.allNotes.collectAsState(initial = emptyList())
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var noteToDelete by remember { mutableStateOf<NoteEntity?>(null) }
    var selectedNotes by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var showColorPicker by remember { mutableStateOf(false) }
    var noteForColor by remember { mutableStateOf<NoteEntity?>(null) }

    if (showDeleteDialog && noteToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Arsip Catatan") },
            text = { Text("Catatan ini akan dipindahkan ke arsip. Anda dapat memulihkannya nanti.") },
            confirmButton = {
                TextButton(onClick = {
                    noteViewModel.archiveNote(noteToDelete!!)
                    showDeleteDialog = false
                    noteToDelete = null
                }) { Text("Arsip") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
            }
        )
    }

    if (showColorPicker && noteForColor != null) {
        AlertDialog(
            onDismissRequest = { showColorPicker = false },
            title = { Text("Pilih Warna") },
            text = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 8.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    noteColors.forEachIndexed { index, color ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(color, shape = CircleShape)
                                .border(
                                    width = if (noteForColor!!.color == index) 2.5.dp else 1.dp,
                                    color = if (noteForColor!!.color == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                    shape = CircleShape
                                )
                                .combinedClickable(onClick = {
                                    noteViewModel.updateNote(noteForColor!!.copy(color = index))
                                    showColorPicker = false
                                    noteForColor = null
                                })
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showColorPicker = false }) { Text("Tutup") }
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "KanazNotes",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(modifier = Modifier.height(8.dp))

                NavigationDrawerItem(
                    label = { Text("Catatan") },
                    selected = true,
                    onClick = { scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Outlined.Note, null) }
                )
                NavigationDrawerItem(
                    label = { Text("Arsip") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate("archive")
                        }
                    },
                    icon = { Icon(Icons.Outlined.Archive, null) }
                )
                NavigationDrawerItem(
                    label = { Text("Template") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate("templates")
                        }
                    },
                    icon = { Icon(Icons.Outlined.Description, null) }
                )
                Divider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                NavigationDrawerItem(
                    label = { Text("Pengaturan") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate("settings")
                        }
                    },
                    icon = { Icon(Icons.Outlined.Settings, null) }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                if (selectedNotes.isNotEmpty()) {
                    TopAppBar(
                        title = { Text("${selectedNotes.size} dipilih") },
                        navigationIcon = {
                            IconButton(onClick = { selectedNotes = emptySet() }) {
                                Icon(Icons.Default.Close, null)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                val note = notes.firstOrNull { it.id in selectedNotes }
                                if (note != null) {
                                    noteForColor = note
                                    showColorPicker = true
                                }
                            }) {
                                Icon(Icons.Outlined.Circle, null)
                            }
                            IconButton(onClick = {
                                notes.filter { it.id in selectedNotes }.forEach { noteViewModel.togglePin(it) }
                                selectedNotes = emptySet()
                            }) {
                                Icon(Icons.Outlined.PushPin, null)
                            }
                            IconButton(onClick = {
                                notes.filter { it.id in selectedNotes }.forEach { noteViewModel.archiveNote(it) }
                                selectedNotes = emptySet()
                            }) {
                                Icon(Icons.Outlined.Archive, null)
                            }
                            IconButton(onClick = {
                                notes.filter { it.id in selectedNotes }.forEach { noteViewModel.deleteNote(it) }
                                selectedNotes = emptySet()
                            }) {
                                Icon(Icons.Outlined.Delete, null)
                            }
                        }
                    )
                } else {
                    TopAppBar(
                        title = { Text("KanazNotes") },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, null)
                            }
                        },
                        actions = {
                            IconButton(onClick = { navController.navigate("search") }) {
                                Icon(Icons.Default.Search, null)
                            }
                        }
                    )
                }
            },
            floatingActionButton = {
                if (selectedNotes.isEmpty()) {
                    FloatingActionButton(onClick = { navController.navigate("add_note") }) {
                        Icon(Icons.Default.Add, "Tambah Catatan")
                    }
                }
            }
        ) { padding ->
            if (notes.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Outlined.NoteAdd,
                        null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Belum ada catatan",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Ketuk tombol + untuk menambahkan catatan baru",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    val pinnedNotes = notes.filter { it.isPinned }
                    val unpinnedNotes = notes.filter { !it.isPinned }

                    if (pinnedNotes.isNotEmpty()) {
                        item {
                            Text(
                                "Disematkan",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        items(pinnedNotes, key = { it.id }) { note ->
                            NoteCard(
                                note = note,
                                isSelected = note.id in selectedNotes,
                                onLongClick = { selectedNotes = selectedNotes + note.id },
                                onClick = {
                                    if (selectedNotes.isNotEmpty()) {
                                        selectedNotes = if (note.id in selectedNotes) selectedNotes - note.id else selectedNotes + note.id
                                    } else {
                                        navController.navigate("edit_note/${note.id}")
                                    }
                                },
                                onArchive = {
                                    noteToDelete = note
                                    showDeleteDialog = true
                                },
                                onPin = { noteViewModel.togglePin(note) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        item {
                            Divider(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            Text(
                                "Semua",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    items(unpinnedNotes, key = { it.id }) { note ->
                        NoteCard(
                            note = note,
                            isSelected = note.id in selectedNotes,
                            onLongClick = { selectedNotes = selectedNotes + note.id },
                            onClick = {
                                if (selectedNotes.isNotEmpty()) {
                                    selectedNotes = if (note.id in selectedNotes) selectedNotes - note.id else selectedNotes + note.id
                                } else {
                                    navController.navigate("edit_note/${note.id}")
                                }
                            },
                            onArchive = {
                                noteToDelete = note
                                showDeleteDialog = true
                            },
                            onPin = { noteViewModel.togglePin(note) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun NoteCard(
    note: NoteEntity,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onArchive: () -> Unit,
    onPin: () -> Unit
) {
    val colorIndex = note.color.coerceIn(0, noteColors.size - 1)
    val cardColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.surfaceVariant else noteColors[colorIndex]
    )
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm")
    val formattedDate = dateFormat.format(Date(note.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        colors = CardColors(
            containerColor = cardColor,
            contentColor = MaterialTheme.colorScheme.onSurface,
            disabledContainerColor = cardColor,
            disabledContentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (note.title.isNotBlank()) {
                    Text(
                        note.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row {
                    if (note.isPinned) {
                        Icon(Icons.Filled.PushPin, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                    }
                    if (isSelected) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Filled.CheckCircle, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            if (note.content.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    note.content,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                formattedDate,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

