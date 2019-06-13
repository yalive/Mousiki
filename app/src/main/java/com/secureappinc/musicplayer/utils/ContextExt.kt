package com.secureappinc.musicplayer.utils

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.LifecycleService

/**
 **********************************
 * Created by Abdelhadi on 4/6/19.
 **********************************
 */

data class ScreenSize(val widthPx: Int, val heightPx: Int)

fun Context.dpToPixel(dp: Float): Int {
    return dpToPixel(dp, this).toInt()
}

fun Context.pixelsToDp(px: Float): Float {
    return pixelsToDp(px, this)
}

fun Context.canDrawOverApps() =
    (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(
        this
    ))


fun Context.screenSize(): ScreenSize {

    //Add the view to the window
    val windowManager = getSystemService(LifecycleService.WINDOW_SERVICE) as WindowManager?

    val displayMetrics = DisplayMetrics()

    windowManager?.defaultDisplay?.getMetrics(displayMetrics)
    val h = displayMetrics.heightPixels
    val w = displayMetrics.widthPixels

    return ScreenSize(w, h)
}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}