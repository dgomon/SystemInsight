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
import rikka.shizuku.Shizuku.checkSelfPermission
import rikka.shizuku.Shizuku.isPreV11
import rikka.shizuku.Shizuku.requestPermission
import rikka.shizuku.Shizuku.shouldShowRequestPermissionRationale
import rikka.shizuku.Shizuku.unbindUserService
import javax.inject.Inject


@HiltViewModel
class ShizukuViewModel @Inject constructor() : ViewModel() {
    private val _status = MutableStateFlow("Checking Shizuku...")
    val status: StateFlow<String> = _status.asStateFlow()

    private val onRequestPermissionResultListener = Shizuku.OnRequestPermissionResultListener { requestCode, grantResult ->
        Log.d(TAG, "Request permission result: $requestCode, $grantResult")
        _status.value = "Request permission result: $requestCode, $grantResult"
    }

    private val onBinderReceivedListener = OnBinderReceivedListener {
        Log.d(TAG, "Binder received")
        if (isPreV11()) {
            _status.value = "Shizuku pre-v11 is not supported"
        } else {
            _status.value = "Binder received"
        }
    }

    private val onBinderDeadListener = OnBinderDeadListener {
        Log.d(TAG, "Binder dead")
        _status.value = "Binder dead"
    }

    init {
        _status.value = ShizukuStatus.NOT_CONNECTED.toString()
        Shizuku.addBinderReceivedListenerSticky(onBinderReceivedListener)
        Shizuku.addBinderDeadListener(onBinderDeadListener)
        Shizuku.addRequestPermissionResultListener(onRequestPermissionResultListener)
    }

    private fun checkPermission(requestCode: Int): Boolean {
        if (isPreV11()) {
            return false
        }

        try {
            if (checkSelfPermission() == PERMISSION_GRANTED) {
                _status.value = "permission granted"
                return true
            } else if (shouldShowRequestPermissionRationale()) {
                _status.value = "User denied permission (shouldShowRequestPermissionRationale=true)"
                return false
            } else {
                _status.value = "requesting permission"
                requestPermission(requestCode)
                return false
            }
        } catch (e: Throwable) {
            _status.value = e.toString()
            return false
        }
    }

    private val userServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, binder: IBinder?) {
            val result = buildString {
                appendLine("Service connected: ${componentName.className}")
                if (binder?.pingBinder() == true) {
                    val service = IPrivilegedCommandService.Stub.asInterface(binder)
                    try {
                        appendLine(service.doSomething())
                    } catch (e: RemoteException) {
                        e.printStackTrace()
                        appendLine(Log.getStackTraceString(e))
                    }
                } else {
                    appendLine("Invalid binder received for $componentName")
                }
            }.trim()

            _status.value = result
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            _status.value = "Service disconnected: " + '\n' + componentName.className
        }
    }

    fun bindPrivilegedService() {
        if (checkPermission(CODE_BIND_SERVICE)) {
            bindUserService()
        }
    }

    fun unbindPrivilegedService() {
        if (checkPermission(CODE_UNBIND_SERVICE)) {
            unbindUserService(userServiceArgs, userServiceConnection, true)
        }
    }

    private fun bindUserService() {
        val minSupportedVersion = 10

        if (Shizuku.getVersion() < minSupportedVersion) {
            _status.value = "Shizuku API $minSupportedVersion+ required"
            return
        }

        _status.value = "Connecting..."
        runCatching {
            Shizuku.bindUserService(userServiceArgs, userServiceConnection)
        }.onFailure { throwable ->
            Log.e("Shizuku", "Failed to bind service", throwable)
            _status.value = throwable.localizedMessage ?: "Unknown error"
        }.onSuccess {
            _status.value = "Connected!"
        }
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

    companion object {
        const val TAG = "ShizukuViewModel"
        const val CODE_BIND_SERVICE = 1001
        const val CODE_UNBIND_SERVICE = 1002
    }

}
