package com.telakuR.easyorder.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest

interface NetworkCallback {
    fun onInternetConnected()
    fun onInternetDisconnected()
}

class NetworkUtils(private val context: Context) {

    private var networkCallback: NetworkCallback? = null

    fun isInternetConnected(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

    fun setNetworkCallback(callback: NetworkCallback?) {
        networkCallback = callback
    }

    fun removeNetworkCallback() {
        networkCallback = null
    }

    private val connectivityCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            networkCallback?.onInternetConnected()
        }

        override fun onLost(network: Network) {
            networkCallback?.onInternetDisconnected()
        }
    }

    fun registerConnectivityCallback() {
        val builder = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerNetworkCallback(builder.build(), connectivityCallback)
    }

    fun unregisterConnectivityCallback() {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.unregisterNetworkCallback(connectivityCallback)
    }
}
