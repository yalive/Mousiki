package com.cas.musicplayer

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.cas.musicplayer.di.AppComponent
import com.cas.musicplayer.di.ComponentProvider
import com.cas.musicplayer.di.DaggerAppComponent
import com.cas.musicplayer.ui.common.ads.AdsManager
import com.cas.musicplayer.ui.player.TAG_SERVICE
import com.cas.musicplayer.utils.UserPrefs
import com.facebook.ads.AudienceNetworkAds
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob


/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class MusicApp : Application(), ComponentProvider {

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

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
        if (AudienceNetworkAds.isInitialized(this)) {
            return
        }
        MobileAds.initialize(this, getString(R.string.admob_app_id))

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
        AdsManager.init(applicationScope)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Log.d(TAG_SERVICE, "onLowMemory: app")
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Log.d(TAG_SERVICE, "onTrimMemory: app with flag=${trimFlag(level)}")
    }

    override fun onTerminate() {
        super.onTerminate()
        Log.d(TAG_SERVICE, "onTerminate: app")
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

    fun trimFlag(flag: Int): String {
        return when (flag) {
            TRIM_MEMORY_BACKGROUND -> "TRIM_MEMORY_BACKGROUND"
            TRIM_MEMORY_COMPLETE -> "TRIM_MEMORY_COMPLETE"
            TRIM_MEMORY_MODERATE -> "TRIM_MEMORY_MODERATE"
            TRIM_MEMORY_RUNNING_CRITICAL -> "TRIM_MEMORY_RUNNING_CRITICAL"
            TRIM_MEMORY_RUNNING_LOW -> "TRIM_MEMORY_RUNNING_LOW"
            TRIM_MEMORY_RUNNING_MODERATE -> "TRIM_MEMORY_RUNNING_MODERATE"
            TRIM_MEMORY_UI_HIDDEN -> "TRIM_MEMORY_UI_HIDDEN"
            else -> "Unknown"
        }
    }
}