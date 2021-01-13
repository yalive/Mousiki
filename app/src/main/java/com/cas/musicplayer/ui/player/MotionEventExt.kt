package com.cas.musicplayer.ui.player

import android.view.MotionEvent
import kotlin.math.abs

fun MotionEvent?.name(): String {
    return when (this?.actionMasked) {
        MotionEvent.ACTION_DOWN -> "Down"
        MotionEvent.ACTION_MOVE -> "Move"
        MotionEvent.ACTION_UP -> "Up"
        MotionEvent.ACTION_CANCEL -> "Cancel"
        MotionEvent.ACTION_POINTER_DOWN -> "DownPointer"
        MotionEvent.ACTION_POINTER_UP -> "UpPointer"
        else -> "Unknown"
    }
}

fun MotionEvent.yDistanceTo(yCoordinate: Float) = abs(y - yCoordinate)
fun MotionEvent.xDistanceTo(xCoordinate: Float) = abs(x - xCoordinate)