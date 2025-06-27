package com.dgomon.systeminsight.service.shizuku

import android.content.Context
import android.os.RemoteException
import android.util.Log
import androidx.annotation.Keep
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class PrivilegedCommandService : IPrivilegedCommandService.Stub {
    /**
     * Constructor is required.
     */
    constructor() {
        Log.i("UserService", "constructor")
    }

    /**
     * Constructor with Context. This is only available from Shizuku API v13.
     *
     *
     * This method need to be annotated with [Keep] to prevent ProGuard from removing it.
     *
     * @param context Context created with createPackageContextAsUser
     * @see [code used to create the instance of this class](https://github.com/RikkaApps/Shizuku-API/blob/672f5efd4b33c2441dbf609772627e63417587ac/server-shared/src/main/java/rikka/shizuku/server/UserService.java.L66)
     */
    @Keep
    constructor(context: Context) {
        Log.i("UserService", "constructor with Context: context=" + context.toString())
    }

    /**
     * Reserved destroy method
     */
    public override fun destroy() {
        Log.i("UserService", "destroy")
        System.exit(0)
    }

    public override fun exit() {
        destroy()
    }

    @Throws(RemoteException::class)
    public override fun doSomething(): String {
//        return "pid=" + Os.getpid() + ", uid=" + Os.getuid() + ", " + stringFromJNI();

        val result = StringBuilder()
        try {
            val process = Runtime.getRuntime().exec("dumpsys battery")
            val reader = BufferedReader(
                InputStreamReader(process.getInputStream())
            )

            var line: String?
            while ((reader.readLine().also { line = it }) != null) {
                result.append(line).append('\n')
            }

            process.waitFor()
        } catch (e: IOException) {
            result.append("Error: ").append(e.message)
        } catch (e: InterruptedException) {
            result.append("Error: ").append(e.message)
        }

        return result.toString()
    }

}
