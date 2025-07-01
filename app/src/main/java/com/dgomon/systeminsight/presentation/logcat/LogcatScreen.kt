package com.dgomon.systeminsight.presentation.logcat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Preview
@Composable
fun LogcatScreen(modifier: Modifier = Modifier, viewModel: LogcatViewModel = hiltViewModel()) {
    val logs by viewModel.logs.collectAsState(initial = "")
    val isConnected by viewModel.isConnected.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Row {
            Button(
                onClick = { viewModel.startLogging() },
                enabled = isConnected
            ) {
                Text(text = "Start")
            }

            Button(
                onClick = { viewModel.stopLogging() },
                enabled = isConnected
            ) {
                Text(text = "Stop")
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
