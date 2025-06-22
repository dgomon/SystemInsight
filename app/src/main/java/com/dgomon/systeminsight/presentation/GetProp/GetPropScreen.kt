package com.dgomon.systeminsight.presentation.GetProp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel


@Preview
@Composable
fun GetPropScreen(viewModel: GetPropViewModel = viewModel()) {
    val props by viewModel.props.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(props.size) {
        if (props.isNotEmpty()) {
            listState.animateScrollToItem(props.lastIndex)
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        items(props) { line ->
            Text(
                text = line,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }
    }

}
