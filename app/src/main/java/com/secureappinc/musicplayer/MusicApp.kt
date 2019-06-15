package com.secureappinc.musicplayer

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.google.android.gms.ads.MobileAds
import com.secureappinc.musicplayer.di.AppComponent
import com.secureappinc.musicplayer.di.ComponentProvider
import com.secureappinc.musicplayer.di.DaggerAppComponent
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