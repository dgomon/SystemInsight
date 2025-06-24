package com.dgomon.systeminsight.presentation.dumpsysdetails

import androidx.lifecycle.SavedStateHandle
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
class DumpsysDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val executor: ShellCommandExecutor
) : ViewModel() {

    private val _serviceOutput = MutableStateFlow<List<String>>(emptyList())
    val serviceOutput: StateFlow<List<String>> = _serviceOutput

    private val serviceName: String = checkNotNull(savedStateHandle["serviceName"])

    init {
        loadServiceDetail()
    }

    private fun loadServiceDetail() {
        viewModelScope.launch(Dispatchers.IO) {
            _serviceOutput.value = listOf("Loading $serviceName...")
            val output = executor.runCommand("dumpsys $serviceName")
            _serviceOutput.value = output
        }
    }
}
