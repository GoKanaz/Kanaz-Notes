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
fun ListSettingsScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel
) {
    val cardSize by settingsViewModel.cardSize.collectAsState()
    val textOverflow by settingsViewModel.textOverflow.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("List Settings") },
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
                        Text("Card Size", style = MaterialTheme.typography.titleMedium)
                        listOf("small", "medium", "large").forEach { size ->
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable { settingsViewModel.setCardSize(size) }.padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = cardSize == size, onClick = { settingsViewModel.setCardSize(size) })
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(size.replaceFirstChar { it.uppercase() })
                            }
                        }
                    }
                }
            }
            item {
                Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Text Overflow", style = MaterialTheme.typography.titleMedium)
                        listOf("ellipsis", "clip", "visible").forEach { overflow ->
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable { settingsViewModel.setTextOverflow(overflow) }.padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = textOverflow == overflow, onClick = { settingsViewModel.setTextOverflow(overflow) })
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(overflow.replaceFirstChar { it.uppercase() })
                            }
                        }
                    }
                }
            }
        }
    }
}
