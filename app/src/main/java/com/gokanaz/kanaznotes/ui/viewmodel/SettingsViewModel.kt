package com.gokanaz.kanaznotes.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.ui.graphics.Color
import com.tencent.mmkv.MMKV

class SettingsViewModel : ViewModel() {
    private val kv: MMKV = MMKV.defaultMMKV()

    private val _darkMode = MutableStateFlow(kv.decodeString("darkMode", "system") ?: "system")
    val darkMode: StateFlow<String> = _darkMode

    private val _isAmoledMode = MutableStateFlow(kv.decodeBool("isAmoledMode", false))
    val isAmoledMode: StateFlow<Boolean> = _isAmoledMode

    private val _isDynamicColor = MutableStateFlow(kv.decodeBool("isDynamicColor", false))
    val isDynamicColor: StateFlow<Boolean> = _isDynamicColor

    private val _selectedColorPalette = MutableStateFlow(kv.decodeInt("selectedColorPalette", 0))
    val selectedColorPalette: StateFlow<Int> = _selectedColorPalette

    val colorPalettes: List<List<Color>> = listOf(
        listOf(Color(0xFFF4A261), Color(0xFF4A90E2), Color(0xFF81C784)),
        listOf(Color(0xFF6200EE), Color(0xFF3700B3), Color(0xFF03DAC5)),
        listOf(Color(0xFFE57373), Color(0xFF64B5F6), Color(0xFF81C784)),
        listOf(Color(0xFFFFB74D), Color(0xFF4DB6AC), Color(0xFF7986CB))
    )

    fun setDarkMode(value: String) {
        _darkMode.value = value
        kv.encode("darkMode", value)
    }

    fun setAmoledMode(value: Boolean) {
        _isAmoledMode.value = value
        kv.encode("isAmoledMode", value)
    }

    fun setDynamicColor(value: Boolean) {
        _isDynamicColor.value = value
        kv.encode("isDynamicColor", value)
    }

    fun setSelectedColorPalette(value: Int) {
        _selectedColorPalette.value = value
        kv.encode("selectedColorPalette", value)
    }

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
