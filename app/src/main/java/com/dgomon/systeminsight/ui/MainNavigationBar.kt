package com.dgomon.systeminsight.ui

import Destination
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import bottomBarDestinations
import com.dgomon.systeminsight.presentation.dumpsys.DumpsysTopBar
import com.dgomon.systeminsight.presentation.dumpsys_details.DumpsysDetailsTopBar
import com.dgomon.systeminsight.presentation.dumpsys_details.DumpsysDetailsViewModel
import com.dgomon.systeminsight.presentation.getProp.GetPropTopBar
import com.dgomon.systeminsight.presentation.logcat.LogcatTopBar
import com.dgomon.systeminsight.presentation.scaffold.DynamicRoutes
import com.dgomon.systeminsight.presentation.settings.SettingsTopBar
import com.dgomon.systeminsight.ui.navigation.AppNavHost
import com.dgomon.systeminsight.ui.navigation.matchesRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigationBar() {
    val navController = rememberNavController()
    val startDestination = Destination.Logcat

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val topBarContent = when {
        currentRoute == Destination.Logcat.route -> {
            @Composable { LogcatTopBar(
                navBackStackEntry = navBackStackEntry!!,
                navController = navController
            ) }
        }
        currentRoute == Destination.Getprop.route -> { { GetPropTopBar(navController) } }
        currentRoute == Destination.Dumpsys.route -> { { DumpsysTopBar(navController) } }
        currentRoute?.matchesRoute(DynamicRoutes.DumpsysDetailsWithArg) == true -> {
            {
                val viewModel = hiltViewModel<DumpsysDetailsViewModel>(navBackStackEntry!!)
                DumpsysDetailsTopBar(navController, viewModel)
            }  // or pass args if needed
        }
        currentRoute == Destination.Settings.route -> { { SettingsTopBar(navController) } }

        else -> null
    }

    var fabContent by remember { mutableStateOf<@Composable () -> Unit>({}) }
    val shouldShowBottomBar = currentRoute in bottomBarDestinations.map { it.route }

    Scaffold(
        topBar = {
            topBarContent?.invoke() ?: TopAppBar(title = { Text("Default") })
        },
        bottomBar = {
            if (shouldShowBottomBar) {
                NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                    bottomBarDestinations.forEach { destination ->
                        NavigationBarItem(
                            selected = currentRoute == destination.route,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
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
            }
        },
        floatingActionButton = { fabContent() }, // delegated to screen
    ) { contentPadding ->
        AppNavHost(
            navController,
            startDestination,
            Modifier.padding(contentPadding),
        )
    }
}
