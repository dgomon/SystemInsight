package com.dgomon.systeminsight.presentation.getProp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun TreeScreen(treeRoot: TreeNode, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.padding(8.dp)) {
        displayTreeNodes(treeRoot.children, 0)
    }
}

private fun LazyListScope.displayTreeNodes(nodes: List<TreeNode>, indent: Int) {
    nodes.forEach { node ->
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = indent.dp, top = 4.dp, bottom = 4.dp)
                    .clickable { node.isExpanded.value = !node.isExpanded.value }
            ) {
                Text(
                    text = if (node.children.isNotEmpty()) {
                        if (node.isExpanded.value) "▼ ${node.name}" else "▶ ${node.name}"
                    } else {
                        "• ${node.name}: ${node.value}"
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        if (node.isExpanded.value) {
            displayTreeNodes(node.children, indent + 16)
        }
    }
}
