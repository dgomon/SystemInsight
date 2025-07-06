package com.dgomon.systeminsight.ui

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor() : ViewModel() {
    private val _title = MutableStateFlow("System Insight")
    val title: StateFlow<String> = _title

    fun setTitle(newTitle: String) {
        _title.value = newTitle
    }

    fun share() {

    }
}