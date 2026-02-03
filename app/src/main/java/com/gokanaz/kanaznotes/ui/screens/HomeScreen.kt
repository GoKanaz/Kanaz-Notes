package com.gokanaz.kanaznotes.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.gokanaz.kanaznotes.R
import com.gokanaz.kanaznotes.data.local.NoteEntity
import com.gokanaz.kanaznotes.ui.viewmodel.NoteViewModel
import com.gokanaz.kanaznotes.ui.viewmodel.SettingsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import kotlinx.coroutines.launch

val noteColors = listOf(
    Color(0xFFF5F5F5),
    Color(0xFFFFF9C4),
    Color(0xFFE8F5E9),
    Color(0xFFE3F2FD),
    Color(0xFFFCE4EC),
    Color(0xFFEDE7F6),
    Color(0xFFF3E0D0),
    Color(0xFFE0F7FA)
)

val noteColorsDark = listOf(
    Color(0xFF2C2C2C),
    Color(0xFF3E3A2F),
    Color(0xFF2D3A2E),
    Color(0xFF2A3441),
    Color(0xFF3D2E35),
    Color(0xFF352F3D),
    Color(0xFF3A332C),
    Color(0xFF2C3839)
)

fun getCardColor(colorIndex: Int, isDark: Boolean): Color {
    val index = colorIndex.coerceIn(0, noteColors.size - 1)
    return if (isDark) noteColorsDark[index] else noteColors[index]
}

fun getTextColor(colorIndex: Int, isDark: Boolean): Color {
    return if (isDark) Color(0xFFE0E0E0) else Color(0xFF1C1C1C)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    noteViewModel: NoteViewModel,
    settingsViewModel: SettingsViewModel,
    navController: NavHostController
) {
    val notes by noteViewModel.allNotes.collectAsState(initial = emptyList())
    val allLabels by noteViewModel.allLabels.collectAsState(initial = emptyList())
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var noteToDelete by remember { mutableStateOf<NoteEntity?>(null) }
    var selectedNotes by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var showColorPicker by remember { mutableStateOf(false) }
    var noteForColor by remember { mutableStateOf<NoteEntity?>(null) }
    var selectedLabelFilter by remember { mutableStateOf<String?>(null) }
    val isDark = isSystemInDarkTheme()

    val filteredNotes = if (selectedLabelFilter != null) {
        notes.filter { note ->
            note.labels.split(",").map { it.trim() }.contains(selectedLabelFilter)
        }
    } else {
        notes
    }

    if (showDeleteDialog && noteToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.archive_note)) },
            text = { Text(stringResource(R.string.archive_message)) },
            confirmButton = {
                TextButton(onClick = {
                    noteViewModel.archiveNote(noteToDelete!!)
                    showDeleteDialog = false
                    noteToDelete = null
                }) { Text(stringResource(R.string.archive)) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }

    if (showColorPicker && noteForColor != null) {
        AlertDialog(
            onDismissRequest = { showColorPicker = false },
            title = { Text(stringResource(R.string.choose_color_title)) },
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
                TextButton(onClick = { showColorPicker = false }) { Text(stringResource(R.string.close)) }
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(modifier = Modifier.height(8.dp))

                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.notes)) },
                    selected = selectedLabelFilter == null,
                    onClick = {
                        selectedLabelFilter = null
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Outlined.Note, null) }
                )
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.archive)) },
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
                    label = { Text(stringResource(R.string.templates)) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate("templates")
                        }
                    },
                    icon = { Icon(Icons.Outlined.Description, null) }
                )
                
                if (allLabels.isNotEmpty()) {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                    Text(
                        stringResource(R.string.label),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
                    )
                    allLabels.forEach { label ->
                        NavigationDrawerItem(
                            label = { Text(label.name) },
                            selected = selectedLabelFilter == label.name,
                            onClick = {
                                selectedLabelFilter = if (selectedLabelFilter == label.name) null else label.name
                                scope.launch { drawerState.close() }
                            },
                            icon = { Icon(Icons.Outlined.Label, null) }
                        )
                    }
                }
                
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.settings)) },
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
                        title = { Text("${selectedNotes.size} ${stringResource(R.string.selected)}") },
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
                        title = { 
                            Column {
                                Text(stringResource(R.string.app_name))
                                if (selectedLabelFilter != null) {
                                    Text(
                                        "${stringResource(R.string.label)}: $selectedLabelFilter",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, null)
                            }
                        },
                        actions = {
                            if (selectedLabelFilter != null) {
                                IconButton(onClick = { selectedLabelFilter = null }) {
                                    Icon(Icons.Default.Close, stringResource(R.string.remove_filter))
                                }
                            }
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
                        Icon(Icons.Default.Add, stringResource(R.string.add_note))
                    }
                }
            }
        ) { padding ->
            if (filteredNotes.isEmpty()) {
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
                        if (selectedLabelFilter != null) stringResource(R.string.no_notes_with_label) else stringResource(R.string.no_notes),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (selectedLabelFilter == null) {
                        Text(
                            stringResource(R.string.add_note_hint),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    val pinnedNotes = filteredNotes.filter { it.isPinned }
                    val unpinnedNotes = filteredNotes.filter { !it.isPinned }

                    if (pinnedNotes.isNotEmpty()) {
                        item {
                            Text(
                                stringResource(R.string.pinned),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        items(pinnedNotes, key = { it.id }) { note ->
                            NoteCard(
                                note = note,
                                isSelected = note.id in selectedNotes,
                                isDark = isDark,
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
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            Text(
                                stringResource(R.string.all),
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
                            isDark = isDark,
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteCard(
    note: NoteEntity,
    isSelected: Boolean,
    isDark: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onArchive: () -> Unit,
    onPin: () -> Unit
) {
    val cardColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.surfaceVariant
        } else {
            getCardColor(note.color, isDark)
        }
    )
    val textColor = getTextColor(note.color, isDark)
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm")
    val formattedDate = dateFormat.format(Date(note.timestamp))
    val labels = note.labels.split(",").filter { it.isNotBlank() }
    val images = note.images.split(",").filter { it.isNotBlank() }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
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
                if (note.title.isNotBlank()) {
                    Text(
                        note.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                        color = textColor
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
            
            if (images.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(images.take(3)) { imagePath ->
                        val bitmap = remember(imagePath) {
                            try {
                                BitmapFactory.decodeFile(imagePath)?.asImageBitmap()
                            } catch (e: Exception) {
                                null
                            }
                        }
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
            
            if (labels.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(labels) { label ->
                        AssistChip(
                            onClick = {},
                            label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.height(24.dp)
                        )
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
                    color = textColor.copy(alpha = 0.8f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                formattedDate,
                style = MaterialTheme.typography.labelSmall,
                color = textColor.copy(alpha = 0.6f)
            )
        }
    }
}
