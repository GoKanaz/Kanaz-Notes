package com.gokanaz.kanaznotes.data.repository

import com.gokanaz.kanaznotes.data.local.LabelEntity
import com.gokanaz.kanaznotes.data.local.NoteDao
import com.gokanaz.kanaznotes.data.local.NoteEntity
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {

    fun getAllNotes(): Flow<List<NoteEntity>> = noteDao.getAllNotes()

    fun getTemplates(): Flow<List<NoteEntity>> = noteDao.getTemplates()

    fun getArchivedNotes(): Flow<List<NoteEntity>> = noteDao.getArchivedNotes()

    fun getTrashedNotes(): Flow<List<NoteEntity>> = noteDao.getTrashedNotes()

    suspend fun getNoteById(id: Int): NoteEntity? = noteDao.getNoteById(id)

    fun getNotesByLabel(label: String): Flow<List<NoteEntity>> = noteDao.getNotesByLabel(label)

    suspend fun insertNote(note: NoteEntity) = noteDao.insertNote(note)

    suspend fun updateNote(note: NoteEntity) = noteDao.updateNote(note)

    suspend fun deleteNote(note: NoteEntity) = noteDao.deleteNote(note)

    fun getAllLabels(): Flow<List<LabelEntity>> = noteDao.getAllLabels()

    suspend fun insertLabel(label: LabelEntity) = noteDao.insertLabel(label)

    suspend fun updateLabel(label: LabelEntity) = noteDao.updateLabel(label)

    suspend fun deleteLabel(label: LabelEntity) = noteDao.deleteLabel(label)
}
