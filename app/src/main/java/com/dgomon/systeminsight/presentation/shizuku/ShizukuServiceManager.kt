package com.dgomon.systeminsight.presentation.shizuku

import android.content.ComponentName
import android.content.ServiceConnection
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.IBinder
import android.util.Log
import com.dgomon.systeminsight.BuildConfig
import com.dgomon.systeminsight.core.service.PrivilegedCommandService
import com.dgomon.systeminsight.core.service.PrivilegedServiceConnectionProvider
import com.dgomon.systeminsight.domain.shizuku.ShizukuStatus
import com.dgomon.systeminsight.domain.shizuku.ShizukuStatus.Bound
import com.dgomon.systeminsight.domain.shizuku.ShizukuStatus.Checking
import com.dgomon.systeminsight.domain.shizuku.ShizukuStatus.Connected
import com.dgomon.systeminsight.domain.shizuku.ShizukuStatus.Connecting
import com.dgomon.systeminsight.domain.shizuku.ShizukuStatus.Dead
import com.dgomon.systeminsight.domain.shizuku.ShizukuStatus.Denied
import com.dgomon.systeminsight.domain.shizuku.ShizukuStatus.Disconnected
import com.dgomon.systeminsight.domain.shizuku.ShizukuStatus.Error
import com.dgomon.systeminsight.domain.shizuku.ShizukuStatus.NoPermission
import com.dgomon.systeminsight.domain.shizuku.ShizukuStatus.RequestingPermission
import com.dgomon.systeminsight.domain.shizuku.ShizukuStatus.Unsupported
import com.dgomon.systeminsight.service.IPrivilegedCommandService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import rikka.shizuku.Shizuku
import rikka.shizuku.Shizuku.OnBinderDeadListener
import rikka.shizuku.Shizuku.OnBinderReceivedListener
import rikka.shizuku.Shizuku.UserServiceArgs
import rikka.shizuku.Shizuku.checkSelfPermission
import rikka.shizuku.Shizuku.isPreV11
import rikka.shizuku.Shizuku.requestPermission
import rikka.shizuku.Shizuku.shouldShowRequestPermissionRationale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShizukuServiceManager @Inject constructor() : PrivilegedServiceConnectionProvider {
    private val _status = MutableStateFlow(Disconnected)
    val status: StateFlow<ShizukuStatus> = _status.asStateFlow()

    override val isConnected: StateFlow<Boolean> = status
        .map { it == Connected }
        .stateIn(
            scope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
            started = SharingStarted.Eagerly,
            initialValue = false
        )

    private var privilegedCommandService: IPrivilegedCommandService? = null

    private val onRequestPermissionResultListener = Shizuku.OnRequestPermissionResultListener { requestCode, grantResult ->
        Log.d(TAG, "Request permission result: $requestCode, $grantResult")
        if (requestCode == CODE_BIND_SERVICE) {
            if (grantResult == PERMISSION_GRANTED) {
                _status.value = Bound
                requestPrivileges()
            } else {
                _status.value = Denied
            }
        }
    }

    private val onBinderReceivedListener = OnBinderReceivedListener {
        Log.d(TAG, "Binder received")
        if (isPreV11()) {
            _status.value = Unsupported
        } else {
            _status.value = Checking
        }
    }

    private val onBinderDeadListener = OnBinderDeadListener {
        Log.d(TAG, "Binder dead")
        _status.value = Dead
    }

    init {
        _status.value = Disconnected
        Shizuku.addBinderReceivedListenerSticky(onBinderReceivedListener)
        Shizuku.addBinderDeadListener(onBinderDeadListener)
        Shizuku.addRequestPermissionResultListener(onRequestPermissionResultListener)
    }

    private fun checkPermission(requestCode: Int): Boolean {
        if (isPreV11()) {
            return false
        }

        return try {
            if (checkSelfPermission() == PERMISSION_GRANTED) {
                true
            } else if (shouldShowRequestPermissionRationale()) {
                _status.value = NoPermission
                false
            } else {
                _status.value = RequestingPermission
                requestPermission(requestCode)
                false
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to check permission", e)
            _status.value = Error
            false
        }
    }

    private val userServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, binder: IBinder?) {
            Log.d(TAG, "onServiceConnected: $componentName $binder")
            if (binder?.pingBinder() == true) {
                privilegedCommandService = IPrivilegedCommandService.Stub.asInterface(binder)
                _status.value = Connected
            } else {
                Log.e(TAG, "Service connected but ping failed")
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            Log.d(TAG, "Service disconnected: $componentName")

            privilegedCommandService = null
            _status.value = Disconnected
        }
    }

    override fun requestPrivileges() {
        if (!checkPermission(CODE_BIND_SERVICE)) {
            Log.e(TAG, "Permission denied")
            return
        }
        val minSupportedVersion = 10

        if (Shizuku.getVersion() < minSupportedVersion) {
            Log.e(TAG, "Unsupported version of Shizuku: ${Shizuku.getVersion()}")
            return
        }

        _status.value = Connecting
        runCatching {
            Shizuku.bindUserService(userServiceArgs, userServiceConnection)
        }.onFailure { throwable ->
            Log.e(TAG, "Failed to bind service", throwable)
            _status.value = Error
        }.onSuccess {
            Log.d(TAG, "Service bound")
            _status.value = Bound
        }
    }

    override fun releasePrivileges() {
        if (!checkPermission(CODE_UNBIND_SERVICE)) {
            Log.e(TAG, "Permission denied")
            _status.value = Error
            return
        }

        Shizuku.unbindUserService(userServiceArgs, userServiceConnection, true)
        _status.value = Disconnected
    }

    override fun getService(): IPrivilegedCommandService?
        = privilegedCommandService

    private val userServiceArgs =
        UserServiceArgs(ComponentName(BuildConfig.APPLICATION_ID,
            PrivilegedCommandService::class.java.name))
        .daemon(false)
        .processNameSuffix("service")
        .debuggable(BuildConfig.DEBUG)
        .version(BuildConfig.VERSION_CODE)

    companion object {
        const val TAG = "ShizukuServiceManager"
        const val CODE_BIND_SERVICE = 1001
        const val CODE_UNBIND_SERVICE = 1002
    }
}
