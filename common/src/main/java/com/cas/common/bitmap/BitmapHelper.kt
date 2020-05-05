package com.cas.common.bitmap

import android.graphics.Bitmap
import android.graphics.Matrix


/**
 ***************************************
 * Created by Y.Abdelhadi on 5/5/20.
 ***************************************
 */

fun Bitmap.resize(newWidth: Int, newHeight: Int): Bitmap? {
    val scaleWidth = newWidth.toFloat() / width
    val scaleHeight = newHeight.toFloat() / height
    val matrix = Matrix()
    matrix.postScale(scaleWidth, scaleHeight)
    val resizedBitmap: Bitmap = Bitmap.createBitmap(
        this, 0, 0, width, height, matrix, false
    )
    recycle()
    return resizedBitmap
}