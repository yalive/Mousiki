package com.cas.musicplayer.ui.common.ads

import android.app.Activity
import android.content.Context
import com.cas.musicplayer.data.config.RemoteAppConfig
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

/**
 ***************************************
 * Created by Y.Abdelhadi on 5/16/20.
 ***************************************
 */
interface InterstitialDelegate {
    fun register(activity: Activity)
    fun unregister()
}

class InterstitialDelegateImp(
    private val context: Context,
    private val analytics: FirebaseAnalytics,
    private val appConfig: RemoteAppConfig
) : InterstitialDelegate {

    override fun register(activity: Activity) {

    }

    override fun unregister() {

    }
}

class NoInterstitialDelegate @Inject constructor() : InterstitialDelegate {

    override fun register(activity: Activity) {
        // no-op
    }

    override fun unregister() {
        // no-op
    }
}