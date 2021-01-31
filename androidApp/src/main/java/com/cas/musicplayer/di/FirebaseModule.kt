package com.cas.musicplayer.di

import android.content.Context
import com.cas.musicplayer.BuildConfig
import com.cas.musicplayer.R
import com.cas.musicplayer.utils.AndroidAnalytics
import com.cas.musicplayer.utils.Storage
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.firebase.storage.ktx.storage
import com.mousiki.shared.data.config.RemoteAppConfig
import com.mousiki.shared.preference.PreferencesHelper
import com.mousiki.shared.utils.AnalyticsApi
import com.mousiki.shared.utils.StorageApi
import kotlinx.serialization.json.Json
import org.koin.dsl.module

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/9/20.
 ***************************************
 */

val firebaseModule = module {
    single { provideFirebaseRemoteConfig() }
    single { provideAnalytics(get()) }
    single { providesStorage() }
    single { providesRemoteAppConfig(get(), get(), get(), get()) }
}

private fun provideFirebaseRemoteConfig(): FirebaseRemoteConfig {
    val remoteConfig = Firebase.remoteConfig
    val configSettings = remoteConfigSettings {
        minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 160 else 1800
    }
    remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
    remoteConfig.setConfigSettingsAsync(configSettings)
    return remoteConfig
}

private fun provideAnalytics(
    context: Context
): AnalyticsApi {
    val analytics = FirebaseAnalytics.getInstance(context)
    return AndroidAnalytics(analytics)
}


private fun providesStorage(): StorageApi {
    return Storage(Firebase.storage)
}

private fun providesRemoteAppConfig(
    firebaseRemoteConfig: FirebaseRemoteConfig,
    json: Json,
    context: Context,
    preferencesHelper: PreferencesHelper
) = RemoteAppConfig(
    firebaseRemoteConfig,
    json,
    context,
    preferencesHelper
)