package com.gokanaz.kanaznotes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.gokanaz.kanaznotes.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkdownNoteScreen(
    navController: NavHostController,
    noteId: Int? = null
) {
    var textValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    var isPreviewMode by rememberSaveable { mutableStateOf(false) }
    var title by rememberSaveable { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (title.isNotBlank()) {
                        Text(title)
                    } else {
                        Text("New Note")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { isPreviewMode = !isPreviewMode }
                    ) {
                        Icon(
                            imageVector = if (isPreviewMode) Icons.Default.Edit else Icons.Default.Visibility,
                            contentDescription = if (isPreviewMode) "Edit Mode" else "Preview Mode"
                        )
                    }
                    IconButton(onClick = { /* Save note */ }) {
                        Icon(Icons.Default.Save, "Save")
                    }
                }
            )
        },
        bottomBar = {
            if (!isPreviewMode) {
                MarkdownToolbar(
                    onActionClick = { action ->
                        textValue = insertMarkdown(textValue, action)
                    }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (!isPreviewMode) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
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
                        newValue.text.last() == '\n'
                    ) {
                        textValue = handleEnterKey(textValue)
                    } else {
                        textValue = newValue
                    }
                },
                isPreviewMode = isPreviewMode,
                modifier = Modifier.weight(1f),
                placeholder = "Start writing with Markdown..."
            )
        }
    }
}
