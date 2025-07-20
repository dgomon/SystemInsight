package com.dgomon.systeminsight.presentation.logcat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import com.dgomon.systeminsight.ui.common.HighlightedText
import com.dgomon.systeminsight.ui.common.RequirePrivilegedConnection
import kotlinx.coroutines.flow.collectLatest

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

            LazyColumn(
                state = listState,
                modifier = modifier
                    .fillMaxSize()
            ) {
                items(parsedLogEntries) { entry ->
                    val backgroundColor = when (entry.logLevel) {
                        "V" -> Color.LightGray
                        "D" -> Color(0xFFBBDEFB) // light blue
                        "I" -> Color(0xFFC8E6C9) // light green
                        "W" -> Color(0xFFFFF9C4) // light yellow
                        "E" -> Color(0xFFFFCDD2) // light red
                        "F" -> Color(0xFFE1BEE7) // light purple
                        else -> Color.Transparent
                    }

                    Row(modifier = Modifier
                            .fillMaxWidth()
                            .background(color = backgroundColor)
                            .height(IntrinsicSize.Min)
                            .clickable(enabled = true, onClick = {
                                logcatViewModel.onRowClicked(entry.rawLine)
                            }),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(1.dp),
                            thickness = DividerDefaults.Thickness, color = Color.LightGray
                        )

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
                            thickness = DividerDefaults.Thickness, color = Color.LightGray
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
                            thickness = DividerDefaults.Thickness, color = Color.LightGray
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
)}
