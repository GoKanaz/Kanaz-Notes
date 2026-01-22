package com.gokanaz.kanaznotes.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.gokanaz.kanaznotes.data.local.NoteEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel : ViewModel() {
    private val mmkv = MMKV.defaultMMKV()
    private val gson = Gson()

    val colorPalettes = listOf(
        listOf(Color(0xFFFF80AB), Color(0xFFF50057), Color(0xFFFF4081), Color(0xFF880E4F)),
        listOf(Color(0xFF8C9EFF), Color(0xFF3D5AFE), Color(0xFF536DFE), Color(0xFF1A237E)),
        listOf(Color(0xFFB9F6CA), Color(0xFF00E676), Color(0xFF69F0AE), Color(0xFF1B5E20)),
        listOf(Color(0xFFFFD180), Color(0xFFFFAB00), Color(0xFFFFAB40), Color(0xFFE65100))
    )

    private val _darkMode = MutableStateFlow(mmkv.decodeString("dark_mode", "system") ?: "system")
    val darkMode = _darkMode.asStateFlow()

    private val _isAmoledMode = MutableStateFlow(mmkv.decodeBool("amoled_mode", false))
    val isAmoledMode = _isAmoledMode.asStateFlow()

    private val _isDynamicColor = MutableStateFlow(mmkv.decodeBool("dynamic_color", true))
    val isDynamicColor = _isDynamicColor.asStateFlow()

    private val _selectedColorPalette = MutableStateFlow(mmkv.decodeInt("color_palette", 0))
    val selectedColorPalette = _selectedColorPalette.asStateFlow()

    private val _isPasswordEnabled = MutableStateFlow(mmkv.decodeBool("password_enabled", false))
    val isPasswordEnabled = _isPasswordEnabled.asStateFlow()

    private val _isScreenProtectionEnabled = MutableStateFlow(mmkv.decodeBool("screen_protection", false))
    val isScreenProtectionEnabled = _isScreenProtectionEnabled.asStateFlow()

    private val _textSize = MutableStateFlow(mmkv.decodeFloat("text_size", 16f))
    val textSize = _textSize.asStateFlow()

    private val _dateFormat = MutableStateFlow(mmkv.decodeString("date_format", "dd/MM/yyyy") ?: "dd/MM/yyyy")
    val dateFormat = _dateFormat.asStateFlow()

    private val _timeFormat = MutableStateFlow(mmkv.decodeString("time_format", "HH:mm") ?: "HH:mm")
    val timeFormat = _timeFormat.asStateFlow()

    private val _isSingleColumn = MutableStateFlow(mmkv.decodeBool("is_single_column", true))
    val isSingleColumn = _isSingleColumn.asStateFlow()

    private val _maxLines = MutableStateFlow(mmkv.decodeInt("max_lines", 3))
    val maxLines = _maxLines.asStateFlow()

    private val _cardSize = MutableStateFlow(mmkv.decodeString("card_size", "medium") ?: "medium")
    val cardSize = _cardSize.asStateFlow()

    private val _textOverflow = MutableStateFlow(mmkv.decodeString("text_overflow", "ellipsis") ?: "ellipsis")
    val textOverflow = _textOverflow.asStateFlow()

    private val _language = MutableStateFlow(mmkv.decodeString("language", "en") ?: "en")
    val language = _language.asStateFlow()

    private val _webdavUrl = MutableStateFlow(mmkv.decodeString("webdav_url", "") ?: "")
    val webdavUrl = _webdavUrl.asStateFlow()

    private val _webdavUsername = MutableStateFlow(mmkv.decodeString("webdav_username", "") ?: "")
    val webdavUsername = _webdavUsername.asStateFlow()

    private val _isSyncEnabled = MutableStateFlow(mmkv.decodeBool("sync_enabled", false))
    val isSyncEnabled = _isSyncEnabled.asStateFlow()

    fun setDarkMode(mode: String) { mmkv.encode("dark_mode", mode); _darkMode.value = mode }
    fun setAmoledMode(enabled: Boolean) { mmkv.encode("amoled_mode", enabled); _isAmoledMode.value = enabled }
    fun setDynamicColor(enabled: Boolean) { mmkv.encode("dynamic_color", enabled); _isDynamicColor.value = enabled }
    fun setColorPalette(index: Int) { mmkv.encode("color_palette", index); _selectedColorPalette.value = index }
    fun setScreenProtection(enabled: Boolean) { mmkv.encode("screen_protection", enabled); _isScreenProtectionEnabled.value = enabled }
    fun setTextSize(size: Float) { mmkv.encode("text_size", size); _textSize.value = size }
    fun setDateFormat(format: String) { mmkv.encode("date_format", format); _dateFormat.value = format }
    fun setTimeFormat(format: String) { mmkv.encode("time_format", format); _timeFormat.value = format }
    fun setSingleColumn(enabled: Boolean) { mmkv.encode("is_single_column", enabled); _isSingleColumn.value = enabled }
    fun setMaxLines(lines: Int) { mmkv.encode("max_lines", lines); _maxLines.value = lines }
    fun setCardSize(size: String) { mmkv.encode("card_size", size); _cardSize.value = size }
    fun setTextOverflow(overflow: String) { mmkv.encode("text_overflow", overflow); _textOverflow.value = overflow }
    fun setLanguage(lang: String) { mmkv.encode("language", lang); _language.value = lang }
    fun setSyncEnabled(enabled: Boolean) { mmkv.encode("sync_enabled", enabled); _isSyncEnabled.value = enabled }

    fun setPassword(enabled: Boolean, hash: String = "") {
        mmkv.encode("password_enabled", enabled)
        if (hash.isNotEmpty()) mmkv.encode("password_hash", hash)
        _isPasswordEnabled.value = enabled
    }

    fun setWebDAVConfig(url: String, username: String, pass: String) {
        mmkv.encode("webdav_url", url)
        mmkv.encode("webdav_username", username)
        mmkv.encode("webdav_password", pass)
        _webdavUrl.value = url
        _webdavUsername.value = username
    }

    fun exportToJson(notes: Any?): String = gson.toJson(notes)

    fun importFromJson(json: String): List<NoteEntity>? {
        return try {
            val type = object : TypeToken<List<NoteEntity>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            null
        }
    }
}
