package com.dgomon.systeminsight.presentation.getProp

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun GetPropScreen(
    modifier: Modifier = Modifier,
    getPropViewModel: GetPropViewModel = hiltViewModel(),
) {
    val props by getPropViewModel.filteredProps.collectAsState()
    val query by getPropViewModel.query.collectAsState()

    // Convert to tree
    val root = remember(props) { buildTree(props) }
    val nodes = root.children

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn {
            item {
                Column {
                    nodes.forEach { TreeNodeView(node = it, searchQuery = query) }
                }
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
            val node = existing ?: TreeNode(name = part, parent = current).also { current.children += it }
            current = node
            if (index == parts.lastIndex) {
                current.value = entry.value
            }
        }
    }

    return root
}
