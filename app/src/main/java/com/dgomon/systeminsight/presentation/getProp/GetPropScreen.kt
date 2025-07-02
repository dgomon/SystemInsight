package com.dgomon.systeminsight.presentation.getProp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun GetPropScreen(
    modifier: Modifier,
    viewModel: GetPropViewModel = hiltViewModel()
) {
    val props by viewModel.props.collectAsState()

    // Convert to tree
    val root = remember(props) { buildTree(props) }
    val nodes = root.children

    LazyColumn {
        item {
            Column {
                nodes.forEach { TreeNodeView(it) }
            }
        }
    }
}

fun buildTree(entries: List<PropEntry>, delimiter: Char = '.') : TreeNode {
    val root = TreeNode(name = "root")

    for (entry in entries) {
        val parts = entry.key.split(delimiter)
        var current = root

        for ((index, part) in parts.withIndex()) {
            val existing = current.children.find { it.name == part }
            val node = existing ?: TreeNode(name = part).also { current.children += it }
            current = node
            if (index == parts.lastIndex) {
                current.value = entry.value
            }
        }
    }

    return root
}
