package com.gokanaz.kanaznotes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StyleSettingsScreen(
    onApplyBrush: () -> Unit
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
                onClick = onApplyBrush,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Filled.Brush, contentDescription = "Brush")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Apply Brush Style")
            }
        }
    }
}
