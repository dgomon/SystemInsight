package com.dgomon.systeminsight.presentation.logcat

import android.os.RemoteException
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dgomon.systeminsight.core.service.PrivilegedServiceConnectionProvider
import com.dgomon.systeminsight.core.share.ShareManager
import com.dgomon.systeminsight.service.ILogCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

enum class LogcatState {
    Init,
    Idle,
    Collecting,
}

@HiltViewModel
class LogcatViewModel @Inject constructor(
    private val serviceConnectionProvider: PrivilegedServiceConnectionProvider,
    private val shareManager: ShareManager
) : ViewModel() {

    companion object {
        private const val LOG_BUFFER_CAPACITY = 20000
        private const val LOG_DISPLAY_CAPACITY = 100
    }

    // aggregates logs internally in the ViewModel
    private val _logBuffer = ArrayDeque<String>(LOG_BUFFER_CAPACITY)

    // expose the logs to the LogcatScreen
    private val _logLines = MutableStateFlow<List<String>>(emptyList())

    private val logMutex = Mutex()
    val isConnected: StateFlow<Boolean> = serviceConnectionProvider.isConnected

    private val _logcatState = MutableStateFlow(LogcatState.Init)
    val logcatState: StateFlow<LogcatState> = _logcatState.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _isScreenVisible = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            combine(isConnected, _isScreenVisible, _logcatState) {
                                                           connected, visible, state ->
                connected && visible && (state in setOf(LogcatState.Init, LogcatState.Collecting) )
            }.distinctUntilChanged().collect { shouldCapture ->
                if (shouldCapture) {
                    resumeCapture()
                } else {
                    pauseCapture()
                }
            }
        }
    }

    // Combine query and services to emit filtered results
    val filteredLogLines: StateFlow<List<String>> = combine(_query, _logLines) { query, logLine ->
        if (query.isBlank()) logLine
        else logLine.filter { it.contains(query, ignoreCase = true) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val logCallback = object : ILogCallback.Stub() {
        override fun onLogLine(line: String) {
            if (_logcatState.value == LogcatState.Collecting) {
                viewModelScope.launch {
                    appendToBuffer(line)
                }
            }
        }
    }

    fun onScreenVisible(visible: Boolean) {
        _isScreenVisible.value = visible
    }

    fun onResumeCapture() {
        _logcatState.value = LogcatState.Collecting
    }

    fun onPauseCapture() {
        _logcatState.value = LogcatState.Idle
    }

    fun setQuery(newQuery: String) {
        _query.value = newQuery
    }
    fun resumeCapture() {
        try {
            serviceConnectionProvider.getService()?.startLogging(logCallback)
        } catch (e: RemoteException) {
            Log.e("LogViewModel", "Failed to start logging", e)
        }
    }

    fun pauseCapture() {
        try {
            serviceConnectionProvider.getService()?.stopLogging()
        } catch (e: RemoteException) {
            Log.e("LogViewModel", "Failed to stop logging", e)
        }
    }

    fun clear() {
        _logBuffer.clear()
        _logLines.value = emptyList()
    }

    fun shareOutput() {
        shareManager.shareAsFile(_logBuffer.toList().joinToString(
            System.lineSeparator()), "log_file.txt")
    }

    private suspend fun appendToBuffer(line: String) {
        logMutex.withLock {
            if (_logBuffer.size >= LOG_BUFFER_CAPACITY) {
                _logBuffer.removeFirst()
            }
            _logBuffer.addLast(line)

            // Update _logLines with only the latest LOG_DISPLAY_CAPACITY entries
            val start = (_logBuffer.size - LOG_DISPLAY_CAPACITY).coerceAtLeast(0)
            _logLines.value = _logBuffer.drop(start)
        }
    }

    override fun onCleared() {
        pauseCapture()
        super.onCleared()
    }
}
