package com.cas.musicplayer.ui.player

import android.view.MotionEvent

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