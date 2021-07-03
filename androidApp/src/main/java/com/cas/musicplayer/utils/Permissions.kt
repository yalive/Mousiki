package com.cas.musicplayer.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

fun Context.readStoragePermissionsGranted(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED
}

fun Fragment.readStoragePermissionsGranted(): Boolean {
    return requireContext().readStoragePermissionsGranted()
}