package com.gokanaz.kanaznotes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.gokanaz.kanaznotes.ui.components.MarkdownAction
import com.gokanaz.kanaznotes.ui.components.MarkdownEditor
import com.gokanaz.kanaznotes.ui.components.MarkdownToolbar
import com.gokanaz.kanaznotes.ui.components.insertMarkdown
import com.gokanaz.kanaznotes.ui.components.handleEnterKey
import com.gokanaz.kanaznotes.ui.viewmodel.NoteViewModel
import com.gokanaz.kanaznotes.data.local.NoteEntity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkdownNoteScreen(
    noteViewModel: NoteViewModel,
    navController: NavHostController,
    noteId: Int? = null
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    
    var textValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    var isPreviewMode by rememberSaveable { mutableStateOf(false) }
    var title by rememberSaveable { mutableStateOf("") }
    var color by rememberSaveable { mutableStateOf(0) }
    var isPinned by rememberSaveable { mutableStateOf(false) }
    var existingNote by remember { mutableStateOf<NoteEntity?>(null) }
    
    LaunchedEffect(noteId) {
        if (noteId != null) {
            scope.launch {
                val note = noteViewModel.getNoteById(noteId)
                if (note != null) {
                    existingNote = note
                    title = note.title
                    textValue = TextFieldValue(note.content)
                    color = note.color
                    isPinned = note.isPinned
                }
            }
        }
    }
    
    fun saveNote() {
        scope.launch {
            val note = if (existingNote != null) {
                existingNote!!.copy(
                    title = title,
                    content = textValue.text,
                    color = color,
                    isPinned = isPinned,
                    isMarkdown = true,
                    timestamp = System.currentTimeMillis()
                )
            } else {
                NoteEntity(
                    title = title,
                    content = textValue.text,
                    color = color,
                    isPinned = isPinned,
                    isMarkdown = true,
                    timestamp = System.currentTimeMillis()
                )
            }
            
            if (existingNote != null) {
                noteViewModel.updateNote(note)
            } else {
                noteViewModel.insertNote(note)
                existingNote = note.copy(id = note.id)
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (title.isNotBlank()) {
                        Text(title)
                    } else {
                        Text("Markdown Note")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { 
                        saveNote()
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        isPinned = !isPinned
                        saveNote()
                    }) {
                        Icon(
                            if (isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                            "Pin"
                        )
                    }
                    IconButton(
                        onClick = { 
                            isPreviewMode = !isPreviewMode
                        }
                    ) {
                        Icon(
                            imageVector = if (isPreviewMode) Icons.Default.Edit else Icons.Default.Visibility,
                            contentDescription = if (isPreviewMode) "Edit Mode" else "Preview Mode"
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (!isPreviewMode) {
                MarkdownToolbar(
                    onActionClick = { action ->
                        textValue = insertMarkdown(textValue, action)
                        saveNote()
                    }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
        ) {
            if (!isPreviewMode) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { 
                        title = it
                        saveNote()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Title") },
                    textStyle = MaterialTheme.typography.headlineSmall,
                    singleLine = true
                )
            }
            
            MarkdownEditor(
                value = textValue,
                onValueChange = { newValue ->
                    if (newValue.text.length > textValue.text.length &&
                        newValue.text.lastOrNull() == '\n'
                    ) {
                        textValue = handleEnterKey(textValue)
                    } else {
                        textValue = newValue
                    }
                    saveNote()
                },
                isPreviewMode = isPreviewMode,
                modifier = Modifier.weight(1f),
                placeholder = "Start writing with Markdown..."
            )
        }
    }
}
