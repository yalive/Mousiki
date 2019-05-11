package com.secureappinc.musicplayer.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.base.common.EventObserver
import com.secureappinc.musicplayer.player.ClickVideoListener
import com.secureappinc.musicplayer.utils.UserPrefs

/**
 **********************************
 * Created by Abdelhadi on 4/25/19.
 **********************************
 */
@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    private var hasShownFirstInterAds = false

    private lateinit var interstitialAd: InterstitialAd

    val TAG = "BaseActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureInterstitialAd()

        observeClickVideo()
    }

    private fun configureInterstitialAd() {
        interstitialAd = InterstitialAd(this)
        interstitialAd.adUnitId = getString(R.string.admob_interstitial_id)
        loadInterstitialAd()
        interstitialAd.adListener = object : AdListener() {
            override fun onAdLoaded() {
                Log.d(TAG, "onAdLoaded")
                if (interstitialAd.isLoaded && !hasShownFirstInterAds) {
                    hasShownFirstInterAds = true
                    interstitialAd.show()
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
            }

            override fun onAdOpened() {
                Log.d(TAG, "onAdOpened")
                super.onAdOpened()
            }
        }
    }

    fun loadInterstitialAd() {
        interstitialAd.loadAd(AdRequest.Builder().addTestDevice("8D18CFA4FEE362E160E97DB5E6D6E770").build())
    }

    fun showInterstitialAd() {
        if (interstitialAd.isLoaded) {
            interstitialAd.show()
        }
    }

    fun observeClickVideo() {
        ClickVideoListener.observe(this, EventObserver {
            val clickTrackCount = UserPrefs.getClickTrackCount()
            Log.d(TAG, "Click track count = $clickTrackCount")
            if (clickTrackCount % 3 == 0L) {
                showInterstitialAd()
            }
        })
    }
}