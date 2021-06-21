package com.cas.musicplayer.ui.common.ads

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.annotation.Keep
import androidx.lifecycle.Lifecycle.Event.ON_START
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.cas.musicplayer.MusicApp
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import com.mousiki.shared.data.config.RemoteAppConfig
import java.util.*


/**
 * Created by Fayssel Yabahddou on 6/1/21.
 */
class AppOpenManager(
    private val appConfig: RemoteAppConfig
) : Application.ActivityLifecycleCallbacks, LifecycleObserver {
    private val LOG_TAG = "AppOpenManager"
    private val AD_UNIT_ID = "ca-app-pub-6125597516229421/1325389808"
    private var appOpenAd: AppOpenAd? = null

    private lateinit var loadCallback: AppOpenAdLoadCallback

    private var currentActivity: Activity? = null

    private var isShowingAd = false

    private var loadTime: Long = 0

    private var appOpenCount = 0

    init {
        MusicApp.get().registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    /** Shows the ad if one isn't already showing.  */
    fun showAdIfAvailable() {
        // Only show ad if there is not already an app open ad currently showing
        // and an ad is available.
        if (!isShowingAd && isAdAvailable()) {
            val fullScreenContentCallback: FullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        // Set the reference to null so isAdAvailable() returns false.
                        appOpenAd = null
                        isShowingAd = false
                        fetchAd()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError?) {}
                    override fun onAdShowedFullScreenContent() {
                        isShowingAd = true
                    }
                }
            appOpenAd?.fullScreenContentCallback = fullScreenContentCallback
            appOpenAd?.show(currentActivity)
        } else {
            fetchAd()
        }
    }

    /** Request an ad  */
    fun fetchAd() {

        // Have unused ad, no need to fetch another.
        // Have unused ad, no need to fetch another.
        if (isAdAvailable()) {
            return
        }

        loadCallback = object : AppOpenAdLoadCallback() {
            /**
             * Called when an app open ad has loaded.
             *
             * @param ad the loaded app open ad.
             */
            override fun onAdLoaded(ad: AppOpenAd) {
                appOpenAd = ad
                loadTime = Date().time
            }

            /**
             * Called when an app open ad has failed to load.
             *
             * @param loadAdError the error.
             */
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                // Handle the error.
            }
        }
        val request = getAdRequest()
        AppOpenAd.load(
            MusicApp.get(), AD_UNIT_ID, request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback
        )
    }

    /** Creates and returns ad request.  */
    private fun getAdRequest(): AdRequest {
        return AdRequest.Builder().build()
    }

    /** Utility method that checks if ad exists and can be shown.  */
    private fun isAdAvailable(): Boolean {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
    }

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityDestroyed(activity: Activity) {
        currentActivity = null
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }


    /** LifecycleObserver methods  */
    @Keep
    @OnLifecycleEvent(ON_START)
    fun onStart() {
        val frequency = appConfig.appOpenAdFrequency()
        if (appOpenCount % frequency == 0) {
            showAdIfAvailable()
        }
        appOpenCount++
    }

    /** Utility method to check if ad was loaded more than n hours ago.  */
    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference: Long = Date().time - this.loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }
}