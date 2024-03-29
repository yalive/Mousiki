package com.cas.musicplayer

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.cas.musicplayer.di.*
import com.cas.musicplayer.player.TAG_PLAYER
import com.cas.musicplayer.ui.common.ads.AdsManager
import com.cas.musicplayer.ui.common.ads.AppOpenManager
import com.cas.musicplayer.ui.local.repository.*
import com.cas.musicplayer.utils.AndroidStrings
import com.cas.musicplayer.utils.ConnectivityState
import com.google.android.gms.ads.MobileAds
import com.mousiki.shared.data.config.RemoteAppConfig
import com.mousiki.shared.data.repository.LocalTrackMapper
import com.mousiki.shared.di.initKoin
import com.mousiki.shared.fs.FileSystem
import com.mousiki.shared.preference.UserPrefs
import com.mousiki.shared.utils.ConnectivityChecker
import com.mousiki.shared.utils.Strings
import com.mousiki.shared.utils.globalAppContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.dsl.module


/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class MusicApp : Application(), KoinComponent {

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var appOpenManager: AppOpenManager? = null

    private var _isInForeground = false
    val isInForeground: Boolean
        get() = _isInForeground

    private var admobInitialized = false


    override fun onCreate() {
        super.onCreate()
        instance = this
        globalAppContext = this
        FileSystem.initialize(this)
        val localSongsRepo = module {
            single { LocalSongsRepository(get()) }
            single { LocalVideosRepository(get()) }
            single {
                LocalTrackMapper(
                    AndroidLocalSongProvider(get())
                )
            }
            single { AlbumRepository(get()) }
            single { LocalArtistRepository(get(), get()) }
            single { FoldersRepository(get(), get()) }
        }

        initKoin(
            appContextModule(this),
            firebaseModule,
            playSongDelegateModule,
            adsDelegateModule,
            viewModelsModule,
            localSongsRepo
        )

        UserPrefs.init(Injector.preferencesHelper.provider)
        configurePreferredTheme()

        MobileAds.initialize(this) {
            admobInitialized = true
            AdsManager.init(applicationScope, get())
        }

        val appConfig = get<RemoteAppConfig>()
        if (appConfig.appOpenAdEnabled()) {
            appOpenManager = AppOpenManager(appConfig)
        }

        ProcessLifecycleOwner.get().lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun onEnterForeground() {
                Log.d(TAG_PLAYER, "onEnterForeground() called")
                _isInForeground = true
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            fun onEnterBackground() {
                Log.d(TAG_PLAYER, "onEnterBackground() called")
                _isInForeground = false
            }
        })
    }


    fun currentActivity(): Activity? = appOpenManager?.currentActivity

    /**
     * Ensure admob SDK is initialized
     */
    suspend fun awaitAdmobSdkInitialized() {
        if (admobInitialized) return
        while (!admobInitialized) {
            delay(50)
        }
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