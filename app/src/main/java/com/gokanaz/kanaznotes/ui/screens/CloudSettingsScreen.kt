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
import androidx.navigation.NavHostController
import com.gokanaz.kanaznotes.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloudSettingsScreen(
    navController: NavHostController,
    settingsViewModel: SettingsViewModel
) {
    val webdavUrl by settingsViewModel.webdavUrl.collectAsState()
    val webdavUsername by settingsViewModel.webdavUsername.collectAsState()
    val isSyncEnabled by settingsViewModel.isSyncEnabled.collectAsState()

    var url by remember { mutableStateOf(webdavUrl) }
    var username by remember { mutableStateOf(webdavUsername) }
    var password by remember { mutableStateOf("") }
    var saved by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan Cloud") },
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
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Konfigurasi WebDAV", style = MaterialTheme.typography.titleMedium)
                        OutlinedTextField(
                            value = url,
                            onValueChange = { url = it; saved = false },
                            label = { Text("URL WebDAV") },
                            placeholder = { Text("https://example.com/webdav") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it; saved = false },
                            label = { Text("Nama Pengguna") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it; saved = false },
                            label = { Text("Kata Sandi") },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = {
                                settingsViewModel.setWebDAVConfig(url, username, password)
                                saved = true
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = url.isNotBlank() && username.isNotBlank()
                        ) {
                            Text(if (saved) "Tersimpan" else "Simpan Konfigurasi")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            item {
                Card {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Sinkronisasi Otomatis", style = MaterialTheme.typography.titleMedium)
                                Text("Sinkronkan catatan secara berkala", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(checked = isSyncEnabled, onCheckedChange = { settingsViewModel.setSyncEnabled(it) })
                        }
                    }
                }
            }
        }
    }
}

