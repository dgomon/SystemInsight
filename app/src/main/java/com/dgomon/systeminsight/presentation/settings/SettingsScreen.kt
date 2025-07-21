package com.dgomon.systeminsight.presentation.settings

import android.R.string.cancel
import android.R.string.ok
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dgomon.systeminsight.R

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = hiltViewModel(),
) {
    val isConnected by settingsViewModel.isConnected.collectAsState()
    val logBufferSize by settingsViewModel.logBufferSize.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var inputValue by remember { mutableStateOf(logBufferSize.toString()) }

    val parsedValue = inputValue.toIntOrNull()
    val isValid = parsedValue != null && parsedValue in 100..10000

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.use_shizuku_backend),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = stringResource(R.string.grant_shizuku_access),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                Switch(
                    checked = isConnected,
                    onCheckedChange = { checked ->
                        if (checked) {
                            settingsViewModel.requestPrivileges()
                        } else {
                            settingsViewModel.releasePrivileges()
                        }
                    }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable(enabled = true, onClick = {
                        // todo: how to idiomatically display alert dialog here?
                        showDialog = true
                    }),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.logcat_buffer_size_title),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "$logBufferSize lines",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            settingsViewModel.setLogBufferSize(parsedValue!!)
                            showDialog = false
                        },
                        enabled = isValid
                    ) {
                        Text(stringResource(ok))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text(stringResource(cancel))
                    }
                },
                title = { Text(stringResource(R.string.saved_log_lines)) },
                text = {
                    Column {
                        Text("Enter a number between 100 and 10000:")
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = inputValue,
                            onValueChange = { inputValue = it },
                            isError = parsedValue == null || parsedValue !in 100..10000,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                }
            )
        }
    }
}
