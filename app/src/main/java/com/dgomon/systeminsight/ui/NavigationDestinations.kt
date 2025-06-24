package com.dgomon.systeminsight.ui

sealed class Screen(val title: String, val route: String) {
    object Logcat : Screen("Logcat", "logcat")
    object GetProp : Screen("GetProp", "getprop")
    object Dumpsys : Screen("Dympsys", "dumpsys")
    object DumpsysDetails : Screen(title = "Dumpsys Details", route = "dumpsysDetails/{serviceName}") {
        fun createRoute(serviceName: String) = "dumpsysDetails/$serviceName"
    }
}
