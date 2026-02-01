package com.gokanaz.kanaznotes.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.autoMirrored.default.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.gokanaz.kanaznotes.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    settingsViewModel: SettingsViewModel
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, null)
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
                SectionTitle("Tampilan")
            }
            item {
                SettingsListItem(
                    icon = Icons.Outlined.Palette,
                    title = "Gaya & Tema",
                    subtitle = "Gelap, terang, dan warna",
                    onClick = { navController.navigate("style_settings") }
                )
            }
            item {
                Spacer(modifier = Modifier.height(12.dp))
                SectionTitle("Umum")
            }
            item {
                SettingsListItem(
                    icon = Icons.Outlined.Language,
                    title = "Bahasa",
                    subtitle = "Pengaturan bahasa aplikasi",
                    onClick = { navController.navigate("language_settings") }
                )
            }
            item {
                Spacer(modifier = Modifier.height(12.dp))
                SectionTitle("Keamanan")
            }
            item {
                SettingsListItem(
                    icon = Icons.Outlined.Lock,
                    title = "Keamanan",
                    subtitle = "Kata sandi dan perlindungan",
                    onClick = { navController.navigate("security_settings") }
                )
            }
            item {
                Spacer(modifier = Modifier.height(12.dp))
                SectionTitle("Sinkronisasi")
            }
            item {
                SettingsListItem(
                    icon = Icons.Outlined.Cloud,
                    title = "Pengaturan Cloud",
                    subtitle = "WebDAV dan sinkronisasi",
                    onClick = { navController.navigate("cloud_settings") }
                )
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
    )
}

@Composable
fun SettingsListItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle) },
        leadingContent = {
            Icon(icon, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        },
        trailingContent = {
            Icon(Icons.Outlined.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}
