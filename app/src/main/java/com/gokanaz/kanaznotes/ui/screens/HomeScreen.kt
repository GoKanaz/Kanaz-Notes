package com.gokanaz.kanaznotes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onLightbulb: () -> Unit,
    onArchive: () -> Unit,
    onHelp: () -> Unit,
    onAgenda: () -> Unit
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onLightbulb,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Filled.Lightbulb, contentDescription = "Lightbulb")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ideas")
            }

            Button(
                onClick = onArchive,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Filled.Archive, contentDescription = "Archive")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Archive Notes")
            }

            Button(
                onClick = onHelp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Filled.HelpOutline, contentDescription = "Help")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Help")
            }

            Button(
                onClick = onAgenda,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Filled.ViewAgenda, contentDescription = "Agenda")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Agenda")
            }
        }
    }
}
