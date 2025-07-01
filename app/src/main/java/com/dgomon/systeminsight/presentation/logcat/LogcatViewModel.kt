package com.dgomon.systeminsight.presentation.logcat

import android.os.RemoteException
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dgomon.systeminsight.service.ILogCallback
import com.dgomon.systeminsight.service.PrivilegedServiceConnectionProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogcatViewModel @Inject constructor(
    private val serviceConnectionProvider: PrivilegedServiceConnectionProvider) : ViewModel() {

    private val logChannel = MutableSharedFlow<String>(replay = 0)
    val logs: SharedFlow<String> = logChannel.asSharedFlow()

    private val _isCapturing = MutableStateFlow(false)
    val isCapturing: StateFlow<Boolean> = _isCapturing.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused.asStateFlow()

    val isConnected: StateFlow<Boolean> = serviceConnectionProvider.isConnected

    private val logCallback = object : ILogCallback.Stub() {
        override fun onLogLine(line: String) {
            if (!_isPaused.value) {
                viewModelScope.launch {
                    logChannel.emit(line)
                }
            }
        }
    }

    fun startCapture() {
        try {
            serviceConnectionProvider.getService()?.startLogging(logCallback)
            _isCapturing.value = true
        } catch (e: RemoteException) {
            Log.e("LogViewModel", "Failed to start logging", e)
        }
    }

    fun stopCapture() {
        try {
            serviceConnectionProvider.getService()?.stopLogging()
            _isCapturing.value = false
        } catch (e: RemoteException) {
            Log.e("LogViewModel", "Failed to stop logging", e)
        }
    }

    fun pauseCapture() {
        _isPaused.value = true
    }

    fun resumeCapture() {
        _isPaused.value = false
    }

    override fun onCleared() {
        stopCapture()
        super.onCleared()
    }
}
