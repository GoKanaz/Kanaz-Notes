package com.gokanaz.kanaznotes.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.gokanaz.kanaznotes.ui.screens.*
import com.gokanaz.kanaznotes.ui.viewmodel.NoteViewModel
import com.gokanaz.kanaznotes.ui.viewmodel.SettingsViewModel
import com.tencent.mmkv.MMKV

@Composable
fun NavGraph(
    navController: NavHostController,
    noteViewModel: NoteViewModel,
    settingsViewModel: SettingsViewModel
) {
    val kv = MMKV.defaultMMKV()
    var isLocked by remember { mutableStateOf(kv.decodeBool("isPasswordEnabled", false)) }
    
    LaunchedEffect(Unit) {
        isLocked = kv.decodeBool("isPasswordEnabled", false)
    }
    
    if (isLocked) {
        LockScreen(onUnlock = { isLocked = false })
    } else {
        NavHost(navController = navController, startDestination = "home") {
            composable("home") {
                HomeScreen(
                    noteViewModel = noteViewModel,
                    settingsViewModel = settingsViewModel,
                    navController = navController
                )
            }
            composable("add_note") {
                AddEditNoteScreen(
                    noteViewModel = noteViewModel,
                    navController = navController,
                    existingNoteId = null
                )
            }
            composable("edit_note/{noteId}") { backStackEntry ->
                val noteId = backStackEntry.arguments?.getString("noteId")?.toIntOrNull()
                AddEditNoteScreen(
                    noteViewModel = noteViewModel,
                    navController = navController,
                    existingNoteId = noteId
                )
            }
            composable("search") {
                SearchScreen(
                    noteViewModel = noteViewModel,
                    navController = navController
                )
            }
            composable("settings") {
                SettingsScreen(
                    navController = navController,
                    settingsViewModel = settingsViewModel
                )
            }
            composable("style_settings") {
                StyleSettingsScreen(
                    navController = navController,
                    settingsViewModel = settingsViewModel
                )
            }
            composable("language_settings") {
                LanguageSettingsScreen(
                    navController = navController,
                    settingsViewModel = settingsViewModel
                )
            }
            composable("security_settings") {
                SecuritySettingsScreen(
                    navController = navController,
                    settingsViewModel = settingsViewModel
                )
            }
            composable("cloud_settings") {
                CloudSettingsScreen(
                    navController = navController,
                    settingsViewModel = settingsViewModel
                )
            }
            composable("templates") {
                TemplateScreen(
                    noteViewModel = noteViewModel,
                    navController = navController
                )
            }
            composable("archive") {
                ArchiveScreen(
                    noteViewModel = noteViewModel,
                    navController = navController
                )
            }
        }
    }
}
