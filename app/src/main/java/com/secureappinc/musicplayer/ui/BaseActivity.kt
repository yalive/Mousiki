package com.secureappinc.musicplayer.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.secureappinc.musicplayer.R

/**
 **********************************
 * Created by Abdelhadi on 4/25/19.
 **********************************
 */
@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    private lateinit var interstitialAd: InterstitialAd

    val TAG = "BaseActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureInterstitialAd()
    }

    private fun configureInterstitialAd() {
        interstitialAd = InterstitialAd(this)
        interstitialAd.adUnitId = getString(R.string.admob_interstitial_id)
        interstitialAd.loadAd(AdRequest.Builder().build())
        interstitialAd.adListener = object : AdListener() {
            override fun onAdLoaded() {
                Log.d(TAG, "onAdLoaded")
                if (interstitialAd.isLoaded) {
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
            }

            override fun onAdOpened() {
                Log.d(TAG, "onAdOpened")
                super.onAdOpened()
            }
        }
    }
}