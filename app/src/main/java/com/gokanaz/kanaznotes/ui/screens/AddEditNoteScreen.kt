package com.gokanaz.kanaznotes.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import com.gokanaz.kanaznotes.R
import com.gokanaz.kanaznotes.data.local.LabelEntity
import com.gokanaz.kanaznotes.ui.viewmodel.NoteViewModel
import com.gokanaz.kanaznotes.data.local.NoteEntity
import com.gokanaz.kanaznotes.util.ImageHelper
import com.gokanaz.kanaznotes.util.AudioHelper
import com.gokanaz.kanaznotes.util.PdfHelper
import com.gokanaz.kanaznotes.ui.theme.noteColors
import com.gokanaz.kanaznotes.ui.theme.getCardColor
import com.gokanaz.kanaznotes.ui.theme.getTextColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import android.graphics.BitmapFactory

data class MarkdownAction(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String,
    val prefix: String,
    val suffix: String = ""
)

@Composable
fun ConfirmationDialog(
    showDialog: Boolean,
    title: String,
    message: String,
    confirmButtonText: String = "Confirm",
    isDestructive: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = title) },
            text = { Text(text = message) },
            confirmButton = {
                TextButton(
                    onClick = onConfirm,
                    colors = if (isDestructive) {
                        ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    } else {
                        ButtonDefaults.textButtonColors()
                    }
                ) {
                    Text(confirmButtonText)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

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
    val scrollState = rememberScrollState()

    var title by remember { mutableStateOf("") }
    var contentTextField by remember { mutableStateOf(TextFieldValue("")) }
    var color by remember { mutableStateOf(0) }
    var isPinned by remember { mutableStateOf(false) }
    var isTemplate by remember { mutableStateOf(false) }
    var selectedLabels by remember { mutableStateOf<Set<String>>(emptySet()) }
    var imageUris by remember { mutableStateOf<List<String>>(emptyList()) }
    var audioFiles by remember { mutableStateOf<List<String>>(emptyList()) }
    
    var showColorPicker by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var showLabelDialog by remember { mutableStateOf(false) }
    var showDiscardDialog by remember { mutableStateOf(false) }
    var showTemplateDialog by remember { mutableStateOf(false) }
    var showArchiveDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var existingNote by remember { mutableStateOf<NoteEntity?>(null) }
    var autoSaveJob by remember { mutableStateOf<Job?>(null) }
    var isSaving by remember { mutableStateOf(false) }
    var isRecording by remember { mutableStateOf(false) }
    var currentRecordingPath by remember { mutableStateOf<String?>(null) }
    var playingAudioIndex by remember { mutableStateOf<Int?>(null) }
    var isInitialLoad by remember { mutableStateOf(true) }
    var toastMessage by remember { mutableStateOf<String?>(null) }

    val allLabels by noteViewModel.allLabels.collectAsState(initial = emptyList())

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val path = AudioHelper.startRecording(context)
            if (path != null) {
                currentRecordingPath = path
                isRecording = true
            }
        }
    }

    fun saveNote() {
        scope.launch {
            val content = contentTextField.text
            if (title.isNotBlank() || content.isNotBlank() || imageUris.isNotEmpty() || audioFiles.isNotEmpty()) {
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
                        audioFiles = audioFiles.joinToString(","),
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
                        audioFiles = audioFiles.joinToString(","),
                        timestamp = System.currentTimeMillis()
                    )
                }
                
                if (existingNote != null) {
                    noteViewModel.updateNote(note)
                } else {
                    noteViewModel.insertNote(note)
                    existingNote = note.copy(id = note.id)
                }
                
                delay(500)
                isSaving = false
            }
        }
    }

    fun triggerAutoSave() {
        autoSaveJob?.cancel()
        autoSaveJob = scope.launch {
            delay(1500)
            saveNote()
        }
    }

    fun shareNote() {
        val shareText = buildString {
            if (title.isNotBlank()) {
                append("$title\n\n")
            }
            append(contentTextField.text)
        }
        
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_note)))
    }

    fun saveToPdf() {
        scope.launch {
            val pdfFile = PdfHelper.createPdfFromNote(context, title, contentTextField.text, System.currentTimeMillis())
            if (pdfFile != null) {
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", pdfFile)
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/pdf")
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                }
                context.startActivity(intent)
            }
        }
    }

    fun insertMarkdown(action: MarkdownAction) {
        val text = contentTextField.text
        val selection = contentTextField.selection
        
        val newTextField = when {
            selection.collapsed -> {
                val newText = StringBuilder(text)
                    .insert(selection.start, action.prefix + action.suffix)
                    .toString()
                val newCursorPos = selection.start + action.prefix.length
                TextFieldValue(
                    text = newText,
                    selection = TextRange(newCursorPos)
                )
            }
            else -> {
                val selectedText = text.substring(selection.start, selection.end)
                val wrappedText = action.prefix + selectedText + action.suffix
                val newText = text.replaceRange(selection.start, selection.end, wrappedText)
                val newCursorPos = selection.start + action.prefix.length + selectedText.length
                TextFieldValue(
                    text = newText,
                    selection = TextRange(newCursorPos)
                )
            }
        }
        
        contentTextField = newTextField
        triggerAutoSave()
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                val savedPath = ImageHelper.saveImageToInternalStorage(context, it)
                if (savedPath != null) {
                    imageUris = imageUris + savedPath
                    saveNote()
                }
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
                    contentTextField = TextFieldValue(note.content)
                    color = note.color
                    isPinned = note.isPinned
                    isTemplate = note.isTemplate
                    selectedLabels = note.labels.split(",").filter { it.isNotBlank() }.toSet()
                    imageUris = note.images.split(",").filter { it.isNotBlank() }
                    audioFiles = note.audioFiles.split(",").filter { it.isNotBlank() }
                    delay(300)
                    scrollState.animateScrollTo(scrollState.maxValue)
                }
            }
        }
    }

    LaunchedEffect(contentTextField.text) {
        if (contentTextField.text.isNotEmpty() && isInitialLoad) {
            delay(300)
            scrollState.animateScrollTo(scrollState.maxValue)
            isInitialLoad = false
        }
    }

    LaunchedEffect(title, contentTextField.text, selectedLabels) {
        triggerAutoSave()
    }

    LaunchedEffect(imageUris) {
        if (imageUris.isNotEmpty()) {
            triggerAutoSave()
        }
    }

    LaunchedEffect(isPinned) {
        triggerAutoSave()
    }

    LaunchedEffect(isTemplate) {
        triggerAutoSave()
    }

    LaunchedEffect(toastMessage) {
        if (toastMessage != null) {
            Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
            delay(3000)
            toastMessage = null
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            AudioHelper.stopAudio()
            if (isRecording) {
                AudioHelper.stopRecording()
            }
        }
    }

    val backgroundColor = getCardColor(color, isDark)
    val textColor = getTextColor(color, isDark)
    val iconColor = if (isDark) Color(0xFFE0E0E0) else Color(0xFF1C1C1C)

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text(stringResource(R.string.exit_question)) },
            text = { Text(stringResource(R.string.auto_save_message)) },
            confirmButton = {
                TextButton(onClick = { navController.popBackStack() }) { Text(stringResource(R.string.exit)) }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }

    ConfirmationDialog(
        showDialog = showTemplateDialog,
        title = if (isTemplate) stringResource(R.string.remove_template_title) else stringResource(R.string.save_template_title),
        message = if (isTemplate) stringResource(R.string.remove_template_message) else stringResource(R.string.save_template_message),
        confirmButtonText = stringResource(R.string.confirm),
        onDismiss = { showTemplateDialog = false },
        onConfirm = {
            isTemplate = !isTemplate
            scope.launch {
                delay(100)
                saveNote()
            }
            showTemplateDialog = false
            toastMessage = if (isTemplate) stringResource(R.string.template_saved_toast) else stringResource(R.string.template_removed_toast)
        }
    )

    ConfirmationDialog(
        showDialog = showArchiveDialog,
        title = stringResource(R.string.archive_title),
        message = stringResource(R.string.archive_message),
        confirmButtonText = stringResource(R.string.confirm),
        onDismiss = { showArchiveDialog = false },
        onConfirm = {
            existingNote?.let { noteViewModel.archiveNote(it) }
            showArchiveDialog = false
            navController.popBackStack()
            toastMessage = stringResource(R.string.archived_toast)
        }
    )

    ConfirmationDialog(
        showDialog = showDeleteDialog,
        title = stringResource(R.string.delete_title),
        message = stringResource(R.string.delete_message),
        confirmButtonText = stringResource(R.string.delete_button),
        isDestructive = true,
        onDismiss = { showDeleteDialog = false },
        onConfirm = {
            existingNote?.let { noteViewModel.moveToTrash(it) }
            showDeleteDialog = false
            navController.popBackStack()
            toastMessage = stringResource(R.string.deleted_toast)
        }
    )

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

    if (showColorPicker) {
        AlertDialog(
            onDismissRequest = { showColorPicker = false },
            title = { Text(stringResource(R.string.choose_color_title)) },
            text = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 8.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
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
            },
            confirmButton = {
                TextButton(onClick = { showColorPicker = false }) { Text(stringResource(R.string.close)) }
            }
        )
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    if (isSaving) {
                        Text(stringResource(R.string.saving), style = MaterialTheme.typography.bodySmall, color = iconColor.copy(alpha = 0.6f))
                    } else if (isRecording) {
                        Text(stringResource(R.string.recording), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
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
                        scope.launch {
                            delay(100)
                            saveNote()
                        }
                    }) {
                        Icon(if (isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin, null, tint = iconColor)
                    }
                    IconButton(onClick = { showColorPicker = true }) {
                        Icon(Icons.Outlined.Circle, stringResource(R.string.choose_color), tint = iconColor)
                    }
                    IconButton(onClick = { showMoreMenu = true }) {
                        Icon(Icons.Default.MoreVert, null, tint = iconColor)
                    }
                    DropdownMenu(
                        expanded = showMoreMenu,
                        onDismissRequest = { showMoreMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.share)) },
                            onClick = {
                                shareNote()
                                showMoreMenu = false
                            },
                            leadingIcon = { Icon(Icons.Outlined.Share, null) }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.add_images)) },
                            onClick = {
                                imagePickerLauncher.launch("image/*")
                                showMoreMenu = false
                            },
                            leadingIcon = { Icon(Icons.Outlined.Image, null) }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.add_labels)) },
                            onClick = {
                                showLabelDialog = true
                                showMoreMenu = false
                            },
                            leadingIcon = { Icon(Icons.Outlined.Label, null) }
                        )
                        DropdownMenuItem(
                            text = { Text(if (isRecording) stringResource(R.string.stop_recording) else stringResource(R.string.record_audio)) },
                            onClick = {
                                if (isRecording) {
                                    AudioHelper.stopRecording()
                                    currentRecordingPath?.let {
                                        audioFiles = audioFiles + it
                                        saveNote()
                                    }
                                    isRecording = false
                                    currentRecordingPath = null
                                } else {
                                    val permission = Manifest.permission.RECORD_AUDIO
                                    if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                                        val path = AudioHelper.startRecording(context)
                                        if (path != null) {
                                            currentRecordingPath = path
                                            isRecording = true
                                        }
                                    } else {
                                        permissionLauncher.launch(permission)
                                    }
                                }
                                showMoreMenu = false
                            },
                            leadingIcon = { Icon(if (isRecording) Icons.Filled.Stop else Icons.Outlined.Mic, null) }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.save_as_pdf)) },
                            onClick = {
                                saveToPdf()
                                showMoreMenu = false
                            },
                            leadingIcon = { Icon(Icons.Outlined.PictureAsPdf, null) }
                        )
                        DropdownMenuItem(
                            text = { Text(if (isTemplate) stringResource(R.string.remove_from_template) else stringResource(R.string.save_as_template)) },
                            onClick = {
                                showTemplateDialog = true
                                showMoreMenu = false
                            },
                            leadingIcon = { Icon(Icons.Outlined.Description, null) }
                        )
                        if (existingNote != null) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.archive)) },
                                onClick = {
                                    showArchiveDialog = true
                                    showMoreMenu = false
                                },
                                leadingIcon = { Icon(Icons.Outlined.Archive, null) }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.delete)) },
                                onClick = {
                                    showDeleteDialog = true
                                    showMoreMenu = false
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
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 3.dp
            ) {
                MarkdownToolbar(
                    onActionClick = { action -> insertMarkdown(action) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .imePadding()
                .padding(horizontal = 16.dp)
        ) {
            if (selectedLabels.isNotEmpty()) {
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
                                stringResource(R.string.title_hint),
                                style = MaterialTheme.typography.displaySmall,
                                color = textColor.copy(alpha = 0.4f)
                            )
                        }
                        innerTextField()
                    }
                }
            )

            imageUris.forEach { imagePath ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
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
                            Icon(Icons.Outlined.BrokenImage, stringResource(R.string.image_not_loaded))
                        }
                    }
                    
                    IconButton(
                        onClick = {
                            ImageHelper.deleteImage(imagePath)
                            imageUris = imageUris.filter { it != imagePath }
                            saveNote()
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

            audioFiles.forEachIndexed { index, audioPath ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.AudioFile, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Audio ${index + 1}", style = MaterialTheme.typography.bodyMedium)
                        }
                        Row {
                            IconButton(onClick = {
                                if (playingAudioIndex == index && AudioHelper.isPlaying()) {
                                    AudioHelper.stopAudio()
                                    playingAudioIndex = null
                                } else {
                                    AudioHelper.playAudio(audioPath) {
                                        playingAudioIndex = null
                                    }
                                    playingAudioIndex = index
                                }
                            }) {
                                Icon(
                                    if (playingAudioIndex == index && AudioHelper.isPlaying()) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                                    stringResource(R.string.play_audio)
                                )
                            }
                            IconButton(onClick = {
                                AudioHelper.deleteAudio(audioPath)
                                audioFiles = audioFiles.filter { it != audioPath }
                                saveNote()
                            }) {
                                Icon(Icons.Outlined.Delete, stringResource(R.string.delete_audio))
                            }
                        }
                    }
                }
            }

            BasicTextField(
                value = contentTextField,
                onValueChange = { contentTextField = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 400.dp),
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = textColor),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                decorationBox = { innerTextField ->
                    Box {
                        if (contentTextField.text.isEmpty()) {
                            Text(
                                stringResource(R.string.content_hint),
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

@Composable
fun MarkdownToolbar(
    onActionClick: (MarkdownAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    
    val markdownActions = listOf(
        MarkdownAction(Icons.Outlined.FormatBold, "Bold", "**", "**"),
        MarkdownAction(Icons.Outlined.FormatItalic, "Italic", "_", "_"),
        MarkdownAction(Icons.Outlined.FormatStrikethrough, "Strikethrough", "~~", "~~"),
        MarkdownAction(Icons.Outlined.Title, "Heading", "# "),
        MarkdownAction(Icons.Outlined.Code, "Code", "`", "`"),
        MarkdownAction(Icons.Outlined.FormatQuote, "Quote", "> "),
        MarkdownAction(Icons.Outlined.FormatListBulleted, "Bullet", "- "),
        MarkdownAction(Icons.Outlined.FormatListNumbered, "Number", "1. "),
        MarkdownAction(Icons.Outlined.CheckBox, "Checkbox", "- [ ] "),
        MarkdownAction(Icons.Outlined.Link, "Link", "[", "](url)"),
        MarkdownAction(Icons.Outlined.HorizontalRule, "Divider", "\n---\n")
    )
    
    Row(
        modifier = modifier
            .horizontalScroll(scrollState)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        markdownActions.forEach { action ->
            FilledTonalIconButton(
                onClick = { onActionClick(action) },
                modifier = Modifier.size(42.dp)
            ) {
                Icon(
                    imageVector = action.icon,
                    contentDescription = action.label,
                    modifier = Modifier.size(20.dp)
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
        title = { Text(stringResource(R.string.manage_labels)) },
        text = {
            Column {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = newLabelName,
                        onValueChange = { newLabelName = it },
                        label = { Text(stringResource(R.string.new_label)) },
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
                        Icon(Icons.Default.Add, stringResource(R.string.add))
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
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.done)) }
        }
    )
}
