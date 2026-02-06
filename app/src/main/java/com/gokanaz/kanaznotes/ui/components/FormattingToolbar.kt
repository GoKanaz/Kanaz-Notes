package com.gokanaz.kanaznotes.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

data class FormattingAction(
    val icon: ImageVector,
    val label: String,
    val prefix: String = "",
    val suffix: String = ""
)

@Composable
fun FormattingToolbar(
    onActionClick: (FormattingAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val actions = listOf(
        FormattingAction(Icons.Outlined.Undo, "Undo"),
        FormattingAction(Icons.Outlined.Redo, "Redo"),
        FormattingAction(Icons.Outlined.Title, "Heading", "# "),
        FormattingAction(Icons.Outlined.FormatBold, "Bold", "**", "**"),
        FormattingAction(Icons.Outlined.FormatItalic, "Italic", "_", "_"),
        FormattingAction(Icons.Outlined.FormatUnderlined, "Underline", "<u>", "</u>"),
        FormattingAction(Icons.Outlined.FormatStrikethrough, "Strikethrough", "~~", "~~"),
        FormattingAction(Icons.Outlined.FormatListBulleted, "Bullet List", "- "),
        FormattingAction(Icons.Outlined.FormatListNumbered, "Numbered List", "1. "),
        FormattingAction(Icons.Outlined.CheckBox, "Checkbox", "- [ ] "),
        FormattingAction(Icons.Outlined.Link, "Link", "[", "](url)")
    )
    
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            actions.forEach { action ->
                IconButton(
                    onClick = { onActionClick(action) },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = action.icon,
                        contentDescription = action.label,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
