#!/bin/bash

cat <<'ENDOFFILE' > app/src/main/java/com/gokanaz/kanaznotes/ui/screens/HomeScreen.kt
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

ENDOFFILE

cat <<'ENDOFFILE' > app/src/main/java/com/gokanaz/kanaznotes/ui/screens/AddEditNoteScreen.kt
package com.gokanaz.kanaznotes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteScreen(
    noteViewModel: NoteViewModel,
    navController: NavHostController,
    existingNoteId: Int?
) {
    var existingNote by remember { mutableStateOf<NoteEntity?>(null) }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var color by remember { mutableStateOf(0) }
    var isPinned by remember { mutableStateOf(false) }
    var isTemplate by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var hasChanges by remember { mutableStateOf(false) }
    var showDiscardDialog by remember { mutableStateOf(false) }
    var initialized by remember { mutableStateOf(false) }

    if (!initialized && existingNoteId != null) {
        val note = noteViewModel.getNoteById(existingNoteId)
        if (note != null) {
            existingNote = note
            title = note.title
            content = note.content
            color = note.color
            isPinned = note.isPinned
            isTemplate = note.isTemplate
        }
        initialized = true
    }

    val cardColor = noteColors.getOrElse(color) { Color.White }

    fun saveNote() {
        if (title.isBlank() && content.isBlank()) {
            navController.popBackStack()
            return
        }
        val note = if (existingNote != null) {
            existingNote!!.copy(
                title = title,
                content = content,
                color = color,
                isPinned = isPinned,
                isTemplate = isTemplate,
                timestamp = System.currentTimeMillis()
            )
        } else {
            NoteEntity(
                title = title,
                content = content,
                color = color,
                isPinned = isPinned,
                isTemplate = isTemplate,
                timestamp = System.currentTimeMillis()
            )
        }
        if (existingNote != null) noteViewModel.updateNote(note) else noteViewModel.insertNote(note)
        navController.popBackStack()
    }

    fun onBack() {
        if (hasChanges) {
            showDiscardDialog = true
        } else {
            navController.popBackStack()
        }
    }

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text("Buang Perubahan?") },
            text = { Text("Perubahan yang Anda buat tidak akan disimpan.") },
            confirmButton = {
                TextButton(onClick = { navController.popBackStack() }) { Text("Buang") }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) { Text("Lanjut Edit") }
            }
        )
    }

    Scaffold(
        containerColor = cardColor,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        isPinned = !isPinned
                        hasChanges = true
                    }) {
                        Icon(if (isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin, null)
                    }
                    IconButton(onClick = { showMoreMenu = true }) {
                        Icon(Icons.Default.MoreVert, null)
                    }
                    DropdownMenu(
                        expanded = showMoreMenu,
                        onDismissRequest = { showMoreMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(if (isTemplate) "Hapus dari Template" else "Simpan sebagai Template") },
                            onClick = {
                                isTemplate = !isTemplate
                                hasChanges = true
                                showMoreMenu = false
                            },
                            leadingIcon = { Icon(Icons.Outlined.Description, null) }
                        )
                        if (existingNote != null) {
                            DropdownMenuItem(
                                text = { Text("Arsip") },
                                onClick = {
                                    noteViewModel.archiveNote(existingNote!!)
                                    showMoreMenu = false
                                    navController.popBackStack()
                                },
                                leadingIcon = { Icon(Icons.Outlined.Archive, null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Hapus") },
                                onClick = {
                                    noteViewModel.deleteNote(existingNote!!)
                                    showMoreMenu = false
                                    navController.popBackStack()
                                },
                                leadingIcon = { Icon(Icons.Outlined.Delete, null) }
                            )
                        }
                    }
                },
                colors = TopAppBarColors(
                    containerColor = cardColor,
                    scrolledContainerColor = cardColor,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { showColorPicker = !showColorPicker }) {
                    Icon(Icons.Outlined.Circle, null, tint = MaterialTheme.colorScheme.onSurface)
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { saveNote() }) {
                    Icon(Icons.Outlined.Check, null, tint = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            if (showColorPicker) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    noteColors.forEachIndexed { index, c ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(c, shape = CircleShape)
                                .border(
                                    width = if (color == index) 2.5.dp else 1.dp,
                                    color = if (color == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                    shape = CircleShape
                                )
                                .combinedClickable(onClick = {
                                    color = index
                                    hasChanges = true
                                    showColorPicker = false
                                })
                        )
                    }
                }
            }

            BasicTextField(
                value = title,
                onValueChange = { title = it; hasChanges = true },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                textStyle = MaterialTheme.typography.displaySmall.copy(color = MaterialTheme.colorScheme.onSurface),
                singleLine = true,
                decorationBox = { innerTextField ->
                    Box {
                        if (title.isEmpty()) {
                            Text("Judul", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        innerTextField()
                    }
                }
            )

            BasicTextField(
                value = content,
                onValueChange = { content = it; hasChanges = true },
                modifier = Modifier.fillMaxWidth().weight(1f),
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                decorationBox = { innerTextField ->
                    Box {
                        if (content.isEmpty()) {
                            Text("Tambah catatan...", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        innerTextField()
                    }
                }
            )
        }
    }
}

ENDOFFILE

cat <<'ENDOFFILE' > app/src/main/java/com/gokanaz/kanaznotes/ui/screens/SearchScreen.kt
package com.gokanaz.kanaznotes.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.gokanaz.kanaznotes.ui.viewmodel.NoteViewModel
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    noteViewModel: NoteViewModel,
    navController: NavHostController
) {
    val notes by noteViewModel.allNotes.collectAsState(initial = emptyList())
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(true) }

    val filtered = if (query.isBlank()) emptyList()
    else notes.filter {
        it.title.contains(query, ignoreCase = true) || it.content.contains(query, ignoreCase = true)
    }

    SearchBar(
        query = query,
        onQueryChange = { query = it },
        onSearch = {},
        active = active,
        onActiveChange = { active = it },
        placeholder = { Text("Cari catatan...") },
        leadingIcon = {
            if (active) {
                IconButton(onClick = {
                    if (query.isNotEmpty()) query = "" else { active = false; navController.popBackStack() }
                }) {
                    Icon(Icons.Default.ArrowBack, null)
                }
            } else {
                Icon(Icons.Default.Search, null)
            }
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { query = "" }) {
                    Icon(Icons.Default.Close, null)
                }
            }
        }
    ) {
        if (query.isBlank()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.Search, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Ketuk untuk mencari catatan Anda", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else if (filtered.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.SearchOff, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Tidak ditemukan hasil untuk \"$query\"", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(8.dp)) {
                items(filtered, key = { it.id }) { note ->
                    val colorIndex = note.color.coerceIn(0, noteColors.size - 1)
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { navController.navigate("edit_note/${note.id}") },
                        colors = CardColors(
                            containerColor = noteColors[colorIndex],
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            disabledContainerColor = noteColors[colorIndex],
                            disabledContentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            if (note.title.isNotBlank()) {
                                Text(note.title, style = MaterialTheme.typography.titleMedium, maxLines = 1)
                            }
                            if (note.content.isNotBlank()) {
                                Text(note.content, style = MaterialTheme.typography.bodyMedium, maxLines = 2, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text(
                                SimpleDateFormat("dd MMM yyyy").format(Date(note.timestamp)),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

ENDOFFILE

cat <<'ENDOFFILE' > app/src/main/java/com/gokanaz/kanaznotes/ui/screens/SettingsScreen.kt
package com.gokanaz.kanaznotes.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.gokanaz.kanaznotes.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    settingsViewModel: SettingsViewModel
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                SectionTitle("Tampilan")
            }
            item {
                SettingsListItem(
                    icon = Icons.Outlined.Palette,
                    title = "Gaya & Tema",
                    subtitle = "Gelap, terang, dan warna",
                    onClick = { navController.navigate("style_settings") }
                )
            }
            item {
                Spacer(modifier = Modifier.height(12.dp))
                SectionTitle("Umum")
            }
            item {
                SettingsListItem(
                    icon = Icons.Outlined.Language,
                    title = "Bahasa",
                    subtitle = "Pengaturan bahasa aplikasi",
                    onClick = { navController.navigate("language_settings") }
                )
            }
            item {
                Spacer(modifier = Modifier.height(12.dp))
                SectionTitle("Keamanan")
            }
            item {
                SettingsListItem(
                    icon = Icons.Outlined.Lock,
                    title = "Keamanan",
                    subtitle = "Kata sandi dan perlindungan",
                    onClick = { navController.navigate("security_settings") }
                )
            }
            item {
                Spacer(modifier = Modifier.height(12.dp))
                SectionTitle("Sinkronisasi")
            }
            item {
                SettingsListItem(
                    icon = Icons.Outlined.Cloud,
                    title = "Pengaturan Cloud",
                    subtitle = "WebDAV dan sinkronisasi",
                    onClick = { navController.navigate("cloud_settings") }
                )
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
    )
}

@Composable
fun SettingsListItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle) },
        leadingContent = {
            Icon(icon, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        },
        trailingContent = {
            Icon(Icons.Outlined.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}

ENDOFFILE

cat <<'ENDOFFILE' > app/src/main/java/com/gokanaz/kanaznotes/ui/screens/StyleSettingsScreen.kt
package com.gokanaz.kanaznotes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.gokanaz.kanaznotes.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StyleSettingsScreen(
    navController: NavHostController,
    settingsViewModel: SettingsViewModel
) {
    val darkMode by settingsViewModel.darkMode.collectAsState()
    val isAmoledMode by settingsViewModel.isAmoledMode.collectAsState()
    val isDynamicColor by settingsViewModel.isDynamicColor.collectAsState()
    val selectedPalette by settingsViewModel.selectedColorPalette.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gaya & Tema") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Card {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Mode Gelap", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("system" to "Sistem", "on" to "Aktif", "off" to "Nonaktif").forEach { (value, label) ->
                                FilterChip(
                                    selected = darkMode == value,
                                    onClick = { settingsViewModel.setDarkMode(value) },
                                    label = { Text(label) }
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                Card {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Mode AMOLED", style = MaterialTheme.typography.titleMedium)
                                Text("Layar hitam total di mode gelap", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(checked = isAmoledMode, onCheckedChange = { settingsViewModel.setAmoledMode(it) })
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                Card {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Warna Dinamis", style = MaterialTheme.typography.titleMedium)
                                Text("Ikuti warna perangkat (Android 12+)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(checked = isDynamicColor, onCheckedChange = { settingsViewModel.setDynamicColor(it) })
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            if (!isDynamicColor) {
                item {
                    Card {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Palet Warna", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                settingsViewModel.colorPalettes.forEachIndexed { index, palette ->
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .border(
                                                width = if (selectedPalette == index) 2.5.dp else 1.dp,
                                                color = if (selectedPalette == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .clickable { settingsViewModel.setSelectedColorPalette(index) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row {
                                            palette.take(3).forEach { c ->
                                                Box(modifier = Modifier.size(14.dp).background(c))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

ENDOFFILE

cat <<'ENDOFFILE' > app/src/main/java/com/gokanaz/kanaznotes/ui/screens/ArchiveScreen.kt
package com.gokanaz.kanaznotes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
    var noteToRestore by remember { mutableStateOf<NoteEntity?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var noteToDelete by remember { mutableStateOf<NoteEntity?>(null) }

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
                contentPadding = PaddingValues(16.dp)
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
                                        Icon(Icons.Filled.Unarchive, null, modifier = Modifier.size(20.dp))
                                    }
                                    IconButton(onClick = { noteToDelete = note; showDeleteDialog = true }, modifier = Modifier.size(36.dp)) {
                                        Icon(Icons.Filled.Delete, null, modifier = Modifier.size(20.dp))
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
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

ENDOFFILE

cat <<'ENDOFFILE' > app/src/main/java/com/gokanaz/kanaznotes/ui/screens/TemplateScreen.kt
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

ENDOFFILE

cat <<'ENDOFFILE' > app/src/main/java/com/gokanaz/kanaznotes/ui/screens/SecuritySettingsScreen.kt
package com.gokanaz.kanaznotes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.gokanaz.kanaznotes.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecuritySettingsScreen(
    navController: NavHostController,
    settingsViewModel: SettingsViewModel
) {
    val isPasswordEnabled by settingsViewModel.isPasswordEnabled.collectAsState()
    val isScreenProtection by settingsViewModel.isScreenProtectionEnabled.collectAsState()
    var showPasswordDialog by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }

    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showPasswordDialog = false; errorText = "" },
            title = { Text("Atur Kata Sandi") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; errorText = "" },
                        label = { Text("Kata Sandi Baru") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it; errorText = "" },
                        label = { Text("Konfirmasi Kata Sandi") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        isError = errorText.isNotEmpty(),
                        supportingText = { if (errorText.isNotEmpty()) Text(errorText, color = MaterialTheme.colorScheme.error) }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    when {
                        password.isEmpty() -> errorText = "Kata sandi tidak boleh kosong"
                        password != confirmPassword -> errorText = "Kata sandi tidak cocok"
                        else -> {
                            settingsViewModel.setPassword(password)
                            showPasswordDialog = false
                            password = ""
                            confirmPassword = ""
                            errorText = ""
                        }
                    }
                }) { Text("Simpan") }
            },
            dismissButton = {
                TextButton(onClick = { showPasswordDialog = false; errorText = ""; password = ""; confirmPassword = "" }) { Text("Batal") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Keamanan") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Card {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Kata Sandi", style = MaterialTheme.typography.titleMedium)
                                Text(
                                    if (isPasswordEnabled) "Kata sandi telah diatur" else "Belum diatur",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (isPasswordEnabled) {
                                OutlinedButton(onClick = { showPasswordDialog = true }) { Text("Ubah") }
                            } else {
                                Button(onClick = { showPasswordDialog = true }) { Text("Atur") }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                Card {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Perlindungan Layar", style = MaterialTheme.typography.titleMedium)
                                Text("Mencegah screenshot", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(checked = isScreenProtection, onCheckedChange = { settingsViewModel.setScreenProtection(it) })
                        }
                    }
                }
            }
        }
    }
}

ENDOFFILE

cat <<'ENDOFFILE' > app/src/main/java/com/gokanaz/kanaznotes/ui/screens/CloudSettingsScreen.kt
package com.gokanaz.kanaznotes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.gokanaz.kanaznotes.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloudSettingsScreen(
    navController: NavHostController,
    settingsViewModel: SettingsViewModel
) {
    val webdavUrl by settingsViewModel.webdavUrl.collectAsState()
    val webdavUsername by settingsViewModel.webdavUsername.collectAsState()
    val isSyncEnabled by settingsViewModel.isSyncEnabled.collectAsState()

    var url by remember { mutableStateOf(webdavUrl) }
    var username by remember { mutableStateOf(webdavUsername) }
    var password by remember { mutableStateOf("") }
    var saved by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan Cloud") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Card {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Konfigurasi WebDAV", style = MaterialTheme.typography.titleMedium)
                        OutlinedTextField(
                            value = url,
                            onValueChange = { url = it; saved = false },
                            label = { Text("URL WebDAV") },
                            placeholder = { Text("https://example.com/webdav") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it; saved = false },
                            label = { Text("Nama Pengguna") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it; saved = false },
                            label = { Text("Kata Sandi") },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = {
                                settingsViewModel.setWebDAVConfig(url, username, password)
                                saved = true
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = url.isNotBlank() && username.isNotBlank()
                        ) {
                            Text(if (saved) "Tersimpan" else "Simpan Konfigurasi")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            item {
                Card {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Sinkronisasi Otomatis", style = MaterialTheme.typography.titleMedium)
                                Text("Sinkronkan catatan secara berkala", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(checked = isSyncEnabled, onCheckedChange = { settingsViewModel.setSyncEnabled(it) })
                        }
                    }
                }
            }
        }
    }
}

ENDOFFILE

git add -A
git commit -m "fix: resolve all compile errors - imports, types, API compat"
git push origin main