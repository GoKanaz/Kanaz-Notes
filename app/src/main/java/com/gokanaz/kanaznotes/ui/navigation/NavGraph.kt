package com.gokanaz.kanaznotes.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.gokanaz.kanaznotes.ui.screens.*
import com.gokanaz.kanaznotes.ui.viewmodel.NoteViewModel
import com.gokanaz.kanaznotes.ui.viewmodel.SettingsViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    noteViewModel: NoteViewModel,
    settingsViewModel: SettingsViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                noteViewModel = noteViewModel,
                settingsViewModel = settingsViewModel,
                onNavigateToAddNote = { navController.navigate("create_note") },
                onNavigateToEditNote = { noteId -> navController.navigate("edit_note/$noteId") },
                onNavigateToSettings = { navController.navigate("settings") },
                onNavigateToSearch = { navController.navigate("search") }
            )
        }

        composable("settings") {
            SettingsScreen(
                navController = navController,
                settingsViewModel = settingsViewModel
            )
        }

        composable("style_settings") { 
            StyleSettingsScreen(navController = navController, settingsViewModel = settingsViewModel) 
        }

        composable("list_settings") { 
            ListSettingsScreen(navController = navController, settingsViewModel = settingsViewModel) 
        }

        composable("editor_settings") { 
            EditorSettingsScreen(
                settingsViewModel = settingsViewModel, 
                onBack = { navController.popBackStack() }
            ) 
        }

        composable("language_settings") { 
            LanguageSettingsScreen(navController = navController, settingsViewModel = settingsViewModel) 
        }

        composable("template_settings") { 
            TemplateSettingsScreen(navController = navController, settingsViewModel = settingsViewModel) 
        }

        composable("security_settings") { 
            SecuritySettingsScreen(navController = navController, settingsViewModel = settingsViewModel) 
        }

        composable("data_settings") { 
            DataSettingsScreen(
                noteViewModel = noteViewModel, 
                settingsViewModel = settingsViewModel, 
                onBack = { navController.popBackStack() }
            ) 
        }

        composable("cloud_settings") { 
            CloudSettingsScreen(navController = navController, settingsViewModel = settingsViewModel) 
        }

        // PERBAIKAN DI SINI: Menggunakan AddEditNoteScreen untuk membuat catatan baru
        composable("create_note") { 
            AddEditNoteScreen(
                noteId = null,
                onBack = { navController.popBackStack() },
                onSave = { note -> 
                    noteViewModel.saveNote(note) 
                }
            )
        }

        // PERBAIKAN DI SINI: Sinkronisasi parameter dengan AddEditNoteScreen.kt
        composable("edit_note/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")?.toIntOrNull()
            AddEditNoteScreen(
                noteId = noteId,
                onBack = { navController.popBackStack() },
                onSave = { note -> 
                    noteViewModel.saveNote(note) 
                }
            )
        }

        composable("search") { 
            SearchScreen(noteViewModel = noteViewModel, onBack = { navController.popBackStack() }) 
        }
    }
}
