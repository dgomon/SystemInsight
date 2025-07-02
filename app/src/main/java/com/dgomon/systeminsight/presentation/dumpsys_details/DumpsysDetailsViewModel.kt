package com.dgomon.systeminsight.presentation.dumpsys_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dgomon.systeminsight.service.PrivilegedServiceConnectionProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

@HiltViewModel
class DumpsysDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val serviceConnectionProvider: PrivilegedServiceConnectionProvider,
) : ViewModel() {

    private val _serviceOutput = MutableStateFlow<List<String>>(emptyList())
    val serviceOutput: StateFlow<List<String>> = _serviceOutput

    private val serviceName = URLDecoder.decode(
        checkNotNull(savedStateHandle["serviceName"]),
        StandardCharsets.UTF_8.toString()
    )

    init {
        loadServiceDetail()
    }

    private fun loadServiceDetail() {
        viewModelScope.launch(Dispatchers.IO) {
            _serviceOutput.value = listOf("Loading $serviceName...")

            val output = serviceConnectionProvider.getService()
                ?.runCommand("dumpsys $serviceName")

            _serviceOutput.value = listOf(output ?: "")
        }
    }
}
