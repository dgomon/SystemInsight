package com.dgomon.systeminsight.presentation.dumpsys

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
class DumpsysViewModel @Inject constructor() : ViewModel() {

    private val _lines = MutableStateFlow<List<String>>(emptyList())
    val lines: StateFlow<List<String>> = _lines

    init {
        dumpsys()
    }

    private fun dumpsys() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val process = Runtime.getRuntime().exec("dumpsys battery")
                val reader = BufferedReader(InputStreamReader(process.inputStream))

                val buffer = mutableListOf<String>()

                while (true) {
                    val line = reader.readLine() ?: break
                    buffer.add(line)
                    if (buffer.size > 1000) buffer.removeFirst() // Limit memory usage
                    _lines.value = buffer.toList()
                }

            } catch (e: Exception) {
                _lines.value = listOf("Error reading logcat: ${e.message}")
            }
        }
    }
}
