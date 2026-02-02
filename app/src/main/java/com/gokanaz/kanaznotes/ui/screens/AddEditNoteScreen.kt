package com.gokanaz.kanaznotes.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.gokanaz.kanaznotes.data.local.LabelEntity
import com.gokanaz.kanaznotes.ui.viewmodel.NoteViewModel
import com.gokanaz.kanaznotes.data.local.NoteEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AddEditNoteScreen(
    noteViewModel: NoteViewModel,
    navController: NavHostController,
    existingNoteId: Int?
) {
    val scope = rememberCoroutineScope()
    val isDark = isSystemInDarkTheme()
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var color by remember { mutableStateOf(0) }
    var isPinned by remember { mutableStateOf(false) }
    var isTemplate by remember { mutableStateOf(false) }
    var selectedLabels by remember { mutableStateOf<Set<String>>(emptySet()) }
    var imageUris by remember { mutableStateOf<List<String>>(emptyList()) }
    
    var showColorPicker by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var showLabelDialog by remember { mutableStateOf(false) }
    var showDiscardDialog by remember { mutableStateOf(false) }
    var existingNote by remember { mutableStateOf<NoteEntity?>(null) }
    var autoSaveJob by remember { mutableStateOf<Job?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    val allLabels by noteViewModel.allLabels.collectAsState(initial = emptyList())

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUris = imageUris + it.toString()
            triggerAutoSave()
        }
    }

    fun triggerAutoSave() {
        autoSaveJob?.cancel()
        autoSaveJob = scope.launch {
            delay(1500)
            if (title.isNotBlank() || content.isNotBlank()) {
                isSaving = true
                val note = if (existingNote != null) {
                    existingNote!!.copy(
                        title = title,
                        content = content,
                        color = color,
                        isPinned = isPinned,
                        isTemplate = isTemplate,
                        labels = selectedLabels.joinToString(","),
                        images = imageUris.joinToString(","),
                        timestamp = System.currentTimeMillis()
                    )
                } else {
                    NoteEntity(
                        title = title,
                        content = content,
                        color = color,
                        isPinned = isPinned,
                        isTemplate = isTemplate,
                        labels = selectedLabels.joinToString(","),
                        images = imageUris.joinToString(","),
                        timestamp = System.currentTimeMillis()
                    )
                }
                if (existingNote != null) {
                    noteViewModel.updateNote(note)
                } else {
                    noteViewModel.insertNote(note)
                    existingNote = note
                }
                delay(500)
                isSaving = false
            }
        }
    }

    LaunchedEffect(existingNoteId) {
        if (existingNoteId != null) {
            scope.launch {
                val note = noteViewModel.getNoteById(existingNoteId)
                if (note != null) {
                    existingNote = note
                    title = note.title
                    content = note.content
                    color = note.color
                    isPinned = note.isPinned
                    isTemplate = note.isTemplate
                    selectedLabels = note.labels.split(",").filter { it.isNotBlank() }.toSet()
                    imageUris = note.images.split(",").filter { it.isNotBlank() }
                }
            }
        }
    }

    LaunchedEffect(title, content, selectedLabels, imageUris) {
        triggerAutoSave()
    }

    val backgroundColor = if (isDark) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onBackground
    val iconColor = MaterialTheme.colorScheme.onSurface

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text("Keluar?") },
            text = { Text("Perubahan akan disimpan otomatis.") },
            confirmButton = {
                TextButton(onClick = { navController.popBackStack() }) { Text("Keluar") }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) { Text("Batal") }
            }
        )
    }

    if (showLabelDialog) {
        LabelDialog(
            allLabels = allLabels,
            selectedLabels = selectedLabels,
            onLabelToggle = { label ->
                selectedLabels = if (label in selectedLabels) {
                    selectedLabels - label
                } else {
                    selectedLabels + label
                }
                triggerAutoSave()
            },
            onAddLabel = { labelName ->
                noteViewModel.insertLabel(LabelEntity(name = labelName, color = 0))
            },
            onDismiss = { showLabelDialog = false }
        )
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    if (isSaving) {
                        Text("Menyimpan...", style = MaterialTheme.typography.bodySmall, color = iconColor.copy(alpha = 0.6f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = iconColor)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        isPinned = !isPinned
                        triggerAutoSave()
                    }) {
                        Icon(if (isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin, null, tint = iconColor)
                    }
                    IconButton(onClick = { showMoreMenu = true }) {
                        Icon(Icons.Default.MoreVert, null, tint = iconColor)
                    }
                    DropdownMenu(
                        expanded = showMoreMenu,
                        onDismissRequest = { showMoreMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(if (isTemplate) "Hapus dari Template" else "Simpan sebagai Template") },
                            onClick = {
                                isTemplate = !isTemplate
                                triggerAutoSave()
                                showMoreMenu = false
                            },
                            leadingIcon = { Icon(Icons.Outlined.Description, null) }
                        )
                        if (existingNote != null) {
                            DropdownMenuItem(
                                text = { Text("Arsip") },
                                onClick = {
                                    existingNote?.let { noteViewModel.archiveNote(it) }
                                    showMoreMenu = false
                                    navController.popBackStack()
                                },
                                leadingIcon = { Icon(Icons.Outlined.Archive, null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Hapus") },
                                onClick = {
                                    existingNote?.let { noteViewModel.deleteNote(it) }
                                    showMoreMenu = false
                                    navController.popBackStack()
                                },
                                leadingIcon = { Icon(Icons.Outlined.Delete, null) }
                            )
                        }
                    }
                },
                colors = TopAppBarColors(
                    containerColor = backgroundColor,
                    scrolledContainerColor = backgroundColor,
                    navigationIconContentColor = iconColor,
                    titleContentColor = iconColor,
                    actionIconContentColor = iconColor
                )
            )
        },
        bottomBar = {
            Surface(
                color = backgroundColor,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { imagePickerLauncher.launch("image/*") }) {
                        Icon(Icons.Outlined.Image, "Tambah Gambar", tint = iconColor)
                    }
                    IconButton(onClick = { showLabelDialog = true }) {
                        Icon(Icons.Outlined.Label, "Tambah Label", tint = iconColor)
                    }
                    IconButton(onClick = { showColorPicker = !showColorPicker }) {
                        Icon(Icons.Outlined.Circle, "Pilih Warna", tint = iconColor)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            if (showColorPicker) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
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
                                        triggerAutoSave()
                                        showColorPicker = false
                                    })
                            )
                        }
                    }
                }
            }

            if (selectedLabels.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        selectedLabels.forEach { label ->
                            AssistChip(
                                onClick = {
                                    selectedLabels = selectedLabels - label
                                    triggerAutoSave()
                                },
                                label = { Text(label, style = MaterialTheme.typography.bodySmall) },
                                trailingIcon = {
                                    Icon(Icons.Default.Close, null, modifier = Modifier.size(16.dp))
                                }
                            )
                        }
                    }
                }
            }

            item {
                BasicTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    textStyle = MaterialTheme.typography.displaySmall.copy(color = textColor),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        Box {
                            if (title.isEmpty()) {
                                Text(
                                    "Judul",
                                    style = MaterialTheme.typography.displaySmall,
                                    color = textColor.copy(alpha = 0.4f)
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }

            items(imageUris) { uriString ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    val bitmap = remember(uriString) {
                        try {
                            val inputStream = context.contentResolver.openInputStream(Uri.parse(uriString))
                            BitmapFactory.decodeStream(inputStream)?.asImageBitmap()
                        } catch (e: Exception) {
                            null
                        }
                    }
                    
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 300.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.BrokenImage, "Gambar tidak dapat dimuat")
                        }
                    }
                    
                    IconButton(
                        onClick = {
                            imageUris = imageUris.filter { it != uriString }
                            triggerAutoSave()
                        },
                        modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            null,
                            tint = Color.White,
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                .padding(4.dp)
                        )
                    }
                }
            }

            item {
                BasicTextField(
                    value = content,
                    onValueChange = { content = it },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 200.dp),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = textColor),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    decorationBox = { innerTextField ->
                        Box {
                            if (content.isEmpty()) {
                                Text(
                                    "Tambah catatan...",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = textColor.copy(alpha = 0.4f)
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun LabelDialog(
    allLabels: List<LabelEntity>,
    selectedLabels: Set<String>,
    onLabelToggle: (String) -> Unit,
    onAddLabel: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var newLabelName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Kelola Label") },
        text = {
            Column {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = newLabelName,
                        onValueChange = { newLabelName = it },
                        label = { Text("Label Baru") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    IconButton(
                        onClick = {
                            if (newLabelName.isNotBlank()) {
                                onAddLabel(newLabelName.trim())
                                newLabelName = ""
                            }
                        }
                    ) {
                        Icon(Icons.Default.Add, "Tambah")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                allLabels.forEach { label ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = label.name in selectedLabels,
                            onCheckedChange = { onLabelToggle(label.name) }
                        )
                        Text(label.name, modifier = Modifier.weight(1f))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Selesai") }
        }
    )
}
