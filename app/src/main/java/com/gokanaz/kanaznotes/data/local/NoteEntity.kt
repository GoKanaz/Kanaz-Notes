package com.gokanaz.kanaznotes.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes_table")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String,
    val timestamp: Long,
    val folderId: Int? = null,
    val color: Int = 0,
    val isPinned: Boolean = false,
    val isDeleted: Boolean = false,
    val isTemplate: Boolean = false,
    val isTrashed: Boolean = false,
    val labels: String = "",
    val images: String = "",
    val audioFiles: String = ""
)
