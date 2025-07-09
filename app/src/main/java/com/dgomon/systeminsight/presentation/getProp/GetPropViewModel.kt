package com.dgomon.systeminsight.presentation.getProp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dgomon.systeminsight.core.share.ShareManager
import com.dgomon.systeminsight.data.shell.ShellCommandExecutor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetPropViewModel @Inject constructor(
    private val executor: ShellCommandExecutor,
    private val shareManager: ShareManager
) : ViewModel() {
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    var isSearchActive by mutableStateOf(false)
        private set

    fun toggleSearchMode() {
        isSearchActive = !isSearchActive
    }


    private lateinit var listOfProps: List<String>

    // Combine query and props to emit filtered results
    private val _props = MutableStateFlow<List<PropEntry>>(emptyList())
    val filteredProps: StateFlow<List<PropEntry>> = combine(_query, _props) { query, props ->
        if (query.isBlank()) props
        else props.filter { it.key.contains(query, ignoreCase = true) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setQuery(newQuery: String) {
        _query.value = newQuery
    }

    init {
        loadProps()
    }

    fun shareOutput() {
        shareManager.shareAsFile(listOfProps.joinToString(
            System.lineSeparator()), "props.txt")
    }

    private fun loadProps() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                listOfProps = executor.runCommand("getprop")

                _props.value = listOfProps.mapNotNull { line ->
                    val match = Regex("""\[(.+?)]: \[(.+?)]""").matchEntire(line)
                    match?.let {
                        val (key, value) = it.destructured
                        PropEntry(key, value)
                    }
                }

            } catch (e: Exception) {
                _props.value = listOf(PropEntry("Error Getting Props", e.message ?: ""))
            }
        }
    }

}