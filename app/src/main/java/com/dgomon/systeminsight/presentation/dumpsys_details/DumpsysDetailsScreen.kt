package com.dgomon.systeminsight.presentation.dumpsys_details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
    viewModel: DumpsysDetailsViewModel = hiltViewModel()
) {
    val output by viewModel.serviceOutput.collectAsState()

    if (output.isEmpty() || output.size == 1 && output.first().isBlank()) {
        // Center message when there's no output or a single blank line
        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.dumpsys_no_output_message),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    } else {
        // Regular scrollable display for multi-line output
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            items(output) { line ->
                Text(text = line, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
