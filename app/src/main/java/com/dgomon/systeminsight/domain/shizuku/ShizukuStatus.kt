package com.dgomon.systeminsight.domain.shizuku

enum class ShizukuStatus {
    Disconnected,
    Checking,
    Bound,
    Connecting,
    Connected,
    RequestingPermission,
    NoPermission,
    Unsupported,
    Denied,
    Dead,
    Error
}
