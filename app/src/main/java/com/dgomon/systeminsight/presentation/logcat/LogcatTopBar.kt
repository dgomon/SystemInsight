package com.dgomon.systeminsight.presentation.logcat

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dgomon.systeminsight.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogcatTopBar(logcatViewModel: LogcatViewModel = hiltViewModel()) {
    val query by logcatViewModel.query.collectAsState()
    val isConnected by logcatViewModel.isConnected.collectAsState()
    val focusManager = LocalFocusManager.current
    val state by logcatViewModel.logcatState.collectAsState()
    val filteredLogLines by logcatViewModel.filteredLogLines.collectAsState()

    TopAppBar(
        title = {
            if (isConnected) {
                OutlinedTextField(
                    value = query,
                    onValueChange = logcatViewModel::setQuery,
                    placeholder = { Text(stringResource(R.string.filter_logs)) },
                    singleLine = true,
                    leadingIcon = {
                        IconButton(onClick = {
                            focusManager.clearFocus() // Dismiss keyboard
                        }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = {
                                logcatViewModel.setQuery("")
                                focusManager.clearFocus() // Dismiss keyboard
                            }) {
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
                        if (state == LogcatState.Paused) {
                            logcatViewModel.resumeCapture()
                        } else {
                            logcatViewModel.pauseCapture()
                        }
                    },
                    enabled = true
                ) {
                    Icon(
                        imageVector =
                            if (state == LogcatState.Resumed)
                                Icons.Default.Pause
                            else
                                Icons.Default.PlayArrow,
                        contentDescription = "Collect or pause",
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
