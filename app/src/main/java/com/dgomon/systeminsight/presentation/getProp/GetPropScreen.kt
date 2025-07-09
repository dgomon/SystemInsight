package com.dgomon.systeminsight.presentation.getProp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dgomon.systeminsight.R
import com.dgomon.systeminsight.presentation.logcat.LogcatState
import com.dgomon.systeminsight.presentation.navigation.NavigationViewModel
import com.dgomon.systeminsight.presentation.scaffold.AppScaffoldViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GetPropScreen(
    modifier: Modifier,
    scaffoldViewModel: AppScaffoldViewModel,
    getPropViewModel: GetPropViewModel = hiltViewModel(),
) {
    val props by getPropViewModel.filteredProps.collectAsState()
    val query by getPropViewModel.query.collectAsState()

    // Convert to tree
    val root = remember(props) { buildTree(props) }
    val nodes = root.children

    LaunchedEffect(Unit) {

        scaffoldViewModel.topBarContent.value = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_properties)) },
                actions = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, end = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = query,
                            onValueChange = getPropViewModel::setQuery,
                            label = { Text(stringResource(R.string.search_property)) },
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            modifier = Modifier
                                .weight(1f)
                                .padding(bottom = 8.dp)
                        )


                        IconButton(
                            onClick = { getPropViewModel.shareOutput() },
                            enabled = true
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
                        }
                    }
                }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

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
