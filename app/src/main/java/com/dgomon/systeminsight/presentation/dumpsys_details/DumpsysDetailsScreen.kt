package com.dgomon.systeminsight.presentation.dumpsys_details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dgomon.systeminsight.R
import com.dgomon.systeminsight.ui.common.HighlightedText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DumpsysDetailsScreen(
    modifier: Modifier = Modifier,
    dumpsysDetailsViewModel: DumpsysDetailsViewModel,
) {
    val query by dumpsysDetailsViewModel.query.collectAsState()
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
            modifier = modifier.fillMaxSize().padding(16.dp),
        ) {
            item {
                Text(
                    text = dumpsysDetailsViewModel.serviceName,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(lines) { text ->
                HighlightedText(text = text, query = query, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
