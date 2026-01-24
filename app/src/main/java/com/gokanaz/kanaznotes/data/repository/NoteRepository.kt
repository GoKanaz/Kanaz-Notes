package com.gokanaz.kanaznotes.data.repository

import com.gokanaz.kanaznotes.data.dao.NoteDao
import com.gokanaz.kanaznotes.data.entity.NoteEntity

class NoteRepository(private val noteDao: NoteDao) {

    suspend fun insertNote(note: NoteEntity) {
        noteDao.insert(note)
    }

    suspend fun updateNote(note: NoteEntity) {
        noteDao.update(note)
    }

    suspend fun deleteNote(note: NoteEntity) {
        noteDao.delete(note)
    }

    fun getAllNotes() = noteDao.getAllNotes()

    fun getNoteById(id: Long) = noteDao.getNoteById(id)
}
