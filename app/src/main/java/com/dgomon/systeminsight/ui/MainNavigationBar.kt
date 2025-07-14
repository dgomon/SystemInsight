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
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dgomon.systeminsight.presentation.dumpsys.DumpsysTopBar
import com.dgomon.systeminsight.presentation.dumpsys_details.DumpsysDetailsTopBar
import com.dgomon.systeminsight.presentation.dumpsys_details.DumpsysDetailsViewModel
import com.dgomon.systeminsight.presentation.getProp.GetPropTopBar
import com.dgomon.systeminsight.presentation.logcat.LogcatTopBar
import com.dgomon.systeminsight.presentation.privilege_control.PrivilegeControlTopBar
import com.dgomon.systeminsight.presentation.scaffold.DynamicRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigationBar() {
    val navController = rememberNavController()
    val startDestination = Destination.LOGCAT
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val topBarContent = when {
//        currentRoute == Destination.PRIVILEGE_CONTROL.route -> { @Composable { PrivilegeControlTopBar() } }
        currentRoute == Destination.LOGCAT.route -> { @Composable { LogcatTopBar(navBackStackEntry = navBackStackEntry!!) } }
        currentRoute == Destination.GETPROP.route -> { { GetPropTopBar() } }
        currentRoute == Destination.DUMPSYS.route -> { { DumpsysTopBar() } }
        currentRoute?.matchesRoute(DynamicRoutes.DumpsysDetailsWithArg) == true -> {
            {
                val viewModel = hiltViewModel<DumpsysDetailsViewModel>(navBackStackEntry!!)
                DumpsysDetailsTopBar(navController, viewModel)
            }  // or pass args if needed
        }

        else -> null
    }

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
                            navController.navigate(route = destination.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
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
            navController,
            startDestination,
            Modifier.padding(contentPadding),
        )
    }
}
