package com.dgomon.systeminsight.presentation.privilege_control

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dgomon.systeminsight.R
import com.dgomon.systeminsight.presentation.navigation.NavigationViewModel

@Preview
@Composable
fun PrivilegeControlScreen(
    modifier: Modifier = Modifier,
    navigationViewModel: NavigationViewModel,
    privilegeControlViewModel: PrivilegeControlViewModel = hiltViewModel(),
    onFabContent: ((@Composable () -> Unit) -> Unit),
) {
    val isConnected by privilegeControlViewModel.isConnected.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        navigationViewModel.setTitle(context.getString(R.string.title_privilege_control))
        onFabContent {} // clears the FAB
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column {
            Button(
                onClick = { privilegeControlViewModel.requestPrivileges() },
                enabled = !isConnected
            ) {
                Text(text = "Connect")
            }

            Button(onClick = {
                privilegeControlViewModel.releasePrivileges() },
                enabled = isConnected
            ) {
                Text(text = "Disconnect")
            }
        }

        val scrollState = rememberScrollState()

        Text(
            text = isConnected.toString(),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .height(144.dp)
                .verticalScroll(scrollState)
                .padding(vertical = 2.dp)
        )
    }

}
