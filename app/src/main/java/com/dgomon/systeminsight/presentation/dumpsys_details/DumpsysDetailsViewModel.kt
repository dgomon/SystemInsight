package com.dgomon.systeminsight.presentation.dumpsys_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dgomon.systeminsight.core.service.CommandServiceClient
import com.dgomon.systeminsight.core.share.ShareManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

@HiltViewModel
class DumpsysDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val commandServiceClient: CommandServiceClient,
    private val shareManager: ShareManager
) : ViewModel() {

    private val _serviceOutput = MutableStateFlow("")
    val serviceOutput: StateFlow<String> = _serviceOutput.asStateFlow()

    private val serviceName = URLDecoder.decode(
        checkNotNull(savedStateHandle["serviceName"]),
        StandardCharsets.UTF_8.toString()
    )

    init {
        loadServiceDetail()
    }

    fun shareOutput() {
        shareManager.shareAsFile(serviceOutput.value, "${serviceName}.txt")
    }

    private fun loadServiceDetail() {
        viewModelScope.launch(Dispatchers.IO) {
            val output = commandServiceClient
                .runCommand("dumpsys $serviceName")
                ?: ""

            _serviceOutput.value = output
        }
    }
}
