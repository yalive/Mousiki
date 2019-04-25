package com.secureappinc.musicplayer.ui

import android.annotation.SuppressLint
import android.os.Bundle
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
                if (interstitialAd.isLoaded) {
                    interstitialAd.show()
                }
            }
        }
    }
}