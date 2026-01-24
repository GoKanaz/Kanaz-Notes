package com.gokanaz.kanaznotes.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ListSettingsScreen(
    cardSize: Int,
    textOverflow: Boolean
) {
    Column {
        if (cardSize == 1) {
            Text(text = "Card size is 1")
        } else if (cardSize == 2) {
            Text(text = "Card size is 2")
        }

        if (textOverflow == true) {
            Text(text = "Text overflow enabled")
        } else {
            Text(text = "Text overflow disabled")
        }
    }
}
