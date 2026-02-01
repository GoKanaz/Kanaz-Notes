package com.gokanaz.kanaznotes.ui.screens

import androidx.compose.foundation.background
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
import androidx.navigation.NavHostController
import com.gokanaz.kanaznotes.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StyleSettingsScreen(
    navController: NavHostController,
    settingsViewModel: SettingsViewModel
) {
    val darkMode by settingsViewModel.darkMode.collectAsState()
    val isAmoledMode by settingsViewModel.isAmoledMode.collectAsState()
    val isDynamicColor by settingsViewModel.isDynamicColor.collectAsState()
    val selectedPalette by settingsViewModel.selectedColorPalette.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gaya & Tema") },
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
            contentPadding = PaddingValues(16.dp),
            verticalItemSpacing = 8.dp
        ) {
            item {
                Card {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Mode Gelap", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("system" to "Sistem", "on" to "Aktif", "off" to "Nonaktif").forEach { (value, label) ->
                                FilterChip(
                                    selected = darkMode == value,
                                    onClick = { settingsViewModel.setDarkMode(value) },
                                    label = { Text(label) }
                                )
                            }
                        }
                    }
                }
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
                                Text("Mode AMOLED", style = MaterialTheme.typography.titleMedium)
                                Text("Layar hitam total di mode gelap", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(checked = isAmoledMode, onCheckedChange = { settingsViewModel.setAmoledMode(it) })
                        }
                    }
                }
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
                                Text("Warna Dinamis", style = MaterialTheme.typography.titleMedium)
                                Text("Ikuti warna perangkat (Android 12+)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(checked = isDynamicColor, onCheckedChange = { settingsViewModel.setDynamicColor(it) })
                        }
                    }
                }
            }
            if (!isDynamicColor) {
                item {
                    Card {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Palet Warna", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                settingsViewModel.colorPalettes.forEachIndexed { index, palette ->
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clickable { settingsViewModel.setSelectedColorPalette(index) }
                                            .border(
                                                width = if (selectedPalette == index) 2.5.dp else 1.dp,
                                                color = if (selectedPalette == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                                shape = androidx.compose.ui.shape.RoundedCornerShape(12.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row {
                                            palette.take(3).forEach { c ->
                                                Box(modifier = Modifier.size(14.dp).background(c))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
