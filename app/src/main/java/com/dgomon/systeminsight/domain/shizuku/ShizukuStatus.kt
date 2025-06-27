package com.dgomon.systeminsight.domain.shizuku

// TODO: clean statuses
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
