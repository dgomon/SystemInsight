package com.dgomon.systeminsight.presentation.getProp

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class TreeNode(
    val name: String,
    var value: String? = null,
    val children: MutableList<TreeNode> = mutableListOf(),
    val isExpanded: MutableState<Boolean> = mutableStateOf(false)
)
