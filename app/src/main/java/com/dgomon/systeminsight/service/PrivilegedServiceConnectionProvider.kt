package com.dgomon.systeminsight.service

import kotlinx.coroutines.flow.StateFlow


interface PrivilegedServiceConnectionProvider {
    val isConnected: StateFlow<Boolean>
    fun getService(): IPrivilegedCommandService?
    fun requestPrivileges()
    fun releasePrivileges()
}
