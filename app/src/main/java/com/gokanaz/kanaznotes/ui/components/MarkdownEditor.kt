package com.gokanaz.kanaznotes.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun MarkdownEditor(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    isPreviewMode: Boolean,
    modifier: Modifier = Modifier,
    placeholder: String = "Start writing..."
) {
    val focusRequester = remember { FocusRequester() }
    
    if (isPreviewMode) {
        MarkdownText(
            markdown = value.text,
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            color = MaterialTheme.colorScheme.onSurface
        )
    } else {
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier
                .fillMaxSize()
                .focusRequester(focusRequester),
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            },
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Default
            )
        )
        
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}

fun insertMarkdown(
    currentValue: TextFieldValue,
    action: MarkdownAction
): TextFieldValue {
    val text = currentValue.text
    val selection = currentValue.selection
    
    return when {
        action.isBlock -> {
            val lineStart = text.lastIndexOf('\n', selection.start - 1) + 1
            val newText = StringBuilder(text).insert(lineStart, action.prefix).toString()
            val newCursorPos = lineStart + action.prefix.length
            TextFieldValue(
                text = newText,
                selection = TextRange(newCursorPos)
            )
        }
        selection.collapsed -> {
            val newText = StringBuilder(text)
                .insert(selection.start, action.prefix + action.suffix)
                .toString()
            val newCursorPos = selection.start + action.prefix.length
            TextFieldValue(
                text = newText,
                selection = TextRange(newCursorPos)
            )
        }
        else -> {
            val selectedText = text.substring(selection.start, selection.end)
            val wrappedText = action.prefix + selectedText + action.suffix
            val newText = text.replaceRange(selection.start, selection.end, wrappedText)
            val newCursorPos = selection.start + action.prefix.length + selectedText.length
            TextFieldValue(
                text = newText,
                selection = TextRange(newCursorPos)
            )
        }
    }
}

fun handleEnterKey(currentValue: TextFieldValue): TextFieldValue {
    val text = currentValue.text
    val cursorPos = currentValue.selection.start
    
    val lineStart = text.lastIndexOf('\n', cursorPos - 1) + 1
    val currentLine = text.substring(lineStart, cursorPos)
    
    val bulletRegex = Regex("^(\\s*)- (\\[[ x]\\] )?")
    val numberRegex = Regex("^(\\s*)(\\d+)\\. ")
    
    return when {
        currentLine.matches(bulletRegex) -> {
            val match = bulletRegex.find(currentLine)!!
            val indent = match.groupValues[1]
            val checkbox = match.groupValues[2]
            val newPrefix = "\n$indent- $checkbox"
            
            val newText = StringBuilder(text).insert(cursorPos, newPrefix).toString()
            TextFieldValue(
                text = newText,
                selection = TextRange(cursorPos + newPrefix.length)
            )
        }
        currentLine.matches(numberRegex) -> {
            val match = numberRegex.find(currentLine)!!
            val indent = match.groupValues[1]
            val number = match.groupValues[2].toInt() + 1
            val newPrefix = "\n$indent$number. "
            
            val newText = StringBuilder(text).insert(cursorPos, newPrefix).toString()
            TextFieldValue(
                text = newText,
                selection = TextRange(cursorPos + newPrefix.length)
            )
        }
        else -> {
            val newText = StringBuilder(text).insert(cursorPos, "\n").toString()
            TextFieldValue(
                text = newText,
                selection = TextRange(cursorPos + 1)
            )
        }
    }
}
