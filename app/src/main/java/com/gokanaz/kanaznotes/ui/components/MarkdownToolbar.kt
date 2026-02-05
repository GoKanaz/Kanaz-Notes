package com.gokanaz.kanaznotes.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class MarkdownAction(
    val icon: ImageVector,
    val label: String,
    val prefix: String,
    val suffix: String = "",
    val isBlock: Boolean = false
)

@Composable
fun MarkdownToolbar(
    onActionClick: (MarkdownAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    
    val formattingActions = listOf(
        MarkdownAction(Icons.Outlined.FormatBold, "Bold", "**", "**"),
        MarkdownAction(Icons.Outlined.FormatItalic, "Italic", "_", "_"),
        MarkdownAction(Icons.Outlined.FormatUnderlined, "Underline", "<u>", "</u>"),
        MarkdownAction(Icons.Outlined.FormatStrikethrough, "Strikethrough", "~~", "~~")
    )
    
    val structureActions = listOf(
        MarkdownAction(Icons.Outlined.Title, "H1", "# ", isBlock = true),
        MarkdownAction(Icons.Outlined.Code, "Code", "`", "`"),
        MarkdownAction(Icons.Outlined.DataObject, "Code Block", "```\n", "\n```", isBlock = true),
        MarkdownAction(Icons.Outlined.FormatQuote, "Quote", "> ", isBlock = true)
    )
    
    val listActions = listOf(
        MarkdownAction(Icons.Outlined.FormatListBulleted, "Bullet", "- ", isBlock = true),
        MarkdownAction(Icons.Outlined.FormatListNumbered, "Number", "1. ", isBlock = true),
        MarkdownAction(Icons.Outlined.Functions, "Math", "$", "$"),
        MarkdownAction(Icons.Outlined.BarChart, "Table", "| Header |\n|--------|\n| Cell |", isBlock = true)
    )
    
    val otherActions = listOf(
        MarkdownAction(Icons.Outlined.Label, "Tag", "#", ""),
        MarkdownAction(Icons.Outlined.HorizontalRule, "Divider", "\n---\n", isBlock = true),
        MarkdownAction(Icons.Outlined.CheckBox, "Checkbox", "- [ ] ", isBlock = true),
        MarkdownAction(Icons.Outlined.Link, "Link", "[", "](url)")
    )
    
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                formattingActions.forEach { action ->
                    ToolbarButton(action = action, onClick = { onActionClick(action) })
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                structureActions.forEach { action ->
                    ToolbarButton(action = action, onClick = { onActionClick(action) })
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listActions.forEach { action ->
                    ToolbarButton(action = action, onClick = { onActionClick(action) })
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBox(4.dp)
            ) {
                otherActions.forEach { action ->
                    ToolbarButton(action = action, onClick = { onActionClick(action) })
                }
            }
        }
    }
}

@Composable
private fun ToolbarButton(
    action: MarkdownAction,
    onClick: () -> Unit
) {
    FilledTonalIconButton(
        onClick = onClick,
        modifier = Modifier.size(40.dp)
    ) {
        Icon(
            imageVector = action.icon,
            contentDescription = action.label,
            modifier = Modifier.size(20.dp)
        )
    }
}
