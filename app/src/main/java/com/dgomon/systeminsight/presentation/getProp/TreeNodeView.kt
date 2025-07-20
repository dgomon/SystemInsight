package com.dgomon.systeminsight.presentation.getProp

import android.widget.Toast
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dgomon.systeminsight.R
import com.dgomon.systeminsight.ui.common.HighlightedText

fun getFullPropertyName(node: TreeNode?): String {
    return generateSequence(node) { it.parent }
        .map { it.name }
        .toList()
        .asReversed()
        .joinToString(".")
}

@Composable
fun PropertyDialog(
    title: String,
    value: String,
    onDismiss: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.ok))
            }
        },
        title = {
            Text(text = title)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    )
                    IconButton(onClick = {
                        clipboardManager.setText(AnnotatedString(value))
                        Toast
                            .makeText(context, R.string.copied_to_clipboard, Toast.LENGTH_SHORT)
                            .show()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = stringResource(R.string.copy_to_clipboard)
                        )
                    }
                }
            }
        }
    )
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TreeNodeView(
    node: TreeNode,
    indentLevel: Int = 0,
    searchQuery: String = ""
) {
    var showDialog by remember { mutableStateOf(false) }

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
                .clickable {
                    if (node.children.isEmpty()) {
                        showDialog = true
                    } else {
                        node.isExpanded.value = !node.isExpanded.value
                    }
                }
                .padding(start = (indentLevel * 16).dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                .animateContentSize()
        ) {
            if (node.children.isEmpty()) {
                HighlightedText(
                    text = node.name,
                    query = searchQuery,
                    modifier = Modifier.weight(5f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.weight(1f))
                HighlightedText(
                    text = node.value.orEmpty(),
                    query = searchQuery,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                    contentDescription = null,
                    modifier = Modifier.rotate(rotationAngle)
                )
                Spacer(modifier = Modifier.width(8.dp))
                HighlightedText(
                    text = node.name,
                    query = searchQuery,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        AnimatedVisibility(
            visible = node.isExpanded.value,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            Column {
                node.children.forEach { child ->
                    TreeNodeView(child, indentLevel + 1, searchQuery)
                }
            }
        }

        // Dialog for leaf node value
        if (showDialog) {
            PropertyDialog(
                title = getFullPropertyName(node),
                value = node.value ?: "",
                onDismiss = { showDialog = false }
            )
        }
    }
}
