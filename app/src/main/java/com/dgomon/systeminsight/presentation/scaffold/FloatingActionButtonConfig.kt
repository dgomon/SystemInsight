package com.dgomon.systeminsight.presentation.scaffold

import androidx.compose.ui.graphics.vector.ImageVector

data class FloatingActionButtonConfig(
    val icon: ImageVector,
    val onClick: () -> Unit
)