package com.gokanaz.kanaznotes.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.gokanaz.kanaznotes.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSettingsScreen(
    navController: NavHostController,
    settingsViewModel: SettingsViewModel
) {
    val language by settingsViewModel.language.collectAsState()

    val languages = listOf(
        "en" to "English",
        "id" to "Bahasa Indonesia"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bahasa") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Card {
                    Column {
                        languages.forEachIndexed { index, (code, name) ->
                            ListItem(
                                headlineContent = { Text(name) },
                                supportingContent = { Text(code.uppercase()) },
                                trailingContent = {
                                    RadioButton(
                                        selected = language == code,
                                        onClick = { settingsViewModel.setLanguage(code) }
                                    )
                                },
                                modifier = Modifier.clickable { settingsViewModel.setLanguage(code) }
                            )
                            if (index < languages.size - 1) {
                                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
