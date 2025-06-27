package com.dgomon.systeminsight.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.dgomon.systeminsight.presentation.dumpsys_details.DumpsysDetailsScreen
import com.dgomon.systeminsight.presentation.dumpsys.DumpsysScreen
import com.dgomon.systeminsight.presentation.getProp.GetPropScreen
import com.dgomon.systeminsight.presentation.logcat.LogcatScreen
import com.dgomon.systeminsight.presentation.privilege_control.PrivilegeControlScreen

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier,
             onServiceClick: (String) -> Unit) {
    NavHost(
        navController = navController,
        startDestination = Screen.PrivilegedControl.route,
        modifier = modifier
    ) {
        composable(Screen.PrivilegedControl.route) { PrivilegeControlScreen() }
        composable(Screen.Dumpsys.route) { DumpsysScreen(onServiceClick = onServiceClick) }
        composable(
            route = Screen.DumpsysDetails.route,
            arguments = listOf(navArgument("serviceName") { type = NavType.StringType })
        ) {
            DumpsysDetailsScreen()
        }
        composable(Screen.Logcat.route) { LogcatScreen() }
        composable(Screen.GetProp.route) { GetPropScreen() }
    }
}
