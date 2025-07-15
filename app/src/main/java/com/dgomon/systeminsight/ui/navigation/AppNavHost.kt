package com.dgomon.systeminsight.ui.navigation

import Destination
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import bottomBarDestinations
import com.dgomon.systeminsight.presentation.dumpsys.DumpsysScreen
import com.dgomon.systeminsight.presentation.dumpsys_details.DumpsysDetailsScreen
import com.dgomon.systeminsight.presentation.getProp.GetPropScreen
import com.dgomon.systeminsight.presentation.logcat.LogcatScreen
import com.dgomon.systeminsight.presentation.privilege_control.SettingsScreen
import com.dgomon.systeminsight.presentation.scaffold.DynamicRoutes

fun String.matchesRoute(routePattern: String): Boolean {
    val regex = routePattern
        .replace("{serviceName}", "[^/]+")
        .toRegex()
    return this.matches(regex)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Destination,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination.route,
        modifier = modifier
    ) {
        // Static routes from Destination enum
        bottomBarDestinations.forEach { destination ->
            composable(destination.route) { backStackEntry ->

                when (destination) {
                    Destination.Logcat -> LogcatScreen(
                        navBackStackEntry = backStackEntry,
                        logcatViewModel = hiltViewModel(backStackEntry)
                    )
                    Destination.Getprop -> GetPropScreen()

                    Destination.Dumpsys -> DumpsysScreen { serviceName ->
                        navController.navigate(DynamicRoutes.buildDumpsysDetailsRoute(serviceName))
                    }

                    else -> {
                        // Handle other bottom bar destinations if needed
                    }
                }
            }
        }

        // Dynamic route for dumpsys details
        composable(
            route = DynamicRoutes.DumpsysDetailsWithArg,
            arguments = listOf(
                navArgument("serviceName") {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) { backStackEntry ->
            DumpsysDetailsScreen(
                dumpsysDetailsViewModel = hiltViewModel(backStackEntry),
            )
        }

        // Settings
        composable(
            route = Destination.Settings.route,
        ) {
            SettingsScreen()
        }
    }
}

