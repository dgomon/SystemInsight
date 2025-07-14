package com.dgomon.systeminsight.presentation.getProp

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dgomon.systeminsight.R
import com.dgomon.systeminsight.ui.common.CommonTopBar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun GetPropTopBar(getPropViewModel: GetPropViewModel = hiltViewModel()) {
    val query by getPropViewModel.query.collectAsState()

    CommonTopBar(
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
        showMenu = true,
        menuItems = listOf(
            "Control Screen" to {
//                navController.navigate("control")
            },
            "Settings" to { /* TODO */ }
        ),
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