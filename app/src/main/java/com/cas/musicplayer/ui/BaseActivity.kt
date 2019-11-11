package com.cas.musicplayer.ui

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.cas.musicplayer.base.common.EventObserver
import com.cas.musicplayer.base.common.asEvent
import com.cas.musicplayer.player.ClickVideoListener
import com.cas.musicplayer.player.OnShowAdsListener
import com.cas.musicplayer.utils.AudienceNetworkInitializeHelper
import com.cas.musicplayer.utils.RequestAdsLiveData
import com.cas.musicplayer.utils.UserPrefs


/**
 **********************************
 * Created by Abdelhadi on 4/25/19.
 **********************************
 */
@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    private var hasShownFirstInterAds = false

    private lateinit var interstitialAd: InterstitialAd

    protected val handler = Handler()

    val TAG = "BaseActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //configureInterstitialAd()

        observeClickVideo()

        // If you call AudienceNetworkAds.buildInitSettings(Context).initialize()
        // in Application.onCreate() this call is not really necessary.
        // Otherwise call initialize() onCreate() of all Activities that contain ads or
        // from onCreate() of your Splash Activity.
        AudienceNetworkInitializeHelper.initialize(this)
    }

    private fun configureInterstitialAd() {
        interstitialAd = InterstitialAd(this)
        interstitialAd.adUnitId = getString(com.cas.musicplayer.R.string.admob_interstitial_id)
        loadInterstitialAd()
        interstitialAd.adListener = object : AdListener() {
            override fun onAdLoaded() {
                Log.d(TAG, "onAdLoaded")
                if (!hasShownFirstInterAds) {
                    hasShownFirstInterAds = true
                    showInterstitialAd()
                }
            }

            override fun onAdImpression() {
                Log.d(TAG, "onAdImpression")
                super.onAdImpression()
            }

            override fun onAdLeftApplication() {
                Log.d(TAG, "onAdLeftApplication")
                super.onAdLeftApplication()
            }

            override fun onAdClicked() {
                Log.d(TAG, "onAdClicked")
                super.onAdClicked()
            }

            override fun onAdFailedToLoad(p0: Int) {
                Log.d(TAG, "onAdFailedToLoad: $p0")
                super.onAdFailedToLoad(p0)
            }

            override fun onAdClosed() {
                Log.d(TAG, "onAdClosed")
                super.onAdClosed()
                loadInterstitialAd()
                onAdsClosed()
            }

            override fun onAdOpened() {
                Log.d(TAG, "onAdOpened")
                super.onAdOpened()
            }
        }
    }


    fun loadInterstitialAd() {
        interstitialAd.loadAd(AdRequest.Builder().build())
    }


    fun showInterstitialAd() {
        print("Show Ads")
        if (interstitialAd.isLoaded) {
            onAdsShown()
            handler.postDelayed({
                interstitialAd.show()
            }, 1000)
        }
    }

    fun onAdsShown() {
        OnShowAdsListener.value = true.asEvent()
    }

    fun onAdsClosed() {
        OnShowAdsListener.value = false.asEvent()
    }

    fun observeAdsRequests() {
        RequestAdsLiveData.observe(this, Observer {
            showInterstitialAd()
        })
    }

    fun isFullScreen(): Boolean {
        return window.attributes.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN != 0
    }

    fun hideStatusBar() {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    fun showStatusBar() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    fun switchToLandscape() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    fun switchToPortrait() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    fun isLandscape(): Boolean {
        val orientation = resources.configuration.orientation
        return orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    fun observeClickVideo() {
        ClickVideoListener.observe(this, EventObserver {
            val clickTrackCount = UserPrefs.getClickTrackCount()
            Log.d(TAG, "Click track count = $clickTrackCount")
            if (clickTrackCount % 4 == 0) {
                showInterstitialAd()
            }
        })
    }
}