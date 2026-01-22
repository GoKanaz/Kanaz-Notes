package com.gokanaz.kanaznotes.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gokanaz.kanaznotes.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateSettingsScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel
) {
    val dateFormat by settingsViewModel.dateFormat.collectAsState()
    val timeFormat by settingsViewModel.timeFormat.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Templates") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
            item {
                Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Date Format", style = MaterialTheme.typography.titleMedium)
                        listOf("dd/MM/yyyy", "MM/dd/yyyy", "yyyy-MM-dd", "dd MMM yyyy").forEach { format ->
                            Row(modifier = Modifier.fillMaxWidth().clickable { settingsViewModel.setDateFormat(format) }.padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(selected = dateFormat == format, onClick = { settingsViewModel.setDateFormat(format) })
                                Text(format, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }
                }
            }
            item {
                Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Time Format", style = MaterialTheme.typography.titleMedium)
                        listOf("HH:mm", "hh:mm a", "hh:mm").forEach { format ->
                            Row(modifier = Modifier.fillMaxWidth().clickable { settingsViewModel.setTimeFormat(format) }.padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(selected = timeFormat == format, onClick = { settingsViewModel.setTimeFormat(format) })
                                Text(format, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
