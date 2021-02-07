package com.cas.musicplayer.firebase

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.mousiki.shared.data.config.RemoteConfigDelegate

class FirebaseConfigDelegate(
    private val firebaseRemoteConfig: FirebaseRemoteConfig
) : RemoteConfigDelegate {

    override fun fetchAndActivate(completionHandler: (Boolean) -> Unit) {
        firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            completionHandler.invoke(task.isSuccessful)
        }
    }

    override fun getBoolean(key: String): Boolean {
        return firebaseRemoteConfig.getBoolean(key)
    }

    override fun getString(key: String): String {
        return firebaseRemoteConfig.getString(key)
    }

    override fun getInt(key: String): Int {
        return firebaseRemoteConfig.getLong(key).toInt()
    }
}