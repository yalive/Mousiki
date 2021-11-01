package com.cas.musicplayer.utils

import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import com.cas.musicplayer.MusicApp

object VideoPlayerSettings {

    @SuppressLint("InlinedApi")
    fun canEnterPiPMode(): Boolean {
        if (!isPiPSupported()) return false
        val context = MusicApp.get()
        val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        return AppOpsManager.MODE_ALLOWED == appOpsManager.checkOpNoThrow(
            AppOpsManager.OPSTR_PICTURE_IN_PICTURE,
            Process.myUid(), context.packageName
        )
    }

    fun isPiPSupported(): Boolean {
        val context = MusicApp.get()
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && context.packageManager.hasSystemFeature(
            PackageManager.FEATURE_PICTURE_IN_PICTURE
        )
    }
}