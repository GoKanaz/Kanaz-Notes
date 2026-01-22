package com.gokanaz.kanaznotes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gokanaz.kanaznotes.ui.viewmodel.NoteViewModel
import com.gokanaz.kanaznotes.ui.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    noteViewModel: NoteViewModel,
    settingsViewModel: SettingsViewModel,
    onNavigateToAddNote: () -> Unit,
    onNavigateToEditNote: (Int) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToSearch: () -> Unit
) {
    val notes by noteViewModel.notes.collectAsState(initial = emptyList())
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Kanaz Notes", modifier = Modifier.padding(28.dp, 16.dp), style = MaterialTheme.typography.headlineSmall)
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Lightbulb, null) },
                    label = { Text("Catatan") },
                    selected = true,
                    onClick = { scope.launch { drawerState.close() } }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Outlined.Notifications, null) },
                    label = { Text("Pengingat") },
                    selected = false,
                    onClick = { }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Add, null) },
                    label = { Text("Buat label baru") },
                    selected = false,
                    onClick = { }
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Outlined.Archive, null) },
                    label = { Text("Arsip") },
                    selected = false,
                    onClick = { }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Outlined.Delete, null) },
                    label = { Text("Sampah") },
                    selected = false,
                    onClick = { }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, null) },
                    label = { Text("Setelan") },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        onNavigateToSettings() 
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Outlined.HelpOutline, null) },
                    label = { Text("Bantuan & masukan") },
                    selected = false,
                    onClick = { }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { 
                        Text("Telusuri catatan Anda", style = MaterialTheme.typography.bodyLarge, color = Color.Gray) 
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.ViewAgenda, contentDescription = "Tampilan")
                        }
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.AccountCircle, contentDescription = "Profil")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            floatingActionButton = {
                LargeFloatingActionButton(
                    onClick = onNavigateToAddNote,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah", modifier = Modifier.size(36.dp))
                }
            }
        ) { padding ->
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalItemSpacing = 8.dp
            ) {
                items(notes) { note ->
                    OutlinedCard(
                        onClick = { onNavigateToEditNote(note.id) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.outlinedCardColors(containerColor = Color.Transparent),
                        border = CardDefaults.outlinedCardBorder().copy(width = 0.5.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            if (note.title.isNotEmpty()) {
                                Text(note.title, style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            Text(note.content, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}
