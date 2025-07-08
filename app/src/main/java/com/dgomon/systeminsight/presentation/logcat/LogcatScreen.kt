package com.dgomon.systeminsight.presentation.logcat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dgomon.systeminsight.R
import com.dgomon.systeminsight.ui.AppScaffoldViewModel
import com.dgomon.systeminsight.ui.NavigationViewModel
import com.dgomon.systeminsight.ui.RequirePrivilegedConnection

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun LogcatScreen(
    modifier: Modifier = Modifier,
//    navigationViewModel: NavigationViewModel,
    scaffoldViewModel: AppScaffoldViewModel,
    logcatViewModel: LogcatViewModel = hiltViewModel(),
    onFabContent: ((@Composable () -> Unit) -> Unit),
) {
    val logLines by logcatViewModel.logLines.collectAsState()
    val isConnected by logcatViewModel.isConnected.collectAsState()
    val state by logcatViewModel.state.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
//        navigationViewModel.setTitle(context.getString(R.string.title_logcat))
        scaffoldViewModel.topBarContent.value = {
            TopAppBar(
                title = { Text("Logcat") },
                actions = {
                    IconButton(onClick = { logcatViewModel.clear() }) {
                        Icon(Icons.Default.Delete, contentDescription = "Clear")
                    }
                    IconButton(onClick = { logcatViewModel.shareOutput() }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                }
            )
        }
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

            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(16.dp)
            ) {

                IconButton(
                    onClick = {
                        if (state == LogcatState.Idle) {
                            logcatViewModel.resumeCapture()
                        } else {
                            logcatViewModel.pauseCapture()
                        }
                    },
                    enabled = true
                ) {
                    Icon(
                        imageVector = if (state == LogcatState.Idle) Icons.Default.PlayArrow else
                            Icons.Default.Pause, contentDescription = "Share",
                    )
                }

                IconButton(
                    onClick = {
                        logcatViewModel.clear()
                    },
                    enabled = logLines.isNotEmpty()
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Share")
                }

                IconButton(
                    onClick = { logcatViewModel.shareOutput() },
                    enabled = logLines.isNotEmpty()
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Share")
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            scaffoldViewModel.topBarContent.value = null
        }
    }
}
