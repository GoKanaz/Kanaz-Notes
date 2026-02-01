package com.gokanaz.kanaznotes.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    @Query("SELECT * FROM notes_table WHERE isDeleted = 0 AND isTemplate = 0 ORDER BY isPinned DESC, id DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes_table WHERE isTemplate = 1 ORDER BY id DESC")
    fun getTemplates(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes_table WHERE isDeleted = 1 ORDER BY id DESC")
    fun getArchivedNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes_table WHERE id = :id")
    suspend fun getNoteById(id: Int): NoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: FolderEntity)

    @Query("SELECT * FROM folders_table")
    fun getAllFolders(): Flow<List<FolderEntity>>
}
