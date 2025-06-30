package com.dgomon.systeminsight.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dgomon.systeminsight.presentation.dumpsys.DumpsysScreen
import com.dgomon.systeminsight.presentation.getProp.GetPropScreen
import com.dgomon.systeminsight.presentation.logcat.LogcatScreen
import com.dgomon.systeminsight.presentation.privilege_control.PrivilegeControlScreen


enum class Destination(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
) {
    PRIVILEGE_CONTROL("control", "Control", Icons.Default.Menu, "Control"),
    LOGCAT("logcat", "Logcat", Icons.Default.Menu, "Logcat"),
    GETPROP("getprop", "Getprop", Icons.Default.Menu, "Getprop"),
    DUMPSYS("dumpsys", "Dumpsys", Icons.Default.Menu, "Dumpsys"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Destination,
    modifier: Modifier = Modifier,
    onServiceClick: (String) -> Unit
) {
    NavHost(
        navController,
        startDestination = startDestination.route
    ) {
        Destination.entries.forEach { destination ->
            composable(destination.route) {
                when (destination) {
                    Destination.PRIVILEGE_CONTROL -> PrivilegeControlScreen()
                    Destination.LOGCAT -> LogcatScreen()
                    Destination.GETPROP -> GetPropScreen()
                    Destination.DUMPSYS -> DumpsysScreen(onServiceClick = onServiceClick)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigationBar(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val startDestination = Destination.PRIVILEGE_CONTROL
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    Scaffold(
        modifier = modifier,
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
                                destination.icon,
                                contentDescription = destination.contentDescription
                            )
                        },
                        label = { Text(destination.label) }
                    )
                }
            }
        }
    ) { contentPadding ->
        AppNavHost(navController,
            startDestination,
            Modifier.padding(contentPadding),
            onServiceClick = { serviceName -> {}})
    }
}

