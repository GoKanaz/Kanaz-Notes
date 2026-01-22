package com.gokanaz.kanaznotes.data.repository

import com.gokanaz.kanaznotes.data.local.NoteDao
import com.gokanaz.kanaznotes.data.local.NoteEntity
import com.gokanaz.kanaznotes.data.local.FolderEntity
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {
    private val mmkv = MMKV.defaultMMKV()

    val allNotes: Flow<List<NoteEntity>> = noteDao.getAllNotes()
    val allFolders: Flow<List<FolderEntity>> = noteDao.getAllFolders()
    val templates: Flow<List<NoteEntity>> = noteDao.getTemplates()

    suspend fun insertNote(note: NoteEntity) = noteDao.insertNote(note)
    suspend fun updateNote(note: NoteEntity) = noteDao.updateNote(note)
    suspend fun insertFolder(folder: FolderEntity) = noteDao.insertFolder(folder)
}
