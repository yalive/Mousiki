package com.cas.musicplayer

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.cas.musicplayer.di.AppComponent
import com.cas.musicplayer.di.ComponentProvider
import com.cas.musicplayer.di.DaggerAppComponent
import com.cas.musicplayer.utils.UserPrefs
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.facebook.ads.AudienceNetworkAds
import com.google.android.gms.ads.MobileAds
import io.fabric.sdk.android.Fabric


/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class MusicApp : Application(), ComponentProvider {

    private var _isInForeground = false
    val isInForeground: Boolean
        get() = _isInForeground

    override val component: AppComponent by lazy {
        DaggerAppComponent
            .factory()
            .create(this)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        configurePreferredTheme()
        if (AudienceNetworkAds.isInAdsProcess(this)) {
            return
        }
        MobileAds.initialize(this, getString(R.string.admob_app_id))
        val crashlytics = Crashlytics.Builder()
            .core(CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
            .build()
        Fabric.with(this, crashlytics)

        ProcessLifecycleOwner.get().lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun onEnterForeground() {
                _isInForeground = true
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            fun onEnterBackground() {
                _isInForeground = false
            }
        })
    }

    companion object {
        private lateinit var instance: MusicApp

        fun get(): MusicApp {
            return instance
        }
    }

    private fun configurePreferredTheme() {
        val preferredTheme = UserPrefs.getThemeModeValue()
        if (preferredTheme == UserPrefs.THEME_AUTOMATIC) {
            UserPrefs.setThemeModeValue(UserPrefs.THEME_AUTOMATIC)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        } else if (preferredTheme == UserPrefs.THEME_LIGHT) {
            UserPrefs.setThemeModeValue(UserPrefs.THEME_LIGHT)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else if (preferredTheme == UserPrefs.THEME_DARK) {
            UserPrefs.setThemeModeValue(UserPrefs.THEME_DARK)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }
}