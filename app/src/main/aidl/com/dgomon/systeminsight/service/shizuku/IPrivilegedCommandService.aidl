package com.dgomon.systeminsight.service.shizuku;

interface IPrivilegedCommandService {

    void destroy() = 16777114; // Destroy method defined by Shizuku server

    void exit() = 1; // Exit method defined by user

    String doSomething() = 2;
}