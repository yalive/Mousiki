package com.cas.musicplayer.utils

import android.app.ActivityManager
import android.content.Context


/**
 ***************************************
 * Created by Y.Abdelhadi on 4/30/20.
 ***************************************
 */

fun Context.isServiceRunning(serviceClass: Class<*>): Boolean {
    val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
    for (service in manager!!.getRunningServices(Int.MAX_VALUE)) {
        if (serviceClass.name == service.service.className) {
            return true
        }
    }
    return false
}