package com.dgomon.systeminsight.service;

import com.dgomon.systeminsight.service.ILogCallback;

interface IPrivilegedCommandService {

    void destroy() = 16777114; // Destroy method defined by Shizuku server

    void exit() = 1;

    byte[] runCommand(String cmd) = 2;

    void startLogging(ILogCallback callback) = 3;

    void stopLogging() = 4;
}