package com.dgomon.systeminsight.presentation.dumpsys

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dgomon.systeminsight.core.service.CommandServiceClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DumpsysViewModel @Inject constructor(
    private val commandServiceClient: CommandServiceClient,
) : ViewModel() {
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _services = MutableStateFlow<List<String>>(emptyList())

    // Combine query and services to emit filtered results
    val filteredServices: StateFlow<List<String>> = combine(_query, _services) { query, services ->
        if (query.isBlank()) services
        else services.filter { it.contains(query, ignoreCase = true) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setQuery(newQuery: String) {
        _query.value = newQuery
    }

    fun loadServices() {
        _services.value = commandServiceClient.runCommand("dumpsys -l")
            ?.lineSequence()
            ?.filter { it.isNotBlank() }
            ?.drop(1) // Skip the "Currently running services" header
            ?.map { it.trim() }
            ?.toList()
            ?: emptyList()
    }
}
