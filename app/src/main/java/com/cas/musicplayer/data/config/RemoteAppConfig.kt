package com.cas.musicplayer.data.config

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import javax.inject.Inject

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/9/20.
 ***************************************
 */
class RemoteAppConfig @Inject constructor(
    private val firebaseRemoteConfig: FirebaseRemoteConfig
) {

    init {
        firebaseRemoteConfig.fetchAndActivate()
    }

    fun getYoutubeApiKeys(): List<String> {
        return firebaseRemoteConfig.getString(YOUTUBE_API_KEYS).split("###")
    }

    companion object {
        const val YOUTUBE_API_KEYS = "youtube_api_keys"
    }
}