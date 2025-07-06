package com.dgomon.systeminsight.presentation.logcat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Alignment
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
    val logs by logcatViewModel.logs.collectAsState(initial = "")
    val isConnected by logcatViewModel.isConnected.collectAsState()
    val isLogging by logcatViewModel.isCapturing.collectAsState()
    val isPaused by logcatViewModel.isPaused.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        navigationViewModel.setTitle(context.getString(R.string.title_logcat))
    }

    // Update FAB whenever logs change
    LaunchedEffect(logs) {
        if (logs.isEmpty()) {
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
                    onClick = { logcatViewModel.shareOutput() },
                    enabled = logs.isNotEmpty()
                ) {
                    Text("Share Logs")
                }
            }

            val scrollState = rememberScrollState()

            Text(
                text = logs,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .height(144.dp)
                    .verticalScroll(scrollState)
                    .padding(vertical = 2.dp)
            )
        }
    }
}
