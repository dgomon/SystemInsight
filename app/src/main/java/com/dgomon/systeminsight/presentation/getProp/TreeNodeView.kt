package com.dgomon.systeminsight.presentation.getProp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TreeNodeView(node: TreeNode, indent: Int = 0) {
    Column {
        Row(
            Modifier
                .clickable { node.isExpanded.value = !node.isExpanded.value }
                .padding(start = indent.dp)
        ) {
            Text(if (node.children.isNotEmpty()) (if (node.isExpanded.value) "▼ " else "▶ ") else "• ")
            Text(text = node.name + (node.value?.let { ": $it" } ?: ""))
        }

        if (node.isExpanded.value) {
            node.children.forEach {
                TreeNodeView(it, indent + 16)
            }
        }
    }
}
