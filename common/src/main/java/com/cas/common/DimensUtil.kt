package com.cas.common

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
    return try {
        (dp * (requireContext().resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
    } catch (e: Exception) {
        // Returns 0 if fragment not attached
        0
    }
}

fun Fragment.dpToPixel(dp: Int): Int {
    return dpToPixel(dp.toFloat())
}