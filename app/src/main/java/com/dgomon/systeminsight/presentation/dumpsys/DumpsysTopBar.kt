package com.dgomon.systeminsight.presentation.dumpsys

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dgomon.systeminsight.R
import com.dgomon.systeminsight.presentation.privilege_control.PrivilegeControlViewModel
import com.dgomon.systeminsight.ui.common.CommonTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DumpsysTopBar(
    navController: NavController,
    privilegeViewModel: PrivilegeControlViewModel = hiltViewModel(),
    dumpsysViewModel: DumpsysViewModel = hiltViewModel()
) {
    val query by dumpsysViewModel.query.collectAsState()
    val isConnected by privilegeViewModel.isConnected.collectAsState()

    CommonTopBar(
        title = {
            if (isConnected) {
                OutlinedTextField(
                    value = query,
                    onValueChange = dumpsysViewModel::setQuery,
                    placeholder = { Text(stringResource(R.string.search_service)) },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { dumpsysViewModel.setQuery("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 16.dp)
                )
            }
            else {
                Text(stringResource(R.string.title_dumpsys))
            }
        },
        showMenu = true,
        menuItems = listOf(
            stringResource(R.string.settings) to {
                navController.navigate(Destination.Settings.route)
            }
        ),
    )
}