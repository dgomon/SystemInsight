package com.dgomon.systeminsight.root

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import com.dgomon.systeminsight.presentation.logcat.LogcatScreen
import com.dgomon.systeminsight.presentation.logcat.LogcatViewModel

class MainActivity : ComponentActivity() {

    // Here we want a viewModel that will get text from logcat
    // LogcatDisplay will subscribe to the viewModel state
    // We also want the ViewModel be injected using Hilt

    private val logcatViewModel: LogcatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
//            MaterialTheme {
//                LogcatScreen(viewModel = logcatViewModel)
                LogcatScreen()
//            }
        }
    }

}