package com.dgomon.systeminsight.presentation.shizuku

import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dgomon.systeminsight.domain.shizuku.ShizukuStatus
import com.dgomon.systeminsight.ui.Screen
import rikka.shizuku.Shizuku

@Composable
fun ShizukuScreen(viewModel: ShizukuViewModel = hiltViewModel()) {
    val status by viewModel.status.collectAsState()

//    LaunchedEffect(Unit) {
//        if (!Shizuku.isPreV11()) {
//            if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
//                viewModel.onPermissionGranted()
//            } else {
//                Shizuku.addRequestPermissionResultListener(object :
//                    Shizuku.OnRequestPermissionResultListener {
//
//                    override fun onRequestPermissionResult(
//                        requestCode: Int,
//                        grantResult: Int
//                    ) {
//                        TODO("Not yet implemented")
//                        Shizuku.removeRequestPermissionResultListener(this)
//                        if (grantResult == PackageManager.PERMISSION_GRANTED) {
//                            viewModel.onPermissionGranted()
//                        } else {
//                            viewModel.markError()
//                        }
//
//                    }
//                })
//
//                Shizuku.requestPermission(100)
//            }
//        } else {
//            viewModel.markError()
//        }
//    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = status,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 2.dp)
        )

//        when (status) {
//            ShizukuStatus.CHECKING -> Text("Checking Shizuku...")
//            ShizukuStatus.CONNECTED -> Text("Shizuku is connected")
//            ShizukuStatus.NOT_CONNECTED -> Text("Shizuku not connected or not available")
//            ShizukuStatus.NO_PERMISSION -> Text("Requesting Shizuku permission...")
//            ShizukuStatus.ERROR -> Text("Failed to connect to Shizuku")
//            ShizukuStatus.UNSUPPORTED -> Text("Unsupported version")
//            ShizukuStatus.DENIED -> Text("Permission denied")
//        }
    }
}
