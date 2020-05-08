package com.cas.musicplayer.ui

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.cas.musicplayer.di.injector.injector
import com.cas.musicplayer.utils.AudienceNetworkInitializeHelper
import com.cas.musicplayer.utils.getCurrentLocale
import com.google.firebase.messaging.FirebaseMessaging
import java.util.*


/**
 **********************************
 * Created by Abdelhadi on 4/25/19.
 **********************************
 */
@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    protected val handler = Handler()

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
        injector.rewardedAdDelegate.register(this)
    }


    override fun onPause() {
        super.onPause()
        injector.rewardedAdDelegate.unregister()
    }

    fun hideStatusBar() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    fun showStatusBar() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    @SuppressLint("SourceLockedOrientationActivity")
    fun switchToLandscape() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    @SuppressLint("SourceLockedOrientationActivity")
    fun switchToPortrait() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    fun isLandscape(): Boolean {
        val orientation = resources.configuration.orientation
        return orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    fun lightStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val flags = window.decorView.systemUiVisibility
            window.decorView.systemUiVisibility = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            // window.statusBarColor = requireContext().color(android.color.)
        }
    }

    fun darkStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val flags = window.decorView.systemUiVisibility
            window.decorView.systemUiVisibility =
                flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        } else {
            // window.statusBarColor = color(R.color.colorPrimary)
        }
    }

    private fun subscribeToTopic() {
        FirebaseMessaging.getInstance()
            .subscribeToTopic(getCurrentLocale().toLowerCase(Locale.getDefault()))
    }

    private fun unsubscribeFromTopic() {
        FirebaseMessaging.getInstance()
            .unsubscribeFromTopic(getCurrentLocale().toLowerCase(Locale.getDefault()))
    }
}