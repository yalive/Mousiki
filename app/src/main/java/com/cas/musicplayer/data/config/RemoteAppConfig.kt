package com.cas.musicplayer.data.config

import android.content.Context
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
    private val context: Context
) {

    init {
        firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                val instance = FirebaseAnalytics.getInstance(context)
                instance.logEvent("error_fetch_remote_config", null)
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