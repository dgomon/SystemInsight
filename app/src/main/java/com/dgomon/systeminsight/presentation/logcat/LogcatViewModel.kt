package com.dgomon.systeminsight.presentation.logcat

import android.os.RemoteException
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dgomon.systeminsight.service.ILogCallback
import com.dgomon.systeminsight.service.PrivilegedServiceConnectionProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogcatViewModel @Inject constructor(
    private val serviceConnectionProvider: PrivilegedServiceConnectionProvider) : ViewModel() {

    private val logChannel = MutableSharedFlow<String>(replay = 0)
    val logs: SharedFlow<String> = logChannel.asSharedFlow()

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
        } catch (e: RemoteException) {
            Log.e("LogViewModel", "Failed to start logging", e)
        }
    }

    fun stopLogging() {
        try {
            serviceConnectionProvider.getService()?.stopLogging()
        } catch (e: RemoteException) {
            Log.e("LogViewModel", "Failed to stop logging", e)
        }
    }

    override fun onCleared() {
        stopLogging()
        super.onCleared()
    }
}
