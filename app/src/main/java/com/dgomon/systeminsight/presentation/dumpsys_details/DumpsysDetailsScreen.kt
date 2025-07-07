package com.dgomon.systeminsight.presentation.dumpsys_details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dgomon.systeminsight.R

@Composable
fun DumpsysDetailsScreen(
    modifier: Modifier = Modifier,
    serviceName: String,
    dumpsysDetailsViewModel: DumpsysDetailsViewModel = hiltViewModel(),
    onFabContent: ((@Composable () -> Unit) -> Unit)
) {
    val output by dumpsysDetailsViewModel.serviceOutput.collectAsState()

    // Update FAB whenever logs change
    LaunchedEffect(output) {
        if (output.isEmpty()) {
            onFabContent {} // Clear FAB
        } else {
            onFabContent {
                FloatingActionButton(onClick = { dumpsysDetailsViewModel.shareOutput() }) {
                    Icon(Icons.Default.Share, contentDescription = "Share")
                }
            }
        }
    }

    if (output.isBlank()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.dumpsys_no_output_message),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    } else {
        val lines = remember(output) { output.lineSequence().toList() }

        LazyColumn(
            modifier = modifier.fillMaxSize().padding(16.dp),
        ) {
            items(lines) { line ->
                Text(text = line, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
