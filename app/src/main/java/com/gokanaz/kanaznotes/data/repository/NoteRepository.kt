package com.gokanaz.kanaznotes.data.repository

import com.gokanaz.kanaznotes.data.local.NoteDao
import com.gokanaz.kanaznotes.data.local.NoteEntity
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {

    suspend fun insertNote(note: NoteEntity) {
        noteDao.insertNote(note)
    }

    suspend fun updateNote(note: NoteEntity) {
        noteDao.updateNote(note)
    }

    suspend fun deleteNote(note: NoteEntity) {
        noteDao.deleteNote(note)
    }

    fun getAllNotes(): Flow<List<NoteEntity>> = noteDao.getAllNotes()

    fun getTemplates(): Flow<List<NoteEntity>> = noteDao.getTemplates()

    val allNotes: Flow<List<NoteEntity>> = getAllNotes()
    val templates: Flow<List<NoteEntity>> = getTemplates()
}
