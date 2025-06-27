package com.dgomon.systeminsight.presentation.shizuku

import android.content.ComponentName
import android.content.ServiceConnection
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import androidx.lifecycle.ViewModel
import com.dgomon.systeminsight.BuildConfig
import com.dgomon.systeminsight.domain.shizuku.ShizukuStatus
import com.dgomon.systeminsight.service.shizuku.IPrivilegedCommandService
import com.dgomon.systeminsight.service.shizuku.PrivilegedCommandService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import rikka.shizuku.Shizuku
import rikka.shizuku.Shizuku.OnBinderDeadListener
import rikka.shizuku.Shizuku.OnBinderReceivedListener
import rikka.shizuku.Shizuku.UserServiceArgs
import javax.inject.Inject


@HiltViewModel
class ShizukuViewModel @Inject constructor() : ViewModel() {
    private val _status = MutableStateFlow("Checking Shizuku...")
    val status: StateFlow<String> = _status.asStateFlow()

    private val onRequestPermissionResultListener = Shizuku.OnRequestPermissionResultListener { requestCode, grantResult ->
        _status.value = "Request permission result: $requestCode, $grantResult"
        if (requestCode == 100) {
            checkAndConnect(100)
        }
    }

    private val onBinderReceivedListener = OnBinderReceivedListener {
        if (Shizuku.isPreV11()) {
            _status.value = "Shizuku pre-v11 is not supported"
        } else {
            _status.value = "Binder received"
        }
    }

    private val onBinderDeadListener = OnBinderDeadListener {
        _status.value = "Binder dead"
    }

    init {
        Shizuku.addBinderReceivedListenerSticky(onBinderReceivedListener)
        Shizuku.addBinderDeadListener(onBinderDeadListener)
        Shizuku.addRequestPermissionResultListener(onRequestPermissionResultListener)

        if (checkPermission(17)) {
//            bindUserService()
        }
    }


    private fun checkPermission(code: Int): Boolean {
        if (Shizuku.isPreV11()) {
            return false
        }
        try {
            if (Shizuku.checkSelfPermission() == PERMISSION_GRANTED) {
                _status.value = "permission granted"
                return true
            } else if (Shizuku.shouldShowRequestPermissionRationale()) {
                _status.value = "User denied permission (shouldShowRequestPermissionRationale=true)"

                return false
            } else {
                Shizuku.requestPermission(code)
                return false
            }
        } catch (e: Throwable) {
            _status.value = e.toString()
        }

        return false
    }

    private fun checkAndConnect(code: Int) {

        _status.value = when {
            // Pre-v11 is unsupported
            Shizuku.isPreV11() -> ShizukuStatus.UNSUPPORTED.toString()

            // Granted
            Shizuku.checkSelfPermission() == PERMISSION_GRANTED -> ShizukuStatus.CONNECTED.toString()

            // Users choose "Deny and don't ask again"
            Shizuku.shouldShowRequestPermissionRationale() -> ShizukuStatus.DENIED.toString()
            else -> {
                // Request the permission
                Shizuku.requestPermission(code)
                ShizukuStatus.CHECKING.toString()
            }
        }
    }

    private val userServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, binder: IBinder?) {
            val res = java.lang.StringBuilder()
            res.append("onServiceConnected: ").append(componentName.className).append('\n')
            if (binder != null && binder.pingBinder()) {
                val service: IPrivilegedCommandService = IPrivilegedCommandService.Stub.asInterface(binder)
                try {
                    val result: String? = service.doSomething()
                    res.append(result)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                    res.append(Log.getStackTraceString(e))
                }
            } else {
                res.append("invalid binder for ").append(componentName).append(" received")
            }
            _status.value = res.toString().trim { it <= ' ' }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            _status.value = "onServiceDisconnected: " + '\n' + componentName.getClassName()
        }
    }

    private fun bindUserService() {
        val res = StringBuilder()
        try {
            if (Shizuku.getVersion() < 10) {
                res.append("requires Shizuku API 10")
            } else {
                Shizuku.bindUserService(userServiceArgs, userServiceConnection)
            }
        } catch (tr: Throwable) {
            tr.printStackTrace()
            res.append(tr.toString())
        }
        _status.value = res.toString().trim { it <= ' ' }
    }

    private val userServiceArgs =
        UserServiceArgs(ComponentName(BuildConfig.APPLICATION_ID,
            PrivilegedCommandService::class.java.name))
        .daemon(false)
        .processNameSuffix("service")
        .debuggable(BuildConfig.DEBUG)
        .version(BuildConfig.VERSION_CODE)


    override fun onCleared() {
        super.onCleared()
        Shizuku.removeRequestPermissionResultListener(onRequestPermissionResultListener)
    }
}
