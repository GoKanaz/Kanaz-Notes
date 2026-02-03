package com.gokanaz.kanaznotes.ui.screens

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.gokanaz.kanaznotes.MainActivity
import com.gokanaz.kanaznotes.ui.viewmodel.SettingsViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSettingsScreen(
    navController: NavHostController,
    settingsViewModel: SettingsViewModel
) {
    val context = LocalContext.current
    val language by settingsViewModel.language.collectAsState()

    val languages = listOf(
        "en" to "English",
        "id" to "Bahasa Indonesia"
    )
    
    fun changeLanguageAndRestart(newLang: String) {
        if (language != newLang) {
            settingsViewModel.setLanguage(newLang)
            
            val locale = when(newLang) {
                "id" -> Locale("id", "ID")
                else -> Locale("en", "US")
            }
            Locale.setDefault(locale)
            
            val config = context.resources.configuration
            config.setLocale(locale)
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            
            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)
            (context as? Activity)?.finish()
            Runtime.getRuntime().exit(0)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bahasa") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Card {
                    Column {
                        languages.forEachIndexed { index, (code, name) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { changeLanguageAndRestart(code) }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = name,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = code.uppercase(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                RadioButton(
                                    selected = language == code,
                                    onClick = { changeLanguageAndRestart(code) }
                                )
                            }
                            if (index < languages.size - 1) {
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
