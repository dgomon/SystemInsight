package com.dgomon.systeminsight.presentation.logcat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dgomon.systeminsight.R
import com.dgomon.systeminsight.ui.NavigationViewModel
import com.dgomon.systeminsight.ui.RequirePrivilegedConnection

@Preview
@Composable
fun LogcatScreen(
    modifier: Modifier = Modifier,
    navigationViewModel: NavigationViewModel,
    logcatViewModel: LogcatViewModel = hiltViewModel(),
    onFabContent: ((@Composable () -> Unit) -> Unit),
) {
    val logLines by logcatViewModel.logLines.collectAsState()

    val isConnected by logcatViewModel.isConnected.collectAsState()
    val isLogging by logcatViewModel.isCapturing.collectAsState()
    val isPaused by logcatViewModel.isPaused.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        navigationViewModel.setTitle(context.getString(R.string.title_logcat))
    }

    // Update FAB whenever logs change
    LaunchedEffect(logLines) {
        if (logLines.isEmpty()) {
            onFabContent {} // Clear FAB
        } else {
            onFabContent {
                FloatingActionButton(onClick = { logcatViewModel.shareOutput() }) {
                    Icon(Icons.Default.Share, contentDescription = "Share")
                }
            }
        }
    }

    RequirePrivilegedConnection(isConnected = isConnected, modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column {
                Button(
                    onClick = { logcatViewModel.startCapture() },
                    enabled = !isLogging
                ) {
                    Text(text = "Start")
                }

                Button(
                    onClick = { logcatViewModel.stopCapture() },
                    enabled = isLogging
                ) {
                    Text(text = "Stop")
                }

                Button(
                    onClick = { logcatViewModel.pauseCapture() },
                    enabled = isLogging && !isPaused
                ) {
                    Text(text = "Pause")
                }

                Button(
                    onClick = { logcatViewModel.resumeCapture() },
                    enabled = isLogging && isPaused
                ) {
                    Text(text = "Resume")
                }

                Button(
                    onClick = {
                        logcatViewModel.clear()
                    },
                    enabled = logLines.isNotEmpty()
                ) {
                    Text(text = "Clear")
                }

                Button(
                    onClick = { logcatViewModel.shareOutput() },
                    enabled = logLines.isNotEmpty()
                ) {
                    Text("Share Logs")
                }

                val listState = rememberLazyListState()
                LaunchedEffect(logLines.size) {
                    // Scroll to the last item when a new one is added
                    if (logLines.isNotEmpty()) {
                        listState.scrollToItem(logLines.lastIndex)
                    }
                }

                LazyColumn(
                    state = listState,
                    modifier = modifier
                        .fillMaxSize()
                ) {
                    items(logLines) { line ->
                        Text(text = line, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
