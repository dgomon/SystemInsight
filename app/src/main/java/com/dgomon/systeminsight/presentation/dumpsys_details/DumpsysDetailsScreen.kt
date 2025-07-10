package com.dgomon.systeminsight.presentation.dumpsys_details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.navigation.NavController
import com.dgomon.systeminsight.R
import com.dgomon.systeminsight.presentation.scaffold.AppScaffoldViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DumpsysDetailsScreen(
    serviceName: String,
    navController: NavController,
    modifier: Modifier = Modifier,
    scaffoldViewModel: AppScaffoldViewModel = hiltViewModel(),
    dumpsysDetailsViewModel: DumpsysDetailsViewModel = hiltViewModel(),
) {
    val output by dumpsysDetailsViewModel.serviceOutput.collectAsState()

    LaunchedEffect(output) {
        scaffoldViewModel.topBarContent.value = {
            TopAppBar(
                title = { Text(serviceName) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (!output.isEmpty()) {
                        IconButton(
                            onClick = {
                                dumpsysDetailsViewModel.shareOutput()
                            },
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
                        }
                    }
                }
            )
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
