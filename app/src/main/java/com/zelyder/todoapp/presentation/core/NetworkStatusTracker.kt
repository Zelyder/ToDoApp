package com.zelyder.todoapp.presentation.core

import com.zelyder.todoapp.domain.enums.NetworkStatus
import kotlinx.coroutines.flow.Flow

interface NetworkStatusTracker {
    fun getNetworkStatus(): Flow<NetworkStatus>
}