package com.dgomon.systeminsight.core.service

import android.content.Context
import android.os.RemoteException
import android.util.Log
import androidx.annotation.Keep
import com.dgomon.systeminsight.service.ILogCallback
import com.dgomon.systeminsight.service.IPrivilegedCommandService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.util.zip.GZIPOutputStream
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
    override fun runCommand(cmd: String): ByteArray {
        return try {
            val process = Runtime.getRuntime().exec(cmd)
            val input = process.inputStream.bufferedReader().readText()
            process.waitFor()

            ByteArrayOutputStream().use { byteStream ->
                GZIPOutputStream(byteStream).use { gzip ->
                    gzip.write(input.toByteArray(Charsets.UTF_8))
                }
                byteStream.toByteArray()
            }
        } catch (e: Exception) {
            ByteArrayOutputStream().use { byteStream ->
                GZIPOutputStream(byteStream).use { gzip ->
                    gzip.write("Command failed: ${e.message}".toByteArray(Charsets.UTF_8))
                }
                byteStream.toByteArray()
            }
        }
    }

    override fun startLogging(callback: ILogCallback) {
        if (logcatJob != null) {
            Log.w(TAG, "startLogging: already logging")
            return
        }

        logcatJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                process = Runtime.getRuntime().exec("logcat -T 0 --format=threadtime")

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