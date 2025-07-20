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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

enum class LogcatState {
    Resumed,     // Logs arrive, callback called
    Paused,      // Logs arrive, callback not called
}

data class LogcatEntry(
    val date: String,            // e.g., "07-17"
    val time: String,            // e.g., "08:55:28.792"
    val pid: Int,                // e.g., 1305
    val tid: Int,                // e.g., 1305
    val logLevel: String,        // e.g., "I", "D", "E", etc.
    val tag: String,             // e.g., "WifiHAL"
    val message: String          // e.g., "In GetCachedScanResultsCommand::handleResponse"
)

val logcatRegex = Regex(
    """^(\d{2}-\d{2}) (\d{2}:\d{2}:\d{2}\.\d{3})\s+(\d+)\s+(\d+)\s+([VDIWEAF])\s+(\S+)\s*:\s+(.*)$"""
)

fun parseLogcatLine(line: String): LogcatEntry? {
    val match = logcatRegex.matchEntire(line) ?: return null
    val (date, time, pid, tid, level, tag, message) = match.destructured
    return LogcatEntry(
        date = date,
        time = time,
        pid = pid.toInt(),
        tid = tid.toInt(),
        logLevel = level,
        tag = tag,
        message = message.trim()
    )
}


@HiltViewModel
class LogcatViewModel @Inject constructor(
    private val serviceConnectionProvider: PrivilegedServiceConnectionProvider,
    private val shareManager: ShareManager
) : ViewModel() {
    companion object {
        private const val TAG = "LogViewModel"
        private const val LOG_BUFFER_CAPACITY = 20000
        private const val LOG_DISPLAY_CAPACITY = 100
    }

    // aggregates logs internally in the ViewModel
    private val _logBuffer = ArrayDeque<String>(LOG_BUFFER_CAPACITY)

    // expose the logs to the LogcatScreen
    private val _logLines = MutableStateFlow<List<String>>(emptyList())

    private val logMutex = Mutex()
    val isConnected: StateFlow<Boolean> = serviceConnectionProvider.isConnected

    private val _logcatState = MutableStateFlow(LogcatState.Paused)
    val logcatState: StateFlow<LogcatState> = _logcatState.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _isScreenCreated = MutableStateFlow(false)

    // Combine query and services to emit filtered results
    val filteredLogLines: StateFlow<List<String>> = combine(_query, _logLines) { query, logLine ->
        if (query.isBlank()) logLine
        else logLine.filter { it.contains(query, ignoreCase = true) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val parsedLogEntries: StateFlow<List<LogcatEntry>> = filteredLogLines
        .map { lines -> lines.mapNotNull { parseLogcatLine(it) } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val logCallback = object : ILogCallback.Stub() {
        override fun onLogLine(line: String) {
            if (_logcatState.value == LogcatState.Resumed) {
                viewModelScope.launch {
                    appendToBuffer(line)
                }
            }
        }
    }

    init {
        Log.d("LogcatViewModel", "init called: instance=${this.hashCode()}")

        viewModelScope.launch {
            isConnected
                .collect { connected ->
                if (connected) {
                    startCapture()
                } else {
                    Log.w(TAG, "Disconnected")
                }
            }
        }

        viewModelScope.launch {
            _isScreenCreated.collect { created ->
                if (created) {
                    resumeCapture()
                }
            }
        }
    }

    fun onScreenCreated(isCreated: Boolean) {
        _isScreenCreated.value = isCreated
    }

    fun resumeCapture() {
        _logcatState.value = LogcatState.Resumed
    }

    fun pauseCapture() {
        _logcatState.value = LogcatState.Paused
    }

    fun setQuery(newQuery: String) {
        _query.value = newQuery
    }

    fun startCapture() {
        try {
            serviceConnectionProvider.getService()?.startLogging(logCallback)
        } catch (e: RemoteException) {
            Log.e("LogViewModel", "Failed to start logging", e)
        }
    }

    fun stopCapture() {
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

    fun exportOutput() {
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
        stopCapture()
        super.onCleared()
    }
}
