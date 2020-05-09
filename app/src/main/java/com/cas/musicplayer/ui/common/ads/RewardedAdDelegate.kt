package com.cas.musicplayer.ui.common.ads

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.cas.musicplayer.MusicApp
import com.cas.musicplayer.R
import com.cas.musicplayer.data.config.EnvConfig
import com.cas.musicplayer.data.config.RemoteAppConfig
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.utils.UserPrefs
import com.cas.musicplayer.utils.toastCentred
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

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
    private val analytics: FirebaseAnalytics,
    private val appConfig: RemoteAppConfig,
    private val envConfig: EnvConfig
) : RewardedAdDelegate {

    private lateinit var rewardedAd: RewardedAd
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
    }

    override fun register(activity: Activity) {
        this.activity = activity
        UserPrefs.getPrefs().registerOnSharedPreferenceChangeListener(preferencesListener)
    }

    override fun unregister() {
        this.activity = null
        UserPrefs.getPrefs().unregisterOnSharedPreferenceChangeListener(preferencesListener)
    }

    private fun showReward(activity: Activity) {
        if (errorLoadingAd) {
            loadAd()
        }
        if (!rewardedAd.isLoaded) {
            analytics.logEvent(ANALYTICS_ERROR_LOAD_AD, null)
            return
        }
        PlayerQueue.pause()
        rewardedAd.show(activity, object : RewardedAdCallback() {
            override fun onUserEarnedReward(p0: RewardItem) {
                PlayerQueue.pause()
            }

            override fun onRewardedAdOpened() {
                PlayerQueue.hideVideo()
                PlayerQueue.pause()
            }

            override fun onRewardedAdClosed() {
                loadAd()
                PlayerQueue.resume()
                PlayerQueue.showVideo()
            }
        })
    }

    private fun loadAd() {
        rewardedAd = RewardedAd(context, context.getString(R.string.admob_rewarded_ad_id))
        rewardedAd.loadAd(AdRequest.Builder().build(), object : RewardedAdLoadCallback() {
            override fun onRewardedAdFailedToLoad(errorCode: Int) {
                errorLoadingAd = true
                if (envConfig.isDev()) {
                    MusicApp.get().toastCentred("Error load ad: $errorCode")
                }
            }

            override fun onRewardedAdLoaded() {
                errorLoadingAd = false
            }
        })
    }

    companion object {
        private const val ANALYTICS_ERROR_LOAD_AD = "rewarded_ad_not_ready_yet"
    }

}

class NoRewardedAdDelegate @Inject constructor() : RewardedAdDelegate {

    override fun register(activity: Activity) {
        // no-op
    }

    override fun unregister() {
        // no-op
    }
}