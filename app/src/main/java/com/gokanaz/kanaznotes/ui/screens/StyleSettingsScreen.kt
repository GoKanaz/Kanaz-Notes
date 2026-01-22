package com.gokanaz.kanaznotes.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.gokanaz.kanaznotes.R
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
                title = { Text("Style") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            item { ThemePreviewSection() }

            item {
                Text(
                    text = "Color palette",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { settingsViewModel.setDynamicColor(!isDynamicColor) }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = isDynamicColor, onClick = { settingsViewModel.setDynamicColor(!isDynamicColor) })
                    Icon(Icons.Default.Brush, null, modifier = Modifier.padding(start = 8.dp))
                    Text("Dynamic color", modifier = Modifier.padding(start = 12.dp))
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    itemsIndexed(settingsViewModel.colorPalettes) { index, palette ->
                        PaletteItem(
                            colors = palette,
                            isSelected = !isDynamicColor && selectedPalette == index,
                            onClick = {
                                settingsViewModel.setDynamicColor(false)
                                settingsViewModel.setColorPalette(index)
                            }
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { settingsViewModel.setAmoledMode(!isAmoledMode) }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = isAmoledMode, onCheckedChange = { settingsViewModel.setAmoledMode(it) })
                    Text("AMOLED Mode", modifier = Modifier.padding(start = 12.dp))
                }
            }

            item {
                Divider(modifier = Modifier.padding(vertical = 16.dp))
                Text(
                    text = "Dark mode",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                listOf("system" to "System default", "off" to "Off", "on" to "On").forEach { (mode, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { settingsViewModel.setDarkMode(mode) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = darkMode == mode, onClick = { settingsViewModel.setDarkMode(mode) })
                        Text(label, modifier = Modifier.padding(start = 12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun PaletteItem(colors: List<Color>, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.size(44.dp).clip(RoundedCornerShape(8.dp))
        ) {
            Row {
                Box(Modifier.size(22.dp).background(colors[0]))
                Box(Modifier.size(22.dp).background(colors[1]))
            }
            Row {
                Box(Modifier.size(22.dp).background(colors[2]))
                Box(Modifier.size(22.dp).background(colors[3]))
            }
        }
        if (isSelected) {
            Icon(
                Icons.Default.Check,
                null,
                tint = Color.White,
                modifier = Modifier.background(Color.Black.copy(0.4f), CircleShape).padding(2.dp)
            )
        }
    }
}

@Composable
fun ThemePreviewSection() {
    val duration = 500
    val animatedPrimary by animateColorAsState(targetValue = MaterialTheme.colorScheme.primary, animationSpec = tween(duration))
    val animatedSurface by animateColorAsState(targetValue = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), animationSpec = tween(duration))

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = animatedSurface)
    ) {
        Column(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_theme_preview),
                contentDescription = null,
                modifier = Modifier.size(140.dp)
            )
            Text(text = "Theme Preview", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = animatedPrimary)
            ) {
                Text("Sample Button")
            }
        }
    }
}
