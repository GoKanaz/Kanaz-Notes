package com.gokanaz.kanaznotes.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.gokanaz.kanaznotes.R
import com.gokanaz.kanaznotes.ui.viewmodel.NoteViewModel
import com.gokanaz.kanaznotes.ui.theme.noteColors
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    noteViewModel: NoteViewModel,
    navController: NavHostController
) {
    val notes by noteViewModel.allNotes.collectAsState(initial = emptyList())
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(true) }

    val filtered = if (query.isBlank()) emptyList()
    else notes.filter {
        it.title.contains(query, ignoreCase = true) || it.content.contains(query, ignoreCase = true)
    }

    SearchBar(
        query = query,
        onQueryChange = { query = it },
        onSearch = {},
        active = active,
        onActiveChange = { active = it },
        placeholder = { Text(stringResource(R.string.search_notes)) },
        leadingIcon = {
            if (active) {
                IconButton(onClick = {
                    if (query.isNotEmpty()) query = "" else { active = false; navController.popBackStack() }
                }) {
                    Icon(Icons.Default.ArrowBack, null)
                }
            } else {
                Icon(Icons.Default.Search, null)
            }
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { query = "" }) {
                    Icon(Icons.Default.Close, null)
                }
            }
        }
    ) {
        if (query.isBlank()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.Search, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(12.dp))
                Text(stringResource(R.string.tap_to_search), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else if (filtered.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.SearchOff, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(12.dp))
                Text(stringResource(R.string.no_results, query), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(8.dp)) {
                items(filtered, key = { it.id }) { note ->
                    val colorIndex = note.color.coerceIn(0, noteColors.size - 1)
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { navController.navigate("edit_note/${note.id}") },
                        colors = CardColors(
                            containerColor = noteColors[colorIndex],
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            disabledContainerColor = noteColors[colorIndex],
                            disabledContentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            if (note.title.isNotBlank()) {
                                Text(note.title, style = MaterialTheme.typography.titleMedium, maxLines = 1)
                            }
                            if (note.content.isNotBlank()) {
                                Text(note.content, style = MaterialTheme.typography.bodyMedium, maxLines = 2, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text(
                                SimpleDateFormat("dd MMM yyyy").format(Date(note.timestamp)),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}
