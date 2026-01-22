package com.gokanaz.kanaznotes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gokanaz.kanaznotes.ui.viewmodel.SettingsViewModel

@Composable
fun PasswordScreen(
    settingsViewModel: SettingsViewModel,
    onPasswordComplete: () -> Unit,
    onCancel: () -> Unit
) {
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().background(Color.Black).padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text("Enter passcode", color = Color.White, fontWeight = FontWeight.Bold)
        
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            repeat(4) { index ->
                Box(
                    modifier = Modifier.size(16.dp).border(2.dp, Color(0xFF90CAF9), CircleShape)
                        .background(if (index < password.length) Color(0xFF90CAF9) else Color.Transparent, CircleShape)
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            val keys = listOf(listOf("1", "2", "3"), listOf("4", "5", "6"), listOf("7", "8", "9"), listOf("", "0", "back"))
            keys.forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    row.forEach { key ->
                        if (key == "back") {
                            IconButton(onClick = { if (password.isNotEmpty()) password = password.dropLast(1) }) {
                                Icon(Icons.Default.Backspace, null, tint = Color.White)
                            }
                        } else if (key != "") {
                            Box(
                                modifier = Modifier.size(72.dp).clip(CircleShape).background(Color(0xFF212121))
                                    .clickable { if (password.length < 4) password += key },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(key, color = Color.White, fontSize = 28.sp)
                            }
                        } else {
                            Spacer(modifier = Modifier.size(72.dp))
                        }
                    }
                }
            }
        }

        TextButton(onClick = onCancel) {
            Text("Batal", color = Color(0xFF90CAF9))
        }
    }

    if (password.length == 4) {
        LaunchedEffect(Unit) {
            onPasswordComplete()
        }
    }
}
