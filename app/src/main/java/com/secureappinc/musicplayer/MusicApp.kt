package com.secureappinc.musicplayer

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.google.android.gms.ads.MobileAds
import io.fabric.sdk.android.Fabric


/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class MusicApp : Application() {

    companion object {
        private lateinit var instance: MusicApp

        fun get(): MusicApp {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        MobileAds.initialize(this, getString(R.string.admob_app_id))
        Fabric.with(this, Crashlytics())
    }
}