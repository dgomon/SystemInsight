package com.dgomon.systeminsight.service.shizuku;

interface IPrivilegedCommandService {

    void destroy() = 16777114; // Destroy method defined by Shizuku server

    void exit() = 1;

    String runCommand(String cmd) = 2;


}