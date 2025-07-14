package com.dgomon.systeminsight.presentation.privilege_control

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.dgomon.systeminsight.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun PrivilegeControlTopBar() {
    TopAppBar(
        title = { Text(stringResource(R.string.title_privilege_control)) },
    )
}