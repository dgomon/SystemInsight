package com.dgomon.systeminsight.presentation.getProp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.dgomon.systeminsight.R
import com.dgomon.systeminsight.ui.NavigationViewModel

@Composable
fun GetPropScreen(
    modifier: Modifier,
    navigationViewModel: NavigationViewModel,
    getPropViewModel: GetPropViewModel = hiltViewModel(),
) {
    val props by getPropViewModel.props.collectAsState()
    val context = LocalContext.current

    // Convert to tree
    val root = remember(props) { buildTree(props) }
    val nodes = root.children

    LaunchedEffect(Unit) {
        navigationViewModel.setTitle(context.getString(R.string.title_properties))
    }

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
