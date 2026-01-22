package com.gokanaz.kanaznotes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gokanaz.kanaznotes.data.local.NoteEntity
import com.gokanaz.kanaznotes.ui.viewmodel.NoteViewModel
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNoteScreen(
    noteViewModel: NoteViewModel,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catatan Baru") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            if (title.isNotEmpty() || content.isNotEmpty()) {
                                val note = NoteEntity(
                                    title = title,
                                    content = content,
                                    timestamp = Date().time,
                                    folderId = null,
                                    color = 0,
                                    isPinned = false,
                                    isDeleted = false,
                                    isTemplate = false
                                )
                                noteViewModel.saveNote(note)
                            }
                            onBack()
                        }
                    ) {
                        Text("Simpan")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Judul") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                singleLine = true
            )
            
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Isi") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp)
                    .heightIn(min = 300.dp),
                maxLines = Int.MAX_VALUE
            )
        }
    }
}
