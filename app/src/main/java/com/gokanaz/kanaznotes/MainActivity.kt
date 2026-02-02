package com.gokanaz.kanaznotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.gokanaz.kanaznotes.data.local.AppDatabase
import com.gokanaz.kanaznotes.data.repository.NoteRepository
import com.gokanaz.kanaznotes.ui.navigation.NavGraph
import com.gokanaz.kanaznotes.ui.theme.KanazNotesTheme
import com.gokanaz.kanaznotes.ui.viewmodel.NoteViewModel
import com.gokanaz.kanaznotes.ui.viewmodel.NoteViewModelFactory
import com.gokanaz.kanaznotes.ui.viewmodel.SettingsViewModel
import com.tencent.mmkv.MMKV
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        MMKV.initialize(this)
        val mmkv = MMKV.defaultMMKV()
        val savedLanguage = mmkv?.decodeString("language") ?: "en"
        
        val locale = when(savedLanguage) {
            "id" -> Locale("id", "ID")
            else -> Locale("en", "US")
        }
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = NoteRepository(database.noteDao())
        val noteViewModel = ViewModelProvider(
            this,
            NoteViewModelFactory(repository)
        )[NoteViewModel::class.java]
        
        val settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        
        setContent {
            KanazNotesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavGraph(
                        navController = navController,
                        noteViewModel = noteViewModel,
                        settingsViewModel = settingsViewModel
                    )
                }
            }
        }
    }
}
