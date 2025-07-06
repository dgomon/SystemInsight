package com.dgomon.systeminsight.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dgomon.systeminsight.R
import com.dgomon.systeminsight.presentation.dumpsys.DumpsysScreen
import com.dgomon.systeminsight.presentation.dumpsys_details.DumpsysDetailsScreen
import com.dgomon.systeminsight.presentation.dumpsys_details.DumpsysDetailsViewModel
import com.dgomon.systeminsight.presentation.getProp.GetPropScreen
import com.dgomon.systeminsight.presentation.logcat.LogcatScreen
import com.dgomon.systeminsight.presentation.privilege_control.PrivilegeControlScreen


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
    navController: NavHostController,
    startDestination: Destination,
    modifier: Modifier = Modifier,
    navigationViewModel: NavigationViewModel = hiltViewModel()
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
                    Destination.PRIVILEGE_CONTROL -> PrivilegeControlScreen(modifier, navigationViewModel = navigationViewModel)
                    Destination.LOGCAT -> LogcatScreen(modifier, navigationViewModel = navigationViewModel)
                    Destination.GETPROP -> GetPropScreen(modifier, navigationViewModel = navigationViewModel)
                    Destination.DUMPSYS -> DumpsysScreen(
                        modifier = modifier,
                        onServiceClick = { serviceName ->
                            navController.navigate(DynamicRoutes.buildDumpsysDetailsRoute(serviceName))
                        },
                        navigationViewModel = navigationViewModel
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
            DumpsysDetailsScreen(serviceName = serviceName, modifier = modifier)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigationBar(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val startDestination = Destination.DUMPSYS
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    val navigationViewModel: NavigationViewModel = hiltViewModel()
    val title by navigationViewModel.title.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(title) },
            )
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
        floatingActionButton = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            if (currentRoute?.startsWith(DynamicRoutes.DumpsysDetailsBase) == true) {
                navBackStackEntry?.let { entry ->
                    // Use entry as ViewModelStoreOwner
                    val dumpsysDetailsViewModel: DumpsysDetailsViewModel = hiltViewModel(entry)

                    FloatingActionButton(onClick = { dumpsysDetailsViewModel.shareOutput() }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                }
            }
        }
    ) { contentPadding ->
        AppNavHost(navController,
            startDestination,
            Modifier.padding(contentPadding),
            navigationViewModel = navigationViewModel,
        )
    }
}

