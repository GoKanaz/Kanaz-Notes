package com.gokanaz.kanaznotes.data.repository

import com.tencent.mmkv.MMKV

class NoteRepository {
    private val kv: MMKV = MMKV.defaultMMKV()

    fun saveNote(id: String, content: String) {
        kv.encode(id, content)
    }

    fun getNote(id: String): String? {
        return kv.decodeString(id)
    }

    fun deleteNote(id: String) {
        kv.removeValueForKey(id)
    }

    fun getAllNotes(keys: List<String>): Map<String, String?> {
        return keys.associateWith { kv.decodeString(it) }
    }
}
