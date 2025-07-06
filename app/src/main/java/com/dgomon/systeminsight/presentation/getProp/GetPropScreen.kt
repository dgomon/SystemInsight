package com.dgomon.systeminsight.presentation.getProp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dgomon.systeminsight.R
import com.dgomon.systeminsight.ui.NavigationViewModel

@Composable
fun GetPropScreen(
    modifier: Modifier,
    navigationViewModel: NavigationViewModel,
    getPropViewModel: GetPropViewModel = hiltViewModel(),
    onFabContent: ((@Composable () -> Unit) -> Unit),
) {
    val props by getPropViewModel.filteredProps.collectAsState()
    val query by getPropViewModel.query.collectAsState()

    val context = LocalContext.current

    // Convert to tree
    val root = remember(props) { buildTree(props) }
    val nodes = root.children

    LaunchedEffect(Unit) {
        navigationViewModel.setTitle(context.getString(R.string.title_properties))

        onFabContent {
            FloatingActionButton(onClick = { getPropViewModel.shareOutput() }) {
                Icon(Icons.Default.Share, contentDescription = "Share")
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        OutlinedTextField(
            value = query,
            onValueChange = getPropViewModel::setQuery,
            label = { Text(stringResource(R.string.search_property)) },
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )


        LazyColumn {
            item {
                Column {
                    nodes.forEach { TreeNodeView(it) }
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
            val node = existing ?: TreeNode(name = part).also { current.children += it }
            current = node
            if (index == parts.lastIndex) {
                current.value = entry.value
            }
        }
    }

    return root
}
