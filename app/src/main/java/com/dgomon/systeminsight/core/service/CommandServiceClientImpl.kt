package com.dgomon.systeminsight.core.service

import java.util.zip.GZIPInputStream
import javax.inject.Inject

class CommandServiceClientImpl @Inject constructor(
    private val serviceConnectionProvider: PrivilegedServiceConnectionProvider
) : CommandServiceClient {

    override fun runCommand(cmd: String): String? {
        val compressed = serviceConnectionProvider.getService()
            ?.runCommand(cmd)

        return compressed?.let {
            GZIPInputStream(it.inputStream()).bufferedReader().use { reader ->
                reader.readText()
            }
        }
    }
}
