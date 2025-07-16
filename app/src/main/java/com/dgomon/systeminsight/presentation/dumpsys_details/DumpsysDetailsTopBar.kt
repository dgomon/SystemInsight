package com.dgomon.systeminsight.presentation.dumpsys_details

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dgomon.systeminsight.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DumpsysDetailsTopBar(
    navController: NavController,
    dumpsysDetailsViewModel: DumpsysDetailsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {
    val output by dumpsysDetailsViewModel.serviceOutput.collectAsState()
    val query by dumpsysDetailsViewModel.query.collectAsState()

    TopAppBar(
        title = {
            if (!output.isBlank()) {
                OutlinedTextField(
                    value = query,
                    onValueChange = dumpsysDetailsViewModel::setQuery,
                    placeholder = { Text(stringResource(R.string.search)) },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { dumpsysDetailsViewModel.setQuery("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 16.dp)
                )
            }
        },
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