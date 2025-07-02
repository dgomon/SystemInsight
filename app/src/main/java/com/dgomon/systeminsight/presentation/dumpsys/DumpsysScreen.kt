package com.dgomon.systeminsight.presentation.dumpsys

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
fun DumpsysScreen(
    modifier: Modifier = Modifier,
    viewModel: DumpsysViewModel = hiltViewModel(),
    onServiceClick: (String) -> Unit
) {
    val services by viewModel.services.collectAsState()

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(services) { service ->
            Text(
                text = service,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clickable { onServiceClick(service) }
                    .padding(8.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
