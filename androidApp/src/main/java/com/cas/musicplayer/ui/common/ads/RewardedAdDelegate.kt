package com.cas.musicplayer.ui.common.ads

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.cas.musicplayer.R
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.utils.EnvConfig
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.mousiki.shared.data.config.RemoteAppConfig
import com.mousiki.shared.preference.SettingsProvider
import com.mousiki.shared.preference.UserPrefs
import com.mousiki.shared.utils.AnalyticsApi
import com.mousiki.shared.utils.logEvent
import com.unity3d.ads.IUnityAdsListener
import com.unity3d.ads.UnityAds

/**
 ***************************************
 * Created by Y.Abdelhadi on 5/6/20.
 ***************************************
 */
interface RewardedAdDelegate {
    fun register(activity: Activity)
    fun unregister()
}

class RewardedAdDelegateImp(
    private val context: Context,
    private val analytics: AnalyticsApi,
    private val appConfig: RemoteAppConfig,
    private val envConfig: EnvConfig
) : RewardedAdDelegate {

    private val MAX_RETRIES = 5
    private var retriesCount = 0

    private var rewardedAd: RewardedAd? = null
    private var activity: Activity? = null
    private var errorLoadingAd = false
    private val preferencesListener: SharedPreferences.OnSharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { p0, key ->
            val clickTrackCount = UserPrefs.getClickTrackCount()
            val modulo = appConfig.getClickCountToShowReward()
            if (key == UserPrefs.CLICK_TRACK_COUNT && clickTrackCount % modulo == 0) {
                activity?.let { showReward(activity = it) }
            }
        }

    init {
        loadAd()
        UnityAds.addListener(UnityAdsListener())
    }

    override fun register(activity: Activity) {
        this.activity = activity
        context.getSharedPreferences(SettingsProvider.OLD_PREF_NAME, Context.MODE_PRIVATE)
            .registerOnSharedPreferenceChangeListener(preferencesListener)
    }

    override fun unregister() {
        this.activity = null
        context.getSharedPreferences(SettingsProvider.OLD_PREF_NAME, Context.MODE_PRIVATE)
            .unregisterOnSharedPreferenceChangeListener(preferencesListener)
    }

    private fun showReward(activity: Activity) {
        if (UnityAds.isReady(activity.getString(R.string.unity_placement_id))) {
            UnityAds.show(activity, activity.getString(R.string.unity_placement_id))
            return
        }

        if (errorLoadingAd) {
            retriesCount = 0
            loadAd()
        }
        if (rewardedAd == null) return
        rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                PlayerQueue.resume()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {

            }

            override fun onAdShowedFullScreenContent() {
                PlayerQueue.resume()
            }
        }

        rewardedAd?.show(activity) { PlayerQueue.pause() }
    }

    private fun loadAd() {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            context,
            context.getString(R.string.admob_rewarded_ad_id),
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    errorLoadingAd = true
                    retriesCount++
                    if (retriesCount < MAX_RETRIES) {
                        loadAd()
                    } else {
                        analytics.logEvent(ANALYTICS_ERROR_LOAD_AD_RETRY)
                    }
                }

                override fun onAdLoaded(reward: RewardedAd) {
                    rewardedAd = reward
                    if (retriesCount > 0) {
                        analytics.logEvent(
                            ANALYTICS_GOT_REWARD_AFTER_RETRIES,
                            "retries" to retriesCount
                        )
                    }
                    retriesCount = 0
                    errorLoadingAd = false
                }
            })
    }

    companion object {
        private const val ANALYTICS_ERROR_LOAD_AD = "rewarded_ad_not_ready_yet"
        private const val ANALYTICS_ERROR_LOAD_AD_RETRY = "rewarded_ad_error_after_retries"
        private const val ANALYTICS_GOT_REWARD_AFTER_RETRIES = "got_rewarded_ad_after_x_retries"
    }

}

class NoRewardedAdDelegate : RewardedAdDelegate {

    override fun register(activity: Activity) {
        // no-op
    }

    override fun unregister() {
        // no-op
    }
}

class UnityAdsListener : IUnityAdsListener {

    override fun onUnityAdsStart(placementId: String?) {
        PlayerQueue.pause()
    }

    override fun onUnityAdsFinish(placementId: String?, result: UnityAds.FinishState?) {
        PlayerQueue.resume()
    }

    override fun onUnityAdsError(error: UnityAds.UnityAdsError?, message: String?) {
    }

    override fun onUnityAdsReady(placementId: String?) {
    }
}