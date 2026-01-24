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

    private val _dateFormat = MutableStateFlow(kv.decodeString("dateFormat", "dd/MM/yyyy") ?: "dd/MM/yyyy")
    val dateFormat: StateFlow<String> = _dateFormat
    fun setDateFormat(value: String) {
        _dateFormat.value = value
        kv.encode("dateFormat", value)
    }

    private val _textSize = MutableStateFlow(kv.decodeInt("textSize", 14))
    val textSize: StateFlow<Int> = _textSize
    fun setTextSize(value: Int) {
        _textSize.value = value
        kv.encode("textSize", value)
    }

    private val _language = MutableStateFlow(kv.decodeString("language", "en") ?: "en")
    val language: StateFlow<String> = _language
    fun setLanguage(value: String) {
        _language.value = value
        kv.encode("language", value)
    }

    private val _isSingleColumn = MutableStateFlow(kv.decodeBool("isSingleColumn", true))
    val isSingleColumn: StateFlow<Boolean> = _isSingleColumn
    fun setSingleColumn(value: Boolean) {
        _isSingleColumn.value = value
        kv.encode("isSingleColumn", value)
    }

    private val _maxLines = MutableStateFlow(kv.decodeInt("maxLines", 3))
    val maxLines: StateFlow<Int> = _maxLines
    fun setMaxLines(value: Int) {
        _maxLines.value = value
        kv.encode("maxLines", value)
    }

    private val _cardSize = MutableStateFlow(kv.decodeInt("cardSize", 1))
    val cardSize: StateFlow<Int> = _cardSize
    fun setCardSize(value: Int) {
        _cardSize.value = value
        kv.encode("cardSize", value)
    }

    private val _textOverflow = MutableStateFlow(kv.decodeBool("textOverflow", false))
    val textOverflow: StateFlow<Boolean> = _textOverflow
    fun setTextOverflow(value: Boolean) {
        _textOverflow.value = value
        kv.encode("textOverflow", value)
    }

    private val _isPasswordEnabled = MutableStateFlow(kv.decodeBool("isPasswordEnabled", false))
    val isPasswordEnabled: StateFlow<Boolean> = _isPasswordEnabled
    fun setPassword(value: String) {
        kv.encode("password", value)
        _isPasswordEnabled.value = true
    }

    private val _isScreenProtectionEnabled = MutableStateFlow(kv.decodeBool("isScreenProtectionEnabled", false))
    val isScreenProtectionEnabled: StateFlow<Boolean> = _isScreenProtectionEnabled
    fun setScreenProtection(value: Boolean) {
        _isScreenProtectionEnabled.value = value
        kv.encode("isScreenProtectionEnabled", value)
    }

    private val _webdavUrl = MutableStateFlow(kv.decodeString("webdavUrl", "") ?: "")
    val webdavUrl: StateFlow<String> = _webdavUrl

    private val _webdavUsername = MutableStateFlow(kv.decodeString("webdavUsername", "") ?: "")
    val webdavUsername: StateFlow<String> = _webdavUsername

    private val _isSyncEnabled = MutableStateFlow(kv.decodeBool("isSyncEnabled", false))
    val isSyncEnabled: StateFlow<Boolean> = _isSyncEnabled

    fun setWebDAVConfig(url: String, username: String) {
        _webdavUrl.value = url
        _webdavUsername.value = username
        kv.encode("webdavUrl", url)
        kv.encode("webdavUsername", username)
    }

    fun setSyncEnabled(value: Boolean) {
        _isSyncEnabled.value = value
        kv.encode("isSyncEnabled", value)
    }

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
}
