package com.dgomon.systeminsight.root

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.dgomon.systeminsight.presentation.GetProp.GetPropScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
//                LogcatScreen()
                GetPropScreen()
            }
        }
    }

}