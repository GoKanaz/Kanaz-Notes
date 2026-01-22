package com.gokanaz.kanaznotes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gokanaz.kanaznotes.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StyleScreen(
    settingsViewModel: SettingsViewModel,
    onBack: () -> Unit
) {
    val darkMode by settingsViewModel.darkMode.collectAsState()
    val amoledMode by settingsViewModel.isAmoledMode.collectAsState()
    val dynamicColor by settingsViewModel.isDynamicColor.collectAsState()
    val selectedPalette by settingsViewModel.selectedColorPalette.collectAsState()

    val colorPalettes = listOf(
        listOf(Color(0xFFF8BBD0), Color(0xFFE1BEE7), Color(0xFFD1C4E9), Color(0xFF880E4F)),
        listOf(Color(0xFFB3E5FC), Color(0xFFC5CAE9), Color(0xFFD1C4E9), Color(0xFF1A237E)),
        listOf(Color(0xFFC8E6C9), Color(0xFFDCEDC8), Color(0xFFF0F4C3), Color(0xFF1B5E20)),
        listOf(Color(0xFFFFE0B2), Color(0xFFFFF9C4), Color(0xFFF5F5DC), Color(0xFFE65100))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Style", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.size(120.dp).background(
                        brush = Brush.sweepGradient(
                            colors = if (dynamicColor) listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary)
                            else colorPalettes[selectedPalette]
                        ),
                        shape = CircleShape
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Color palette", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)

            Row(
                modifier = Modifier.fillMaxWidth().clickable { settingsViewModel.setDynamicColor(!dynamicColor) }.padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = dynamicColor, onClick = { settingsViewModel.setDynamicColor(!dynamicColor) })
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.padding(horizontal = 8.dp))
                Text("Dynamic color")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                colorPalettes.forEachIndexed { index, palette ->
                    Box(
                        modifier = Modifier.size(64.dp).clip(CircleShape)
                            .background(Brush.linearGradient(listOf(palette[0], palette[3])))
                            .border(width = if (selectedPalette == index && !dynamicColor) 3.dp else 0.dp, color = MaterialTheme.colorScheme.onSurface, shape = CircleShape)
                            .clickable {
                                settingsViewModel.setDynamicColor(false)
                                settingsViewModel.setColorPalette(index)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedPalette == index && !dynamicColor) Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth().clickable { settingsViewModel.setAmoledMode(!amoledMode) }.padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = amoledMode, onCheckedChange = { settingsViewModel.setAmoledMode(it) })
                Text("AMOLED Mode", modifier = Modifier.padding(start = 8.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Dark mode", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)

            listOf("System default", "Off", "On").forEach { mode ->
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { settingsViewModel.setDarkMode(mode) }.padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = (darkMode == mode), onClick = { settingsViewModel.setDarkMode(mode) })
                    Text(mode, modifier = Modifier.padding(start = 12.dp))
                }
            }
        }
    }
}
