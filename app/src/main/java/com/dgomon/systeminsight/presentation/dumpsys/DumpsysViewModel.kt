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

    private val _services = MutableStateFlow<List<String>>(emptyList())
    val services: StateFlow<List<String>> = _services

    init {
        loadServices()
    }

    private fun loadServices() {
        viewModelScope.launch(Dispatchers.IO) {
            _services.value = executor.runCommand("dumpsys -l")
                .dropWhile { it.isBlank() || it.startsWith("Currently running services:") }
                .map (String::trim)
                .filter (String::isNotEmpty)
        }
    }
}
