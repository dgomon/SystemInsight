package com.dgomon.systeminsight

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.dgomon.systeminsight.ui.AppRoot
import com.dgomon.systeminsight.ui.theme.SystemInsightTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SystemInsightTheme {
                AppRoot()
            }
        }
    }
}