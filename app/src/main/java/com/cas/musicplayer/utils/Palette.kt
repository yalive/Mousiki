package com.cas.musicplayer.utils

import android.graphics.Bitmap
import androidx.palette.graphics.Palette
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/12/20.
 ***************************************
 */
suspend fun Bitmap.getPallet(): Palette? = suspendCoroutine { continuation ->
    Palette.from(this).generate { palette ->
        continuation.resume(palette)
    }
}