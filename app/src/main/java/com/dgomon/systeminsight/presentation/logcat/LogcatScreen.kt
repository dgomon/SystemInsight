package com.dgomon.systeminsight.presentation.logcat

import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dgomon.systeminsight.R
import com.dgomon.systeminsight.presentation.scaffold.AppScaffoldViewModel
import com.dgomon.systeminsight.ui.common.RequirePrivilegedConnection

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun LogcatScreen(
    modifier: Modifier = Modifier,
    scaffoldViewModel: AppScaffoldViewModel,
    logcatViewModel: LogcatViewModel = hiltViewModel(),
) {
    val logLines by logcatViewModel.logLines.collectAsState()
    val isConnected by logcatViewModel.isConnected.collectAsState()
    val state by logcatViewModel.state.collectAsState()

    LaunchedEffect(isConnected) {
        scaffoldViewModel.topBarContent.value = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_logcat)) },
                actions = {
                    if (isConnected) {
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
                                    Icons.Default.Pause,
                                contentDescription = "Share",
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
            )
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
        }
    }
}
