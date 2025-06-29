package com.dgomon.systeminsight.presentation.logcat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import rikka.shizuku.Shizuku
import javax.inject.Inject

@HiltViewModel
class LogcatViewModel @Inject constructor() : ViewModel() {

    private var logcatProcess: Process? = null
    private var logcatJob: Job? = null
    private val _logs = MutableStateFlow<List<String>>(emptyList())
    val logs: StateFlow<List<String>> = _logs.asStateFlow()

//    fun startLogging() {
//        if (logcatJob != null) return  // Already running
//
//        logcatJob = viewModelScope.launch(Dispatchers.IO) {
//            try {
//                val process = Shizuku.newProcess(
//                    arrayOf("logcat", "-v", "time"), null, null
//                )
//                logcatProcess = process
//                val reader = process.inputStream.bufferedReader()
//
//                reader.lineSequence().forEach { line ->
//                    _logs.update { it + line }
//                }
//
//            } catch (e: Exception) {
//                _logs.update { it + "[Error] ${e.message}" }
//            }
//        }
//    }

    fun stopLogging() {
        logcatJob?.cancel()
        logcatJob = null
        logcatProcess?.destroy()
        logcatProcess = null
    }

    override fun onCleared() {
        stopLogging()
        super.onCleared()
    }
}
