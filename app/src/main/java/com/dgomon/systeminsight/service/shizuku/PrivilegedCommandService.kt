package com.dgomon.systeminsight.service.shizuku

import android.content.Context
import android.os.RemoteException
import android.util.Log
import androidx.annotation.Keep
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.system.exitProcess

class PrivilegedCommandService : IPrivilegedCommandService.Stub {

    @Keep
    @SuppressWarnings("unused")
    constructor() {}

    @Keep
    @SuppressWarnings("unused")
    constructor(context: Context) {}

    override fun destroy() {
        Log.i("UserService", "destroy")
        exitProcess(0)
    }

    override fun exit() {
        destroy()
    }

    @Throws(RemoteException::class)
    override fun doSomething(): String {
        val result = StringBuilder()
        runCatching {
            val process = Runtime.getRuntime().exec("dumpsys battery")

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

}
