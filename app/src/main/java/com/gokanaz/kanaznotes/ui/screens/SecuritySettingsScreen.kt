package com.gokanaz.kanaznotes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gokanaz.kanaznotes.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecuritySettingsScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel
) {
    val isPasswordEnabled by settingsViewModel.isPasswordEnabled.collectAsState()
    val isScreenProtectionEnabled by settingsViewModel.isScreenProtectionEnabled.collectAsState()
    var showPasswordDialog by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Security") },
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
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Password", style = MaterialTheme.typography.titleMedium)
                            Text("Protect app with password", style = MaterialTheme.typography.bodySmall)
                        }
                        Switch(
                            checked = isPasswordEnabled,
                            onCheckedChange = { enabled ->
                                if (enabled) showPasswordDialog = true 
                                else settingsViewModel.setPassword(false)
                            }
                        )
                    }
                }
            }
            item {
                Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Screen Protection", style = MaterialTheme.typography.titleMedium)
                            Text("Hide content in app switcher", style = MaterialTheme.typography.bodySmall)
                        }
                        Switch(
                            checked = isScreenProtectionEnabled,
                            onCheckedChange = { settingsViewModel.setScreenProtection(it) }
                        )
                    }
                }
            }
        }
    }
    
    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showPasswordDialog = false },
            title = { Text("Set Password") },
            text = {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (password.isNotEmpty()) {
                        settingsViewModel.setPassword(true, password)
                        showPasswordDialog = false
                    }
                }) { Text("Set") }
            },
            dismissButton = {
                TextButton(onClick = { showPasswordDialog = false }) { Text("Cancel") }
            }
        )
    }
}
