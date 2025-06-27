package com.dgomon.systeminsight.service

import android.content.Context
import android.os.RemoteException
import android.util.Log
import androidx.annotation.Keep
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import kotlin.system.exitProcess

class PrivilegedCommandService : IPrivilegedCommandService.Stub {

    private var logcatJob: Job? = null
    private var process: Process? = null

    @Keep
    @SuppressWarnings("unused")
    constructor() {}

    @Keep
    @SuppressWarnings("unused")
    constructor(context: Context) {}

    override fun destroy() {
        Log.i(TAG, "destroy")
        exitProcess(0)
    }

    override fun exit() {
        destroy()
    }

    @Throws(RemoteException::class)
    override fun runCommand(cmd: String): String {
        val result = StringBuilder()
        runCatching {
            val process = Runtime.getRuntime().exec(cmd)

            val reader = BufferedReader(
                InputStreamReader(process.inputStream)
            )

            reader.useLines { lines ->
                lines.forEach { line ->
                    result.append(line).append('\n')
                }
            }

            process.waitFor()
        }.onFailure { throwable ->
            result.append("Error: ").append(throwable.message)
        }

        return result.toString()
    }

    override fun startLogging(callback: ILogCallback) {
        if (logcatJob != null) {
            Log.w(TAG, "startLogging: already logging")
            return
        }

        logcatJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                process = Runtime.getRuntime().exec("logcat")

                process?.inputStream?.bufferedReader()?.useLines { lines ->
                    lines.forEach { line ->
                        try {
                            callback.onLogLine(line)
                        } catch (e: RemoteException) {
                            Log.w(TAG, "Callback failed", e)
                        }
                    }
                }
            } catch (e: IOException) {
                Log.e(TAG, "Failed to start logcat", e)
            }
        }
    }

    override fun stopLogging() {
        logcatJob?.cancel()
        logcatJob = null

        process?.destroy()
        process = null
    }

    companion object {
        const val TAG = "PrivilegedService"
    }

}