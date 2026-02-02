package com.gokanaz.kanaznotes.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gokanaz.kanaznotes.data.local.LabelEntity
import com.gokanaz.kanaznotes.data.local.NoteEntity
import com.gokanaz.kanaznotes.data.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class NoteViewModel(private val repository: NoteRepository) : ViewModel() {

    val allNotes: Flow<List<NoteEntity>> = repository.getAllNotes()
    val templates: Flow<List<NoteEntity>> = repository.getTemplates()
    val archivedNotes: Flow<List<NoteEntity>> = repository.getArchivedNotes()
    val allLabels: Flow<List<LabelEntity>> = repository.getAllLabels()

    fun insertNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.insertNote(note)
        }
    }

    fun updateNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.updateNote(note)
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    fun archiveNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.updateNote(note.copy(isDeleted = true))
        }
    }

    fun unarchiveNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.updateNote(note.copy(isDeleted = false))
        }
    }

    fun togglePin(note: NoteEntity) {
        viewModelScope.launch {
            repository.updateNote(note.copy(isPinned = !note.isPinned))
        }
    }

    suspend fun getNoteById(id: Int): NoteEntity? {
        return repository.getNoteById(id)
    }

    fun getNotesByLabel(label: String): Flow<List<NoteEntity>> {
        return repository.getNotesByLabel(label)
    }

    fun insertLabel(label: LabelEntity) {
        viewModelScope.launch {
            repository.insertLabel(label)
        }
    }

    fun deleteLabel(label: LabelEntity) {
        viewModelScope.launch {
            repository.deleteLabel(label)
        }
    }
}
