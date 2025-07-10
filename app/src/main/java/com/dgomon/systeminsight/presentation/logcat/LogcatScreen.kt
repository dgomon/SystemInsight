package com.dgomon.systeminsight.presentation.logcat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
    scaffoldViewModel: AppScaffoldViewModel,
    modifier: Modifier = Modifier,
    logcatViewModel: LogcatViewModel = hiltViewModel(),
) {
    val query by logcatViewModel.query.collectAsState()
    val filteredLogLines by logcatViewModel.filteredLogLines.collectAsState()
    val isConnected by logcatViewModel.isConnected.collectAsState()
    val state by logcatViewModel.state.collectAsState()

    LaunchedEffect(isConnected) {
        scaffoldViewModel.topBarContent.value = {
            TopAppBar(
                title = {
                    if (isConnected) {
                        OutlinedTextField(
                            value = query,
                            onValueChange = logcatViewModel::setQuery,
                            placeholder = { Text(stringResource(R.string.filter_logs)) },
                            singleLine = true,
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = null
                                )
                            },
                            trailingIcon = {
                                if (query.isNotEmpty()) {
                                    IconButton(onClick = { logcatViewModel.setQuery("") }) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear")
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 16.dp)
                        )
                    }
                    else {
                        Text(stringResource(R.string.title_logcat))
                    }
                },
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
                            enabled = filteredLogLines.isNotEmpty()
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Share")
                        }

                        IconButton(
                            onClick = { logcatViewModel.shareOutput() },
                            enabled = filteredLogLines.isNotEmpty()
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
                        }
                    }
                }
            )
        }
    }

    RequirePrivilegedConnection(isConnected = isConnected, modifier = modifier, content = {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            val listState = rememberLazyListState()

            LaunchedEffect(filteredLogLines.size) {
                // Scroll to the last item when a new one is added
                if (filteredLogLines.isNotEmpty()) {
                    listState.scrollToItem(filteredLogLines.lastIndex)
                }
            }

            LazyColumn(
                state = listState,
                modifier = modifier
                    .fillMaxSize()
            ) {
                items(filteredLogLines) { line ->
                    Text(text = line, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    })
}
