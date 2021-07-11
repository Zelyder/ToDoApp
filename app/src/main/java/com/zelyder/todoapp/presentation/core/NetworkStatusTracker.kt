package com.zelyder.todoapp.presentation.core

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import com.zelyder.todoapp.domain.enums.NetworkStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map

@ExperimentalCoroutinesApi
class NetworkStatusTracker(context: Context) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val networkStatus = callbackFlow<NetworkStatus> {
        val networkStatusCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onUnavailable() {
                super.onUnavailable()
                offer(NetworkStatus.Unavailable)
                Log.d("LOL", "onUnavailable")
            }

            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                offer(NetworkStatus.Available)
                Log.d("LOL","onAvailable")
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                offer(NetworkStatus.Unavailable)
                Log.d("LOL","onLost")
            }
        }
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, networkStatusCallback)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(networkStatusCallback)
        }
    }
}

inline fun <Result> Flow<NetworkStatus>.map (
    crossinline onUnavailable: suspend () -> Result,
    crossinline onAvailable: suspend () -> Result
): Flow<Result> = map {status ->
    when(status) {
        NetworkStatus.Unavailable -> onUnavailable()
        NetworkStatus.Available -> onAvailable()
    }
}