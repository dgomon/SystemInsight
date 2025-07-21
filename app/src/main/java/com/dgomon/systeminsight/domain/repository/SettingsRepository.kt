package com.dgomon.systeminsight.domain.repository

import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    val logBufferSize: StateFlow<Int>
    suspend fun setLogBufferSize(value: Int)
}
