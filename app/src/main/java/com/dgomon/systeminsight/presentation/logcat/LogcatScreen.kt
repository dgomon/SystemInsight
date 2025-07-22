package com.dgomon.systeminsight.presentation.logcat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import com.dgomon.systeminsight.R
import com.dgomon.systeminsight.ui.common.HighlightedText
import com.dgomon.systeminsight.ui.common.RequirePrivilegedConnection
import kotlinx.coroutines.flow.collectLatest

@Composable
private fun logLevelBackgroundColor(level: String): Color {
    val isDark = isSystemInDarkTheme()
    return when (level) {
        "V" -> if (isDark) Color.DarkGray else Color.LightGray
        "D" -> if (isDark) Color(0xFF0D47A1) else Color(0xFFBBDEFB)
        "I" -> if (isDark) Color(0xFF1B5E20) else Color(0xFFC8E6C9)
        "W" -> if (isDark) Color(0xFFF57F17) else Color(0xFFFFF9C4)
        "E" -> if (isDark) Color(0xFFB71C1C) else Color(0xFFFFCDD2)
        "F" -> if (isDark) Color(0xFF4A148C) else Color(0xFFE1BEE7)
        else -> Color.Transparent
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun LogcatScreen(
    navBackStackEntry: NavBackStackEntry,
    modifier: Modifier = Modifier,
    logcatViewModel: LogcatViewModel = hiltViewModel(navBackStackEntry),
) {
    val searchQuery by logcatViewModel.query.collectAsState()
    val filteredLogLines by logcatViewModel.filteredLogLines.collectAsState()
    val parsedLogEntries by logcatViewModel.parsedLogEntries.collectAsState()
    val isConnected by logcatViewModel.isConnected.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        logcatViewModel.onScreenCreated(true)
        logcatViewModel.copyEvents.collectLatest { logLine ->
            clipboardManager.setText(AnnotatedString(logLine))
            snackbarHostState.showSnackbar("Copied to clipboard")
        }
    }

    RequirePrivilegedConnection(isConnected = isConnected, modifier = modifier, content = {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val listState = rememberLazyListState()

            LaunchedEffect(parsedLogEntries.size) {
                // Scroll to the last item when a new one is added
                if (parsedLogEntries.isNotEmpty()) {
                    listState.scrollToItem(parsedLogEntries.lastIndex)
                }
            }

            if (parsedLogEntries.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Info, // Or any relevant icon
                        contentDescription = stringResource(R.string.no_logs),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(48.dp)
                            .padding(bottom = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = stringResource(R.string.no_logs_to_display),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = stringResource(R.string.press_the_play),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
            else {
                LazyColumn(
                    state = listState,
                    modifier = modifier
                        .fillMaxSize()
                ) {
                    items(parsedLogEntries) { entry ->
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .background(color = logLevelBackgroundColor(entry.logLevel))
                                .height(IntrinsicSize.Min)
                                .clickable(enabled = true, onClick = {
                                    logcatViewModel.onRowClicked(entry.rawLine)
                                }),
                        ) {

                            HorizontalDivider(
                                thickness = DividerDefaults.Thickness,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(IntrinsicSize.Min),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .width(32.dp)  // Fixed width for logLevel
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = entry.logLevel,
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }

                                VerticalDivider(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .width(1.dp),
                                    thickness = DividerDefaults.Thickness,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                                )

                                HighlightedText(
                                    text = entry.tag,
                                    query = searchQuery,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .width(64.dp)
                                        .padding(end = 8.dp)  // Optional, to balance padding
                                )

                                VerticalDivider(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .width(1.dp),
                                    thickness = DividerDefaults.Thickness,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                                )
                                HighlightedText(
                                    text = entry.message,
                                    query = searchQuery,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier
                                        .weight(1f)  // Take all remaining space
                                        .padding(end = 8.dp)  // Optional, to balance padding
                                )
                            }
                        }
                    }
                }
            }
        }
    }
)}
