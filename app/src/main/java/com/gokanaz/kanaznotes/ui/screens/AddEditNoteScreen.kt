package com.gokanaz.kanaznotes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddEditNoteScreen(
    onSave: (String) -> Unit,
    initialText: String = ""
) {
    var text by remember { mutableStateOf(initialText) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { onSave(text) }) {
                Icon(imageVector = Icons.Filled.Save, contentDescription = "Save")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Note") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
