package com.cas.musicplayer.utils

import android.Manifest
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.cas.musicplayer.MusicApp

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/28/20.
 ***************************************
 */
object SystemSettings {

    fun canEnterPiPMode(): Boolean {
        if (!supportPip()) return false
        val context = MusicApp.get()
        val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        return AppOpsManager.MODE_ALLOWED == appOpsManager.checkOpNoThrow(
            AppOpsManager.OPSTR_PICTURE_IN_PICTURE,
            Process.myUid(), context.packageName
        )
    }

    fun supportPip(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            MusicApp.get().packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
        } else false
    }

    fun canDrawOverApps(context: Context): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(context))
    }

    fun canWriteSettings(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.System.canWrite(context)
        } else {
            val status =
                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_SETTINGS)
            status == PackageManager.PERMISSION_GRANTED
        }
    }

    fun enableSettingModification(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.data = "package:${context.packageName}".toUri()
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            }
        }
    }

    fun enableSettingModification(fragment: Fragment, rqCode: Int) {
        val context = fragment.context ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.data = "package:${context.packageName}".toUri()
            if (intent.resolveActivity(context.packageManager) != null) {
                fragment.startActivityForResult(intent, rqCode)
            }
        }
    }
}