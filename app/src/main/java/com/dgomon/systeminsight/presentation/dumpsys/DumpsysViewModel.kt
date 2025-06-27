package com.dgomon.systeminsight.presentation.dumpsys

import androidx.lifecycle.ViewModel
import com.dgomon.systeminsight.service.PrivilegedServiceConnectionProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class DumpsysViewModel @Inject constructor(
    private val serviceConnectionProvider: PrivilegedServiceConnectionProvider
) : ViewModel() {

    private val _services = MutableStateFlow<List<String>>(emptyList())
    val services: StateFlow<List<String>> = _services

    init {
        loadServices()
    }

    private fun loadServices() {
        _services.value = serviceConnectionProvider.getService()
            ?.runCommand("dumpsys -l")
            ?.lineSequence()
            ?.filter { it.isNotBlank() }
            ?.toList()
            ?: emptyList()
    }
}
