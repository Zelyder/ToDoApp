package com.zelyder.todoapp.domain.enums

sealed class NetworkStatus {
    object Available: NetworkStatus()
    object Unavailable: NetworkStatus()
}
