package com.gokanaz.kanaznotes.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.gokanaz.kanaznotes.data.local.NoteEntity
import com.gokanaz.kanaznotes.ui.viewmodel.NoteViewModel

@Composable
fun CreateNoteScreen(noteViewModel: NoteViewModel) {
    Column {
        Text(text = "Create Note")
        val note = NoteEntity(id = 0, title = "New Note", content = "Content")
        noteViewModel.insertNote(note)
    }
}
