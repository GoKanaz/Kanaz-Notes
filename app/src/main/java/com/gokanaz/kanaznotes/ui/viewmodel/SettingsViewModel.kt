package com.gokanaz.kanaznotes.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel : ViewModel() {
    private val mmkv = MMKV.defaultMMKV()
    
    private val _language = MutableStateFlow(mmkv?.decodeString("language") ?: "en")
    val language: StateFlow<String> = _language
    
    fun setLanguage(lang: String) {
        _language.value = lang
        mmkv?.encode("language", lang)
    }
}
