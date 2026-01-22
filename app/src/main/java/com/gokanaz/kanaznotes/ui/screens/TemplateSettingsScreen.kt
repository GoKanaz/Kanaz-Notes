package com.gokanaz.kanaznotes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateSettingsScreen(navController: NavController) {
    var dateFormat by remember { mutableStateOf("yyyy-MM-dd") }
    var timeFormat by remember { mutableStateOf("HH:mm") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Template Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text("Date Format", style = MaterialTheme.typography.labelLarge)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { dateFormat = "yyyy-MM-dd" }) { Text("ISO") }
                Button(onClick = { dateFormat = "dd/MM/yyyy" }) { Text("Normal") }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Time Format", style = MaterialTheme.typography.labelLarge)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { timeFormat = "HH:mm" }) { Text("24h") }
                Button(onClick = { timeFormat = "hh:mm a" }) { Text("12h") }
            }
        }
    }
}
