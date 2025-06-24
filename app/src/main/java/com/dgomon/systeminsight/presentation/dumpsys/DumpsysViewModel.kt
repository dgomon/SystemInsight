package com.dgomon.systeminsight.presentation.dumpsys

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
class DumpsysViewModel @Inject constructor(
    private val executor: ShellCommandExecutor
) : ViewModel() {

    private val _lines = MutableStateFlow<List<String>>(emptyList())
    val lines: StateFlow<List<String>> = _lines

    init {
        dumpsys()
    }

    private fun dumpsys() {
        viewModelScope.launch(Dispatchers.IO) {
            _lines.value = executor.runCommand("dumpsys battery")
        }
    }
}
