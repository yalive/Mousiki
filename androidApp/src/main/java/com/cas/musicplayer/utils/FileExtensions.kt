package com.cas.musicplayer.utils

import android.content.Context
import com.cas.musicplayer.R
import com.cas.musicplayer.utils.Utils.getStoragePaths
import timber.log.Timber
import java.io.File

/**
 * Created by Fayssel Yabahddou on 6/25/21.
 */

private const val INTERNAL_STORAGE = R.string.internal_storage
private const val EXTERNAL_STORAGE = R.string.external_storage
private const val MEGA_BYTES_SIZE = 1048576

fun File.fixedPath(context: Context): String {
    val storagePaths = getStoragePaths(context)
    if (storagePaths.isEmpty()) return path
    val type = path.contains(storagePaths[0])
    Timber.d("fixedPath()")
    return when{
        storagePaths.isNotEmpty() && type -> path.replace(storagePaths[0], "/${context.getString(INTERNAL_STORAGE)}")
        storagePaths.size > 1 && !type -> path.replace(storagePaths[1], "/${context.getString(EXTERNAL_STORAGE)}")
        else -> path
    }
}

fun File.fixedName(context: Context): String {
    return File(fixedPath(context)).name
}