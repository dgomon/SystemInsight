package com.dgomon.systeminsight.presentation.dumpsys_details

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DumpsysDetailsScreen(
    viewModel: DumpsysDetailsViewModel = hiltViewModel()
) {
    val output by viewModel.serviceOutput.collectAsState()

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(output) { line ->
            Text(text = line, style = MaterialTheme.typography.bodySmall)
        }
    }
}
