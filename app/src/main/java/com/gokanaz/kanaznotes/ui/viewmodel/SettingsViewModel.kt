package com.gokanaz.kanaznotes.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.tencent.mmkv.MMKV

class SettingsViewModel : ViewModel() {
    private val kv: MMKV = MMKV.defaultMMKV()

    fun saveString(key: String, value: String) {
        kv.encode(key, value)
    }

    fun getString(key: String, default: String = ""): String {
        return kv.decodeString(key, default) ?: default
    }

    fun saveBoolean(key: String, value: Boolean) {
        kv.encode(key, value)
    }

    fun getBoolean(key: String, default: Boolean = false): Boolean {
        return kv.decodeBool(key, default)
    }

    fun saveInt(key: String, value: Int) {
        kv.encode(key, value)
    }

    fun getInt(key: String, default: Int = 0): Int {
        return kv.decodeInt(key, default)
    }
}
