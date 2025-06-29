package com.dgomon.systeminsight.presentation.shizuku

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Preview
@Composable
fun ShizukuScreen(viewModel: ShizukuViewModel = hiltViewModel()) {
    val status by viewModel.status.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column {
            Button(onClick = {
                viewModel.bindPrivilegedService()
            }) {
                Text(text = "Connect")
            }

            Button(onClick = {
                viewModel.unbindPrivilegedService()
            }) {
                Text(text = "Disconnect")
            }
        }

        val scrollState = rememberScrollState()

        Text(
            text = status,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .height(144.dp)
                .verticalScroll(scrollState)
                .padding(vertical = 2.dp)
        )
    }

}
