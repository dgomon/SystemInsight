package com.dgomon.systeminsight.presentation.dumpsys_details

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DumpsysDetailsTopBar(
    navController: NavController,
    dumpsysDetailsViewModel: DumpsysDetailsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {

    val output by dumpsysDetailsViewModel.serviceOutput.collectAsState()
    val serviceName = dumpsysDetailsViewModel.serviceName

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