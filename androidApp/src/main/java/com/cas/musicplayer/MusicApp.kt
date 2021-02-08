package com.cas.musicplayer

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.cas.musicplayer.di.*
import com.cas.musicplayer.ui.common.ads.AdsManager
import com.cas.musicplayer.utils.AndroidStrings
import com.cas.musicplayer.utils.ConnectivityState
import com.facebook.ads.AudienceNetworkAds
import com.google.android.gms.ads.MobileAds
import com.mousiki.shared.di.initKoin
import com.mousiki.shared.fs.FileSystem
import com.mousiki.shared.preference.UserPrefs
import com.mousiki.shared.utils.ConnectivityChecker
import com.mousiki.shared.utils.Strings
import com.mousiki.shared.utils.globalAppContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module


/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class MusicApp : Application() {

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var _isInForeground = false
    val isInForeground: Boolean
        get() = _isInForeground

    override fun onCreate() {
        super.onCreate()
        instance = this
        globalAppContext = this
        FileSystem.initialize(this)
        initKoin(
            appContextModule(this),
            firebaseModule,
            playSongDelegateModule,
            adsDelegateModule,
            viewModelsModule
        )

        UserPrefs.init(Injector.preferencesHelper.provider)
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

private fun appContextModule(appContext: MusicApp) = module {
    single<Context> { appContext }
    single<ConnectivityChecker> { ConnectivityState(get()) }
    single<Strings> { AndroidStrings }
}