package com.dgomon.systeminsight.presentation.logcat

import android.os.RemoteException
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dgomon.systeminsight.domain.shizuku.ShizukuStatus
import com.dgomon.systeminsight.domain.shizuku.ShizukuStatus.Connected
import com.dgomon.systeminsight.domain.shizuku.ShizukuStatus.Disconnected
import com.dgomon.systeminsight.service.ILogCallback
import com.dgomon.systeminsight.service.PrivilegedServiceConnectionProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogcatViewModel @Inject constructor(
    private val serviceConnectionProvider: PrivilegedServiceConnectionProvider) : ViewModel() {

    private val logChannel = MutableSharedFlow<String>(replay = 0)
    val logs: SharedFlow<String> = logChannel.asSharedFlow()

    private val _isLogging = MutableStateFlow(false)
    val isLogging: StateFlow<Boolean> = _isLogging.asStateFlow()

    val isConnected: StateFlow<Boolean> = serviceConnectionProvider.isConnected

    private val logCallback = object : ILogCallback.Stub() {
        override fun onLogLine(line: String) {
            viewModelScope.launch {
                logChannel.emit(line)
            }
        }
    }

    fun startLogging() {
        try {
            serviceConnectionProvider.getService()?.startLogging(logCallback)
            _isLogging.value = true
        } catch (e: RemoteException) {
            Log.e("LogViewModel", "Failed to start logging", e)
        }
    }

    fun stopLogging() {
        try {
            serviceConnectionProvider.getService()?.stopLogging()
            _isLogging.value = false
        } catch (e: RemoteException) {
            Log.e("LogViewModel", "Failed to stop logging", e)
        }
    }

    override fun onCleared() {
        stopLogging()
        super.onCleared()
    }
}
