package com.dgomon.systeminsight.core.service


interface CommandServiceClient {
    fun runCommand(cmd: String): String?
}
