package com.dgomon.systeminsight.ui

sealed class Screen(val title: String, val route: String) {
    object Logcat : Screen("Logcat", "logcat")
    object GetProp : Screen("GetProp", "getprop")
}
