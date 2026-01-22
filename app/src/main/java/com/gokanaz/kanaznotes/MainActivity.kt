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
import com.gokanaz.kanaznotes.ui.theme.KanazNotesTheme
import com.gokanaz.kanaznotes.ui.viewmodel.SettingsViewModel
import com.gokanaz.kanaznotes.ui.viewmodel.NoteViewModel
import com.gokanaz.kanaznotes.ui.navigation.NavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()
            val noteViewModel: NoteViewModel = viewModel()
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
}
