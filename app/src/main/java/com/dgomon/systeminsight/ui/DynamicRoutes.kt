package com.dgomon.systeminsight.ui

import android.net.Uri

object DynamicRoutes {
    const val DumpsysDetailsBase = "dumpsysDetails"
    const val DumpsysDetailsWithArg = "$DumpsysDetailsBase/{serviceName}"

    fun buildDumpsysDetailsRoute(serviceName: String): String {
        return "$DumpsysDetailsBase/${Uri.encode(serviceName)}"
    }
}