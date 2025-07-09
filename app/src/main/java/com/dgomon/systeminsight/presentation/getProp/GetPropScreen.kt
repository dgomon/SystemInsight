package com.dgomon.systeminsight.presentation.getProp

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dgomon.systeminsight.R
import com.dgomon.systeminsight.presentation.scaffold.AppScaffoldViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun GetPropScreen(
    modifier: Modifier = Modifier,
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
                title = {
                    AnimatedContent(
                        targetState = getPropViewModel.isSearchActive,
                        transitionSpec = {
                            (slideInHorizontally(animationSpec = tween(300)) { it } + fadeIn()) with
                                    (slideOutHorizontally(animationSpec = tween(300)) { it } + fadeOut())                        },
                        label = "SearchBarTransition"
                    ) { isSearch ->
                        if (isSearch) {
                            OutlinedTextField(
                                value = query,
                                onValueChange = getPropViewModel::setQuery,
                                placeholder = { Text(stringResource(R.string.search_property)) },
                                singleLine = true,
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                trailingIcon = {
                                    if (query.isNotEmpty()) {
                                        IconButton(onClick = { getPropViewModel.setQuery("") }) {
                                            Icon(Icons.Default.Close, contentDescription = "Clear")
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 56.dp)
                            )
                        } else {
                            Text(stringResource(R.string.title_properties))
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { getPropViewModel.shareOutput() },
                        enabled = true
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
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
