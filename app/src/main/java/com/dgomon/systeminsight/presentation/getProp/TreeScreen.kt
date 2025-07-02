package com.dgomon.systeminsight.presentation.getProp

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp


@Composable
fun TreeScreen(treeRoot: TreeNode, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.padding(8.dp)
    ) {
        displayTreeNodes(treeRoot.children, 0)
    }
}

private fun LazyListScope.displayTreeNodes(nodes: List<TreeNode>, indentLevel: Int) {
    nodes.forEach { node ->
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { node.isExpanded.value = !node.isExpanded.value }
                    .padding(start = indentLevel.dp * 16, end = 16.dp, top = 16.dp, bottom = 16.dp)
                    .animateContentSize()
            ) {
                if (node.children.isEmpty()) {
                    Text(text = node.name)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = node.value ?: "")
                } else {
                    val rotationAngle by animateFloatAsState(
                        targetValue = if (node.isExpanded.value) 90f else 0f,
                        animationSpec = tween(durationMillis = 300),
                        label = "ArrowRotation"
                    )

                    Image(
                        imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                        contentDescription = null,
                        modifier = Modifier.rotate(rotationAngle)
                    )
                    Text(
                        text = node.name,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        if (node.isExpanded.value) {
            displayTreeNodes(node.children, indentLevel + 1)
        }
    }
}
