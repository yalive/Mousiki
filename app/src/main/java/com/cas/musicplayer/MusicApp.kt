package com.cas.musicplayer

import android.app.Application
import com.cas.musicplayer.di.AppComponent
import com.cas.musicplayer.di.ComponentProvider
import com.cas.musicplayer.di.DaggerAppComponent
import com.crashlytics.android.Crashlytics
import com.facebook.ads.AudienceNetworkAds
import com.google.android.gms.ads.MobileAds
import io.fabric.sdk.android.Fabric


/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class MusicApp : Application(), ComponentProvider {

    override val component: AppComponent by lazy {
        DaggerAppComponent
            .factory()
            .create(this)
    }

    override fun onCreate() {
        super.onCreate()

        if (AudienceNetworkAds.isInAdsProcess(this)) {

            return
        }
        instance = this
        MobileAds.initialize(this, getString(R.string.admob_app_id))
        Fabric.with(this, Crashlytics())
    }

    companion object {
        private lateinit var instance: MusicApp

        fun get(): MusicApp {
            return instance
        }
    }
}