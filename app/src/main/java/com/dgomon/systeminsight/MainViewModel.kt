package com.dgomon.systeminsight

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dgomon.systeminsight.core.service.PrivilegedServiceConnectionProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val connectionProvider: PrivilegedServiceConnectionProvider
) : ViewModel() {

    private var hasAttemptedConnection = false

    fun ensureConnected() {
        if (hasAttemptedConnection) return
        hasAttemptedConnection = true

        viewModelScope.launch {
            runCatching {
                connectionProvider.requestPrivileges()
            }.onFailure {
                Log.w("MainViewModel", "Failed to connect to privileged service", it)
            }
        }
    }
}
