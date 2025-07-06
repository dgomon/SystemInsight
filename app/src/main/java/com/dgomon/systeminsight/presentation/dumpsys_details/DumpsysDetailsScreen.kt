package com.dgomon.systeminsight.presentation.dumpsys_details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.dgomon.systeminsight.R
import com.dgomon.systeminsight.ui.NavigationViewModel

@Composable
fun DumpsysDetailsScreen(
    modifier: Modifier = Modifier,
    serviceName: String,
    dumpsysDetailsViewModel: DumpsysDetailsViewModel = hiltViewModel(),
) {
    val output by dumpsysDetailsViewModel.serviceOutput.collectAsState()

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
            modifier = modifier.fillMaxSize()
        ) {
            items(lines) { line ->
                Text(text = line, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
