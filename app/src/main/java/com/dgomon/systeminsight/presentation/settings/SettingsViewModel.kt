package com.dgomon.systeminsight.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dgomon.systeminsight.core.AppInfoProvider
import com.dgomon.systeminsight.core.service.PrivilegedServiceConnectionProvider
import com.dgomon.systeminsight.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val serviceConnectionProvider: PrivilegedServiceConnectionProvider,
    private val appInfoProvider: AppInfoProvider,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val isConnected: StateFlow<Boolean> = serviceConnectionProvider.isConnected
    val logBufferSize: StateFlow<Int> = settingsRepository.logBufferSize
    val version = appInfoProvider.versionName
    val versionCode = appInfoProvider.versionCode
    val logBufferSizeRange = 100..10_000


    fun requestPrivileges() {
        serviceConnectionProvider.requestPrivileges()
    }

    fun releasePrivileges() {
        serviceConnectionProvider.releasePrivileges()
    }

    fun setLogBufferSize(size: Int) {
        viewModelScope.launch {
            settingsRepository.setLogBufferSize(size)
        }
    }

    fun isValidLogBufferSize(value: Int?): Boolean {
        return value in logBufferSizeRange
    }
}
