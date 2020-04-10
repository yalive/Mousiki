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
        val youtubeApiKeys = getYoutubeApiKeys()
        firebaseRemoteConfig.fetchAndActivate()
        /*firebaseRemoteConfig.fetch(0).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val youtubeApiKeys1 = getYoutubeApiKeys()
                print("")
            } else {
                print("")
            }
        }*/
    }

    fun getYoutubeApiKeys(): List<String> {
        return firebaseRemoteConfig.getString(YOUTUBE_API_KEYS).split("###")
    }

    companion object {
        const val YOUTUBE_API_KEYS = "youtube_api_keys"
    }
}