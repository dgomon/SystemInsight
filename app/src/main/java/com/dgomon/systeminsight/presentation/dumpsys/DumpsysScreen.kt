package com.dgomon.systeminsight.presentation.dumpsys

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dgomon.systeminsight.R
import com.dgomon.systeminsight.presentation.privilege_control.PrivilegeControlViewModel
import com.dgomon.systeminsight.ui.NavigationViewModel
import com.dgomon.systeminsight.ui.RequirePrivilegedConnection

@Composable
fun DumpsysScreen(
    modifier: Modifier = Modifier,
    navigationViewModel: NavigationViewModel,
    privilegeViewModel: PrivilegeControlViewModel = hiltViewModel(),
    dumpsysViewModel: DumpsysViewModel = hiltViewModel(),
    onServiceClick: (String) -> Unit
) {
    val query by dumpsysViewModel.query.collectAsState()
    val services by dumpsysViewModel.filteredServices.collectAsState()
    val isConnected by privilegeViewModel.isConnected.collectAsState()

    LaunchedEffect(Unit) {
        navigationViewModel.setTitle("Dumpsys")
    }

    LaunchedEffect(isConnected) {
        if (isConnected) {
            dumpsysViewModel.loadServices()
        }
    }

    RequirePrivilegedConnection(
        isConnected = isConnected,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = dumpsysViewModel::setQuery,
                label = { Text(stringResource(R.string.search_service)) },
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            if (services.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.loading_services),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(services) { service ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .clickable { onServiceClick(service) }
                                    .padding(horizontal = 8.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = service,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
            }
        }
    }
}
