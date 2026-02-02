package com.gokanaz.kanaznotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.gokanaz.kanaznotes.data.local.NoteDatabase
import com.gokanaz.kanaznotes.data.repository.NoteRepository
import com.gokanaz.kanaznotes.ui.navigation.NavGraph
import com.gokanaz.kanaznotes.ui.theme.KanazNotesTheme
import com.gokanaz.kanaznotes.ui.viewmodel.NoteViewModel
import com.gokanaz.kanaznotes.ui.viewmodel.NoteViewModelFactory
import com.gokanaz.kanaznotes.ui.viewmodel.SettingsViewModel
import com.tencent.mmkv.MMKV
import android.view.WindowManager

class MainActivity : ComponentActivity() {
    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MMKV.initialize(this)

        val database = NoteDatabase.getDatabase(this)
        val repository = NoteRepository(database.noteDao())
        val factory = NoteViewModelFactory(repository)

        setContent {
            settingsViewModel = viewModel()
            val noteViewModel: NoteViewModel = viewModel(factory = factory)
            val navController = rememberNavController()

            KanazNotesTheme(settingsViewModel = settingsViewModel) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph(
                        navController = navController,
                        noteViewModel = noteViewModel,
                        settingsViewModel = settingsViewModel
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::settingsViewModel.isInitialized) {
            val kv = MMKV.defaultMMKV()
            val isScreenProtectionEnabled = kv.decodeBool("isScreenProtectionEnabled", false)
            
            if (isScreenProtectionEnabled) {
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE
                )
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
            }
        }
    }
}
