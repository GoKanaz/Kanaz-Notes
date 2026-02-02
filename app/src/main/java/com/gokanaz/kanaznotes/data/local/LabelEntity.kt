package com.gokanaz.kanaznotes.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "labels_table")
data class LabelEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val color: Int = 0
)
