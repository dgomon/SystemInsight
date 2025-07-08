package com.dgomon.systeminsight.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppScaffoldViewModel @Inject constructor() : ViewModel() {
    val topBarContent = mutableStateOf<(@Composable () -> Unit)?>(null)
}
