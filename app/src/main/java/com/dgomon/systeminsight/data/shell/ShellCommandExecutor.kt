package com.dgomon.systeminsight.data.shell

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

class ShellCommandExecutor @Inject constructor() {
    suspend fun runCommand(command: String): List<String> = withContext(Dispatchers.IO) {
        try {
            val process = Runtime.getRuntime().exec(command)
            BufferedReader(InputStreamReader(process.inputStream))
                .lineSequence()
                .toList()
        } catch (e: Exception) {
            listOf("Error: ${e.message}")
        }
    }
}