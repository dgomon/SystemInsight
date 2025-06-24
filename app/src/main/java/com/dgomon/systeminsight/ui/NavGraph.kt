package com.dgomon.systeminsight.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.dgomon.systeminsight.presentation.dumpsysdetails.DumpsysDetailsScreen
import com.dgomon.systeminsight.presentation.dumpsys.DumpsysScreen
import com.dgomon.systeminsight.presentation.getProp.GetPropScreen
import com.dgomon.systeminsight.presentation.logcat.LogcatScreen

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier,
             onServiceClick: (String) -> Unit) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dumpsys.route,
        modifier = modifier
    ) {
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
