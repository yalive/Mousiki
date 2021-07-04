package com.cas.musicplayer.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.utils.AudienceNetworkInitializeHelper
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import com.mousiki.shared.utils.getCurrentLocale
import java.util.*


/**
 **********************************
 * Created by Abdelhadi on 4/25/19.
 **********************************
 */
@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // If you call AudienceNetworkAds.buildInitSettings(Context).initialize()
        // in Application.onCreate() this call is not really necessary.
        // Otherwise call initialize() onCreate() of getSongs Activities that contain ads or
        // from onCreate() of your Splash Activity.
        AudienceNetworkInitializeHelper.initialize(this)
        subscribeToTopic()
    }

    override fun onResume() {
        super.onResume()
        Injector.rewardedAdDelegate.register(this)
    }


    override fun onPause() {
        super.onPause()
        Injector.rewardedAdDelegate.unregister()
    }

    private fun subscribeToTopic() {
        try {
            FirebaseMessaging.getInstance()
                .subscribeToTopic(getCurrentLocale().toLowerCase(Locale.getDefault()))
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(
                Exception(
                    "Unable to subscribe to topic ${
                        getCurrentLocale().toLowerCase(
                            Locale.getDefault()
                        )
                    }", e
                )
            )
        }
    }

    private fun unsubscribeFromTopic() {
        FirebaseMessaging.getInstance()
            .unsubscribeFromTopic(getCurrentLocale().toLowerCase(Locale.getDefault()))
    }
}