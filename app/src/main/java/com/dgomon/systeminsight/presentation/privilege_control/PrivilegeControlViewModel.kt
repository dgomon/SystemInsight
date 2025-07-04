package com.dgomon.systeminsight.presentation.privilege_control

import androidx.lifecycle.ViewModel
import com.dgomon.systeminsight.core.service.PrivilegedServiceConnectionProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class PrivilegeControlViewModel @Inject constructor(
    private val serviceConnectionProvider: PrivilegedServiceConnectionProvider) : ViewModel() {

    val isConnected: StateFlow<Boolean> = serviceConnectionProvider.isConnected

    fun requestPrivileges() {
        serviceConnectionProvider.requestPrivileges()
    }

    fun releasePrivileges() {
        serviceConnectionProvider.releasePrivileges()
    }

    override fun onCleared() {
        releasePrivileges()
        super.onCleared()
    }
}
