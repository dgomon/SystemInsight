package com.dgomon.systeminsight.presentation.dumpsys_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dgomon.systeminsight.core.service.CommandServiceClient
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
    private val commandServiceClient: CommandServiceClient,
) : ViewModel() {

    private val _serviceOutput = MutableStateFlow("")
    val serviceOutput: StateFlow<String> = _serviceOutput

    private val serviceName = URLDecoder.decode(
        checkNotNull(savedStateHandle["serviceName"]),
        StandardCharsets.UTF_8.toString()
    )

    init {
        loadServiceDetail()
    }

    private fun loadServiceDetail() {
        viewModelScope.launch(Dispatchers.IO) {
            _serviceOutput.value = "Loading $serviceName..."

            val output = commandServiceClient
                .runCommand("dumpsys $serviceName")
                ?: ""

            _serviceOutput.value = output
        }
    }
}
