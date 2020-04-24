package com.cas.musicplayer.utils

import android.graphics.Bitmap
import androidx.palette.graphics.Palette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/12/20.
 ***************************************
 */
/*suspend fun Bitmap.getPalette(): Palette? = suspendCoroutine { continuation ->
    Palette.from(this).generate { palette ->
        continuation.resume(palette)
    }
}*/

suspend fun Bitmap.getPalette(): Palette? = withContext(Dispatchers.Default) {
    Palette.from(this@getPalette).generate()
}