package com.gokanaz.kanaznotes.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gokanaz.kanaznotes.data.local.NoteDatabase
import com.gokanaz.kanaznotes.data.local.NoteEntity
import com.gokanaz.kanaznotes.data.repository.NoteRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NoteRepository
    val notes: StateFlow<List<NoteEntity>>
    val templates: StateFlow<List<NoteEntity>>
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    init {
        val dao = NoteDatabase.getDatabase(application).noteDao()
        repository = NoteRepository(dao)
        notes = repository.allNotes.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        templates = repository.templates.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    val filteredNotes = combine(notes, _searchQuery) { notesList, query ->
        if (query.isEmpty()) notesList else notesList.filter {
            it.title.contains(query, ignoreCase = true) || it.content.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) { _searchQuery.value = query }
    fun saveNote(note: NoteEntity) = viewModelScope.launch { repository.insertNote(note) }
    fun saveAsTemplate(note: NoteEntity) = viewModelScope.launch { repository.insertNote(note.copy(id = 0, isTemplate = true)) }
}
