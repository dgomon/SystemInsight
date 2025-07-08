package com.dgomon.systeminsight.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.dgomon.systeminsight.R
import com.dgomon.systeminsight.presentation.dumpsys.DumpsysScreen
import com.dgomon.systeminsight.presentation.dumpsys_details.DumpsysDetailsScreen
import com.dgomon.systeminsight.presentation.getProp.GetPropScreen
import com.dgomon.systeminsight.presentation.logcat.LogcatScreen
import com.dgomon.systeminsight.presentation.navigation.NavigationViewModel
import com.dgomon.systeminsight.presentation.privilege_control.PrivilegeControlScreen
import com.dgomon.systeminsight.presentation.scaffold.AppScaffoldViewModel
import com.dgomon.systeminsight.presentation.scaffold.DynamicRoutes

enum class Destination(
    val route: String,
    val label: String,
    val iconResId: Int,
    val contentDescription: String
) {
    PRIVILEGE_CONTROL("control", "Control", R.drawable.ic_settings, "Control"),
    LOGCAT("logcat", "Logcat", R.drawable.ic_settings, "Logcat"),
    GETPROP("getprop", "Getprop", R.drawable.ic_settings, "Getprop"),
    DUMPSYS("dumpsys", "Dumpsys", R.drawable.ic_settings, "Dumpsys"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: Destination,
    navigationViewModel: NavigationViewModel = hiltViewModel(),
    scaffoldViewModel: AppScaffoldViewModel = hiltViewModel(),
    onFabChanged: ((@Composable () -> Unit) -> Unit),
) {
    NavHost(
        navController = navController,
        startDestination = startDestination.route,
        modifier = modifier
    ) {
        // Static routes from Destination enum
        Destination.entries.forEach { destination ->
            composable(destination.route) {
                when (destination) {
                    Destination.PRIVILEGE_CONTROL -> PrivilegeControlScreen(
                        modifier,
                        navigationViewModel = navigationViewModel,
                        onFabContent = onFabChanged
                    )
                    Destination.LOGCAT -> LogcatScreen(
                        modifier,
                        scaffoldViewModel = scaffoldViewModel,
                        onFabContent = onFabChanged
                    )
                    Destination.GETPROP -> GetPropScreen(
                        modifier,
                        navigationViewModel = navigationViewModel,
                        onFabContent = onFabChanged
                    )
                    Destination.DUMPSYS -> DumpsysScreen(
                        onServiceClick = { serviceName ->
                            navController.navigate(DynamicRoutes.buildDumpsysDetailsRoute(serviceName))
                        },
                        navigationViewModel = navigationViewModel,
                        onFabContent = onFabChanged
                    )
                }
            }
        }

        // Dynamic route for dumpsys details
        composable(
            route = DynamicRoutes.DumpsysDetailsWithArg,
            arguments = listOf(navArgument("serviceName") { type = NavType.StringType })
        ) { backStackEntry ->
            val serviceName = backStackEntry.arguments?.getString("serviceName") ?: ""
            DumpsysDetailsScreen(
                serviceName = serviceName,
                onFabContent = onFabChanged
            )
        }
    }
}

