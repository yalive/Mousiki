package com.cas.musicplayer.di

import android.content.Context
import com.cas.musicplayer.R
import com.cas.musicplayer.data.config.RemoteAppConfig
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/9/20.
 ***************************************
 */
@Module
object ConfigModule {

    @Singleton
    @Provides
    @JvmStatic
    fun provideAppConfigDataSource(
        remoteConfig: FirebaseRemoteConfig,
        context: Context
    ): RemoteAppConfig {
        return RemoteAppConfig(remoteConfig, context)
    }

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
}