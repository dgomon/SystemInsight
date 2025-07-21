package com.dgomon.systeminsight.core

import android.content.Context
import android.content.pm.ApplicationInfo
import com.dgomon.systeminsight.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DefaultAppInfoProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : AppInfoProvider {

    override val versionName: String
        get() = BuildConfig.VERSION_NAME

    override val versionCode: Int
        get() = BuildConfig.VERSION_CODE

    override val applicationId: String
        get() = context.packageName

    override val buildType: String
        get() = BuildConfig.BUILD_TYPE

    override val isDebuggable: Boolean
        get() = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
}
