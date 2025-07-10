package com.dgomon.systeminsight.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun RequirePrivilegedConnection(
    isConnected: Boolean,
    modifier: Modifier = Modifier,
    fallbackMessage: String = "Waiting for privileged connection...",
    content: @Composable () -> Unit
) {
    if (isConnected) {
        content()
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = fallbackMessage,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
