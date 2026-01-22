package com.gokanaz.kanaznotes.data.repository

import com.gokanaz.kanaznotes.data.local.NoteDao
import com.gokanaz.kanaznotes.data.local.NoteEntity
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {
    val allNotes: Flow<List<NoteEntity>> = noteDao.getAllNotes()
    val templates: Flow<List<NoteEntity>> = noteDao.getTemplates()

    suspend fun insertNote(note: NoteEntity) {
        noteDao.insertNote(note)
    }

    suspend fun deleteNote(note: NoteEntity) {
        noteDao.deleteNote(note)
    }
}
