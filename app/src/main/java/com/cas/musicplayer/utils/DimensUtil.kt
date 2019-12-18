package com.cas.musicplayer.utils

import android.content.Context
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment


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

fun Fragment.dpToPixel(dp: Float): Int {
    return (dp * (requireContext().resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
}

fun Fragment.dpToPixel(dp: Int): Int {
    return dpToPixel(dp.toFloat())
}