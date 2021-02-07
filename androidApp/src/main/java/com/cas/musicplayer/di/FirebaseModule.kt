package com.cas.musicplayer.di

import android.annotation.SuppressLint
import com.cas.musicplayer.BuildConfig
import com.cas.musicplayer.firebase.FirebaseConfigDelegate
import com.cas.musicplayer.utils.AndroidAnalytics
import com.cas.musicplayer.utils.Storage
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.firebase.storage.ktx.storage
import com.mousiki.shared.data.config.RemoteAppConfig
import com.mousiki.shared.utils.AnalyticsApi
import com.mousiki.shared.utils.StorageApi
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/9/20.
 ***************************************
 */

@SuppressLint("MissingPermission")
val firebaseModule = module {
    single { provideFirebaseRemoteConfig() }
    single { AndroidAnalytics(FirebaseAnalytics.getInstance(get())) } bind AnalyticsApi::class
    single { Storage(Firebase.storage) } bind StorageApi::class
    single { RemoteAppConfig(FirebaseConfigDelegate(provideFirebaseRemoteConfig()), get(), get()) }
}

private fun provideFirebaseRemoteConfig(): FirebaseRemoteConfig {
    val remoteConfig = Firebase.remoteConfig
    val configSettings = remoteConfigSettings {
        minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 160 else 1800
    }
    remoteConfig.setConfigSettingsAsync(configSettings)
    remoteConfig.setDefaultsAsync(RemoteAppConfig.defaultConfig())
    return remoteConfig
}