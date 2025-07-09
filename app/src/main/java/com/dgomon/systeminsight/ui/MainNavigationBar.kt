package com.dgomon.systeminsight.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.dgomon.systeminsight.presentation.navigation.NavigationViewModel
import com.dgomon.systeminsight.presentation.scaffold.AppScaffoldViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigationBar(
    navigationViewModel: NavigationViewModel = hiltViewModel(),
    scaffoldViewModel: AppScaffoldViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val startDestination = Destination.DUMPSYS
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    val topBarContent by scaffoldViewModel.topBarContent
    var fabContent by remember { mutableStateOf<@Composable () -> Unit>({}) }

    Scaffold(
        topBar = {
            topBarContent?.invoke() ?: TopAppBar(title = { Text("Default") })
        },
        bottomBar = {
            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                Destination.entries.forEachIndexed { index, destination ->
                    NavigationBarItem(
                        selected = selectedDestination == index,
                        onClick = {
                            navController.navigate(route = destination.route)
                            selectedDestination = index
                        },
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = destination.iconResId),
                                contentDescription = destination.contentDescription
                            )
                        },
                        label = { Text(destination.label) }
                    )
                }
            }
        },
        floatingActionButton = { fabContent() }, // delegated to screen
    ) { contentPadding ->
        AppNavHost(
            Modifier.padding(contentPadding),
            navController,
            startDestination,
            navigationViewModel = navigationViewModel,
            scaffoldViewModel = scaffoldViewModel,
            onFabChanged = { fabContent = it },
        )
    }
}
