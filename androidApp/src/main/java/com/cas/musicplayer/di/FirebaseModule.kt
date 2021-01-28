package com.cas.musicplayer.di

import android.content.Context
import com.cas.musicplayer.BuildConfig
import com.cas.musicplayer.R
import com.cas.musicplayer.utils.AndroidAnalytics
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.mousiki.shared.utils.AnalyticsApi
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/9/20.
 ***************************************
 */
@Module
object FirebaseModule {

    @Singleton
    @Provides
    @JvmStatic
    fun provideFirebaseRemoteConfig(): FirebaseRemoteConfig {
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 160 else 1800
        }
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        remoteConfig.setConfigSettingsAsync(configSettings)
        return remoteConfig
    }

    @Singleton
    @Provides
    @JvmStatic
    fun provideAnalytics(
        context: Context
    ): AnalyticsApi {
        val analytics = FirebaseAnalytics.getInstance(context)
        return AndroidAnalytics(analytics)
    }

    @Singleton
    @Provides
    @JvmStatic
    fun provideFirebaseStorage(): FirebaseStorage {
        return Firebase.storage
    }
}