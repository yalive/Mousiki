package com.cas.common.connectivity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.lifecycle.LiveData

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/12/20.
 ***************************************
 */
class ConnectivityState(
    private val context: Context
) : LiveData<ConnectionModel>() {

    override fun onActive() {
        super.onActive()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        context.registerReceiver(networkReceiver, filter)
    }

    override fun onInactive() {
        super.onInactive()
        context.unregisterReceiver(networkReceiver)
    }

    fun isConnected() = value?.isConnected ?: false

    private val networkReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.extras != null) {
                val activeNetwork =
                    intent.extras!![ConnectivityManager.EXTRA_NETWORK_INFO] as NetworkInfo?
                val isConnected =
                    activeNetwork != null && activeNetwork.isConnectedOrConnecting
                if (isConnected) {
                    postValue(ConnectionModel(true))
                    //checkServerAccess()
                } else {
                    postValue(ConnectionModel(false))
                }
            }
        }
    }
}

data class ConnectionModel(
    val isConnected: Boolean
)