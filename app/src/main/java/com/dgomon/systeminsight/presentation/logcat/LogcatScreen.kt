package com.dgomon.systeminsight.presentation.logcat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import com.dgomon.systeminsight.ui.common.HighlightedText
import com.dgomon.systeminsight.ui.common.RequirePrivilegedConnection

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun LogcatScreen(
    navBackStackEntry: NavBackStackEntry,
    modifier: Modifier = Modifier,
    logcatViewModel: LogcatViewModel = hiltViewModel(navBackStackEntry),
) {
    val searchQuery by logcatViewModel.query.collectAsState()
    val filteredLogLines by logcatViewModel.filteredLogLines.collectAsState()
    val isConnected by logcatViewModel.isConnected.collectAsState()

    LaunchedEffect(Unit) {
        logcatViewModel.onScreenCreated(true)
    }

    RequirePrivilegedConnection(isConnected = isConnected, modifier = modifier, content = {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            val listState = rememberLazyListState()

            LaunchedEffect(filteredLogLines.size) {
                // Scroll to the last item when a new one is added
                if (filteredLogLines.isNotEmpty()) {
                    listState.scrollToItem(filteredLogLines.lastIndex)
                }
            }

            LazyColumn(
                state = listState,
                modifier = modifier
                    .fillMaxSize()
            ) {
                items(filteredLogLines) { line ->
                    HighlightedText(line = line, query = searchQuery)
                }
            }
        }
    }
)}
