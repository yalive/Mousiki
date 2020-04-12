package com.cas.musicplayer.data.config

import android.content.Context
import android.os.Bundle
import com.cas.common.connectivity.ConnectivityState
import com.cas.musicplayer.utils.getCurrentLocale
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import javax.inject.Inject

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/9/20.
 ***************************************
 */
class RemoteAppConfig @Inject constructor(
    private val firebaseRemoteConfig: FirebaseRemoteConfig,
    private val connectivityState: ConnectivityState,
    private val context: Context
) {

    init {
        val connectedBefore = connectivityState.isConnected()
        firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
                val bundle = Bundle()
                bundle.putBoolean("isConnected", connectivityState.isConnected())
                bundle.putBoolean("isConnectedBeforeCall", connectedBefore)
                bundle.putString("local", getCurrentLocale())
                firebaseAnalytics.logEvent("error_fetch_remote_config", bundle)
            }
        }
    }

    fun getYoutubeApiKeys(): List<String> {
        return firebaseRemoteConfig.getString(YOUTUBE_API_KEYS).split("###")
    }

    companion object {
        const val YOUTUBE_API_KEYS = "youtube_api_keys"
    }
}