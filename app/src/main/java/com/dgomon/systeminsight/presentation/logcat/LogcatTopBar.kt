package com.dgomon.systeminsight.presentation.logcat

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.dgomon.systeminsight.R
import com.dgomon.systeminsight.ui.common.CommonTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogcatTopBar(
    navBackStackEntry: NavBackStackEntry,
    navController: NavController,
    logcatViewModel: LogcatViewModel = hiltViewModel(navBackStackEntry)
) {
    val query by logcatViewModel.query.collectAsState()
    val isConnected by logcatViewModel.isConnected.collectAsState()
    val state by logcatViewModel.logcatState.collectAsState()
    val filteredLogLines by logcatViewModel.filteredLogLines.collectAsState()

    CommonTopBar(
        title = {
            if (isConnected) {
                OutlinedTextField(
                    value = query,
                    onValueChange = logcatViewModel::setQuery,
                    placeholder = { Text("Filter logs") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text("Logcat")
            }
        },
        showMenu = true,
        menuItems = listOf(
            stringResource(R.string.settings) to {
                navController.navigate(Destination.Settings.route)
            }
        ),
        actions = {
            if (isConnected) {
                IconButton(onClick = {
                    if (state == LogcatState.Paused) logcatViewModel.resumeCapture()
                    else logcatViewModel.pauseCapture()
                }) {
                    Icon(
                        imageVector = if (state == LogcatState.Resumed)
                            Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Pause/Resume"
                    )
                }

                IconButton(
                    onClick = { logcatViewModel.clear() },
                    enabled = filteredLogLines.isNotEmpty()
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Clear")
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