package com.dgomon.systeminsight.presentation.getProp

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
class GetPropViewModel @Inject constructor(
    private val executor: ShellCommandExecutor
) : ViewModel() {

    private val _props = MutableStateFlow<List<PropEntry>>(emptyList())
    val props: StateFlow<List<PropEntry>> = _props

    init {
        loadProps()
    }

    private fun loadProps() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val buffer = executor.runCommand("getprop")

                _props.value = buffer.mapNotNull { line ->
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