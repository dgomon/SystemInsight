package com.dgomon.systeminsight.ui

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Screen(val title: String, val route: String) {
    object Logcat : Screen("Logcat", "logcat")
    object GetProp : Screen("GetProp", "getprop")
    object Dumpsys : Screen("Dympsys", "dumpsys")
    object DumpsysDetails : Screen(title = "Dumpsys Details", route = "dumpsysDetails/{serviceName}") {
        fun createRoute(serviceName: String): String {
            val encoded = URLEncoder.encode(serviceName, StandardCharsets.UTF_8.toString())
            return "dumpsysDetails/$encoded"
        }
    }
}
