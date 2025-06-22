package com.dgomon.systeminsight.presentation.GetProp

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
class GetPropViewModel @Inject constructor() : ViewModel() {

    private val _props = MutableStateFlow<List<String>>(emptyList())
    val props: StateFlow<List<String>> = _props

    init {
        getProps()
    }

    private fun getProps() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val process = Runtime.getRuntime().exec("getprop")
                val reader = BufferedReader(InputStreamReader(process.inputStream))

                val buffer = mutableListOf<String>()

                while (true) {
                    val line = reader.readLine() ?: break
                    buffer.add(line)
                    if (buffer.size > 1000) buffer.removeAt(0) // Limit memory usage
                    _props.value = buffer.toList()
                }

            } catch (e: Exception) {
                _props.value = listOf("Error reading logcat: ${e.message}")
            }
        }
    }

}