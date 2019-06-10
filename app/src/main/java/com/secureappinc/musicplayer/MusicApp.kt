package com.secureappinc.musicplayer

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.google.android.gms.ads.MobileAds
import com.secureappinc.musicplayer.di.AppComponent
import com.secureappinc.musicplayer.di.DaggerAppComponent
import io.fabric.sdk.android.Fabric


/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class MusicApp : Application() {

    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        MobileAds.initialize(this, getString(R.string.admob_app_id))
        Fabric.with(this, Crashlytics())

        appComponent = DaggerAppComponent.create()
    }

    companion object {
        private lateinit var instance: MusicApp

        fun get(): MusicApp {
            return instance
        }
    }
}