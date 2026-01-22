package com.gokanaz.kanaznotes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Upload
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DataSettingsScreen(
    onDownload: () -> Unit,
    onUpload: () -> Unit
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
                onClick = onDownload,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Filled.Download, contentDescription = "Download")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Download Data")
            }

            Button(
                onClick = onUpload,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Filled.Upload, contentDescription = "Upload")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Upload Data")
            }
        }
    }
}
