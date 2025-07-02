package com.dgomon.systeminsight.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.dgomon.systeminsight.MainViewModel

@Composable
fun AppRoot(
    viewModel: MainViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.ensureConnected()
    }

    MainNavigationBar()
}