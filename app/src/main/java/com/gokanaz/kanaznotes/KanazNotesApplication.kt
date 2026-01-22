package com.gokanaz.kanaznotes

import android.app.Application
import com.gokanaz.kanaznotes.data.local.NoteDatabase
import com.gokanaz.kanaznotes.data.repository.NoteRepository
import com.tencent.mmkv.MMKV

class KanazNotesApplication : Application() {
    val database by lazy { NoteDatabase.getDatabase(this) }
    val repository by lazy { NoteRepository(database.noteDao()) }

    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
    }
}
