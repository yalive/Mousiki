package com.cas.musicplayer.di

import android.content.Context
import com.cas.musicplayer.R
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
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

/*    @Singleton
    @Provides
    @JvmStatic
    fun provideAppConfigDataSource(
        remoteConfig: FirebaseRemoteConfig,
        connectivityState: ConnectivityState,
        context: Context
    ): RemoteAppConfig {
        return RemoteAppConfig(remoteConfig, connectivityState, context)
    }*/

    @Singleton
    @Provides
    @JvmStatic
    fun provideFirebaseRemoteConfig(): FirebaseRemoteConfig {
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        remoteConfig.setConfigSettingsAsync(configSettings)
        return remoteConfig
    }

    @Singleton
    @Provides
    @JvmStatic
    fun provideFirebaseAnalytics(context: Context): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(context)
    }

    @Singleton
    @Provides
    @JvmStatic
    fun provideFirebaseStorage(): FirebaseStorage {
        return Firebase.storage
    }
}