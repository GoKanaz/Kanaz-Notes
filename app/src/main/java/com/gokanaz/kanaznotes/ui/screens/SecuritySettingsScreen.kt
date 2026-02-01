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
fun SecuritySettingsScreen(
    navController: NavHostController,
    settingsViewModel: SettingsViewModel
) {
    val isPasswordEnabled by settingsViewModel.isPasswordEnabled.collectAsState()
    val isScreenProtection by settingsViewModel.isScreenProtectionEnabled.collectAsState()
    var showPasswordDialog by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }

    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showPasswordDialog = false; errorText = "" },
            title = { Text("Atur Kata Sandi") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; errorText = "" },
                        label = { Text("Kata Sandi Baru") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it; errorText = "" },
                        label = { Text("Konfirmasi Kata Sandi") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        isError = errorText.isNotEmpty(),
                        supportingText = { if (errorText.isNotEmpty()) Text(errorText, color = MaterialTheme.colorScheme.error) }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    when {
                        password.isEmpty() -> errorText = "Kata sandi tidak boleh kosong"
                        password != confirmPassword -> errorText = "Kata sandi tidak cocok"
                        else -> {
                            settingsViewModel.setPassword(password)
                            showPasswordDialog = false
                            password = ""
                            confirmPassword = ""
                            errorText = ""
                        }
                    }
                }) { Text("Simpan") }
            },
            dismissButton = {
                TextButton(onClick = { showPasswordDialog = false; errorText = ""; password = ""; confirmPassword = "" }) { Text("Batal") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Keamanan") },
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
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Kata Sandi", style = MaterialTheme.typography.titleMedium)
                                Text(
                                    if (isPasswordEnabled) "Kata sandi telah diatur" else "Belum diatur",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (isPasswordEnabled) {
                                OutlinedButton(onClick = { showPasswordDialog = true }) { Text("Ubah") }
                            } else {
                                Button(onClick = { showPasswordDialog = true }) { Text("Atur") }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
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
                                Text("Perlindungan Layar", style = MaterialTheme.typography.titleMedium)
                                Text("Mencegah screenshot", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(checked = isScreenProtection, onCheckedChange = { settingsViewModel.setScreenProtection(it) })
                        }
                    }
                }
            }
        }
    }
}

