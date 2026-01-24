package com.gokanaz.kanaznotes.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.gokanaz.kanaznotes.ui.viewmodel.NoteViewModel
import com.gokanaz.kanaznotes.ui.viewmodel.SettingsViewModel
import com.gokanaz.kanaznotes.data.local.NoteEntity

@Composable
fun NavGraph(
    navController: NavHostController,
    noteViewModel: NoteViewModel,
    settingsViewModel: SettingsViewModel,
    onNavigateToAddNote: () -> Unit,
    onNavigateToEditNote: (NoteEntity) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onLightbulb: () -> Unit,
    onArchive: () -> Unit,
    onHelp: () -> Unit,
    onAgenda: () -> Unit,
    onStyle: () -> Unit,
    onLanguage: () -> Unit,
    onCalendar: () -> Unit,
    onSecurity: () -> Unit,
    onCloudUpload: () -> Unit,
    onCloud: () -> Unit,
    onBack: () -> Unit,
    onDownload: () -> Unit,
    onUpload: () -> Unit
) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            // isi screen home
        }
        composable("settings") {
            // isi screen settings
        }
    }
}
