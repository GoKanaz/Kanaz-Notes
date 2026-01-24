package com.gokanaz.kanaznotes.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.gokanaz.kanaznotes.ui.viewmodel.NoteViewModel
import com.gokanaz.kanaznotes.data.local.NoteEntity

@Composable
fun SearchScreen(noteViewModel: NoteViewModel) {
    val notes = noteViewModel.allNotes.collectAsState(initial = emptyList())
    Column {
        notes.value.forEach { note: NoteEntity ->
            Text(text = note.title)
            Text(text = note.content)
        }
    }
}
