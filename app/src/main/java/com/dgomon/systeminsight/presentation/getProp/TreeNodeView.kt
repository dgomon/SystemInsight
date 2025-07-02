package com.dgomon.systeminsight.presentation.getProp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TreeNodeView(
    node: TreeNode,
    indentLevel: Int = 1
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        val rotationAngle by animateFloatAsState(
            targetValue = if (node.isExpanded.value) 90f else 0f,
            animationSpec = tween(300),
            label = "rotation"
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { node.isExpanded.value = !node.isExpanded.value }
                .padding(start = (indentLevel * 16).dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                .animateContentSize()
        ) {
            if (node.children.isEmpty()) {
                Text(text = node.name)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = node.value ?: "")
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                    contentDescription = null,
                    modifier = Modifier.rotate(rotationAngle)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = node.name)
            }
        }

        AnimatedVisibility(
            visible = node.isExpanded.value,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            Column {
                node.children.forEach { child ->
                    TreeNodeView(child, indentLevel + 1)
                }
            }
        }
    }
}
