package com.cas.musicplayer.utils

import android.content.Context
import android.util.DisplayMetrics


/**
 **********************************
 * Created by Abdelhadi on 3/24/19.
 **********************************
 */

fun dpToPixel(dp: Float, context: Context): Float {
    return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}


fun pixelsToDp(px: Float, context: Context): Float {
    return px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}