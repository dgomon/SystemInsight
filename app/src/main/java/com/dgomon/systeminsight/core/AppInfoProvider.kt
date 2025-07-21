package com.dgomon.systeminsight.core

interface AppInfoProvider {
    val versionName: String
    val versionCode: Int
    val applicationId: String
    val buildType: String
    val isDebuggable: Boolean
}
