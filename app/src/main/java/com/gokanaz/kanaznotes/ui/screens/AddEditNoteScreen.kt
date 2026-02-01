package com.gokanaz.kanaznotes.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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
