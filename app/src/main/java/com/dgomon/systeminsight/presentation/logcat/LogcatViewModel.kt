package com.dgomon.systeminsight.presentation.logcat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

@HiltViewModel
class LogcatViewModel @Inject constructor() : ViewModel() {

    private val _logLines = MutableStateFlow<List<String>>(emptyList())
    val logLines: StateFlow<List<String>> = _logLines

    init {
        startLogcat()
    }

    private fun startLogcat() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val process = Runtime.getRuntime().exec("su -c logcat")
                val reader = BufferedReader(InputStreamReader(process.inputStream))

                val buffer = mutableListOf<String>()

                while (true) {
                    val line = reader.readLine() ?: break
                    buffer.add(line)
                    if (buffer.size > 1000) buffer.removeAt(0) // Limit memory usage
                    _logLines.value = buffer.toList()
                }

            } catch (e: Exception) {
                _logLines.value = listOf("Error reading logcat: ${e.message}")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Runtime.getRuntime().exec("killall logcat") // optional cleanup (or retain a Process ref and destroy)
    }
}
