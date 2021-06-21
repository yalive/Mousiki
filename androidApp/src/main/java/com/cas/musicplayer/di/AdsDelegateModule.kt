package com.cas.musicplayer.di

import com.cas.musicplayer.BuildConfig
import com.cas.musicplayer.ui.common.ads.*
import com.cas.musicplayer.ui.common.ads.facebook.FacebookAdsDelegateImp
import com.cas.musicplayer.utils.EnvConfig
import com.mousiki.shared.ads.FacebookAdsDelegate
import com.mousiki.shared.ads.GetListAdsDelegate
import com.mousiki.shared.data.config.RemoteAppConfig
import org.koin.dsl.module

val adsDelegateModule = module {
    single<GetListAdsDelegate> {
        GetListAdsDelegateImp(get(), get())
    }

    single<FacebookAdsDelegate> {
        FacebookAdsDelegateImp()
    }

    single<RewardedAdDelegate> {
        val config: RemoteAppConfig = get()
        if (config.rewardAdOn() && !BuildConfig.DEBUG) {
            RewardedAdDelegateImp(
                context = get(),
                analytics = get(),
                appConfig = get(),
                envConfig = EnvConfig()
            )
        } else {
            NoRewardedAdDelegate()
        }
    }
}