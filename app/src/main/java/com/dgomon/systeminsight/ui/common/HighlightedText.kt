package com.dgomon.systeminsight.ui.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

@Composable
fun HighlightedText(line: String, query: String, modifier: Modifier = Modifier) {
    if (query.isBlank()) {
        Text(text = line, modifier = modifier, style = MaterialTheme.typography.bodyMedium)
        return
    }

    val annotatedString = buildAnnotatedString {
        val lowerCaseLine = line.lowercase()
        val lowerCaseQuery = query.lowercase()
        var currentIndex = 0

        while (currentIndex < line.length) {
            val matchIndex = lowerCaseLine.indexOf(lowerCaseQuery, startIndex = currentIndex)
            if (matchIndex == -1) {
                append(line.substring(currentIndex))
                break
            }

            // Append text before the match
            append(line.substring(currentIndex, matchIndex))

            // Highlighted match
            withStyle(SpanStyle(
                background = Color.Yellow
            )) {
                append(line.substring(matchIndex, matchIndex + query.length))
            }

            currentIndex = matchIndex + query.length
        }
    }

    Text(text = annotatedString, modifier = modifier, style = MaterialTheme.typography.bodyMedium)
}
