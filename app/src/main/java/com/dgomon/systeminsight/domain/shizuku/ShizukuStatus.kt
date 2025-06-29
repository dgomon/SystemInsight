package com.dgomon.systeminsight.domain.shizuku

enum class ShizukuStatus {
    Disconnected,
    Checking,
    Bound,
    Connecting,
    Connected,
    NoPermission,
    RequestingPermission,
    Unsupported,
    Denied,
    Dead,
    Error
}
