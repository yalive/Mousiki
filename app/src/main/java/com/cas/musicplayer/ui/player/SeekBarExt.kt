package com.cas.musicplayer.ui.player

import android.os.Build
import android.widget.SeekBar

/**
 ***************************************
 * Created by Y.Abdelhadi on 5/11/20.
 ***************************************
 */

fun SeekBar.animateProgress(value: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        setProgress(value, true)
    } else {
        progress = value
    }
}