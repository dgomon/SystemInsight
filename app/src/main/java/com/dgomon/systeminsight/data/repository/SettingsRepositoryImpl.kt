package com.dgomon.systeminsight.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.dgomon.systeminsight.data.SettingsKeys.LOG_BUFFER_SIZE_KEY
import com.dgomon.systeminsight.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(private val dataStore: DataStore<Preferences>):
    SettingsRepository {
    private val _logBufferSize = MutableStateFlow(DEFAULT_LOG_BUFFER_SIZE)
    override val logBufferSize: StateFlow<Int> = _logBufferSize.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.data
                .map { prefs -> prefs[LOG_BUFFER_SIZE_KEY] ?: DEFAULT_LOG_BUFFER_SIZE }
                .collect { _logBufferSize.value = it }
        }
    }

    override suspend fun setLogBufferSize(value: Int) {
        _logBufferSize.value = value
        // persist to DataStore if needed
        dataStore.edit { prefs ->
            prefs[LOG_BUFFER_SIZE_KEY] = value
        }
    }

    companion object {
        const val DEFAULT_LOG_BUFFER_SIZE = 1000
    }
}
