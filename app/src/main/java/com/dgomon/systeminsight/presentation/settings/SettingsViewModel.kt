package com.dgomon.systeminsight.presentation.settings

import androidx.lifecycle.ViewModel
import com.dgomon.systeminsight.core.service.PrivilegedServiceConnectionProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val serviceConnectionProvider: PrivilegedServiceConnectionProvider) : ViewModel() {

    val isConnected: StateFlow<Boolean> = serviceConnectionProvider.isConnected

    fun requestPrivileges() {
        serviceConnectionProvider.requestPrivileges()
    }

    fun releasePrivileges() {
        serviceConnectionProvider.releasePrivileges()
    }
}
