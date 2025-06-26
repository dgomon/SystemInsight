package com.dgomon.systeminsight.presentation.logcat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dgomon.systeminsight.data.shell.ShellCommandExecutor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogcatViewModel @Inject constructor(
    private val executor: ShellCommandExecutor
) : ViewModel() {

    private val _logLines = MutableStateFlow<List<String>>(emptyList())
    val logLines: StateFlow<List<String>> = _logLines

    init {
        startLogcat()
    }

    private fun startLogcat() {
        viewModelScope.launch(Dispatchers.IO) {
            _logLines.value = executor.runCommand("logcat")
        }
    }

    override fun onCleared() {
        super.onCleared()
        Runtime.getRuntime().exec("killall logcat")
    }
}
