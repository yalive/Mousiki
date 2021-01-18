package com.cas.musicplayer.ui.common.ads

import android.content.Context
import com.cas.musicplayer.BuildConfig
import com.cas.musicplayer.data.config.EnvConfig
import com.cas.musicplayer.data.config.RemoteAppConfig
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 ***************************************
 * Created by Y.Abdelhadi on 5/3/20.
 ***************************************
 */
@Module(includes = [RewardedAdDelegateModule::class])
interface AdsDelegateModule {

    @Singleton
    @Binds
    fun bindsNativeAdsDelegate(delegate: GetListAdsDelegateImp): GetListAdsDelegate
}

@Module
class RewardedAdDelegateModule {

    @Provides
    @Singleton
    fun providesRewardedAdDelegate(
        context: Context,
        appConfig: RemoteAppConfig,
        analytics: FirebaseAnalytics,
        envConfig: EnvConfig
    ): RewardedAdDelegate {
        return if (appConfig.rewardAdOn() && !BuildConfig.DEBUG) {
            RewardedAdDelegateImp(
                context = context,
                analytics = analytics,
                appConfig = appConfig,
                envConfig = envConfig
            )
        } else {
            NoRewardedAdDelegate()
        }
    }
}