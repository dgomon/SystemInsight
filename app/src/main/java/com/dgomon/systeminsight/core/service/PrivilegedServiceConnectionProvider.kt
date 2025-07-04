package com.dgomon.systeminsight.core.service

import com.dgomon.systeminsight.service.IPrivilegedCommandService
import kotlinx.coroutines.flow.StateFlow


interface PrivilegedServiceConnectionProvider {
    val isConnected: StateFlow<Boolean>
    fun getService(): IPrivilegedCommandService?
    fun requestPrivileges()
    fun releasePrivileges()
}
