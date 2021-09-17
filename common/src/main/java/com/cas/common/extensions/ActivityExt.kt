package com.cas.common.extensions

import android.app.Activity
import android.os.Build
import android.view.View

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/29/20.
 ***************************************
 */

fun Activity.enterFullScreen() {
    var flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_FULLSCREEN
    flags = flags or (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    window.decorView.systemUiVisibility = flags
}

fun Activity.exitFullScreen() {
    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
}

val Activity.isInPictureInPictureModeCompact: Boolean
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) isInPictureInPictureMode else false