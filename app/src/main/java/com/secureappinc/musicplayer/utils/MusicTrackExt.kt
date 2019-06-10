package com.secureappinc.musicplayer.utils

import com.secureappinc.musicplayer.data.enteties.MusicTrack
import java.util.regex.Pattern

/**
 **********************************
 * Created by Abdelhadi on 4/13/19.
 **********************************
 */

val LENGTH_PATTERN = Pattern.compile("^PT(?:([0-9]+)H)?(?:([0-9]+)M)?(?:([0-9]+)S)?$", Pattern.CASE_INSENSITIVE)

fun MusicTrack.durationToSeconds(): Long {
    val m = LENGTH_PATTERN.matcher(duration)
    if (m.matches()) {
        val hr = m.group(1)
        val min = m.group(2)
        val sec = m.group(3)

        var duration: Long = 0
        if (hr != null)
            duration += java.lang.Long.parseLong(hr) * 60 * 60
        if (min != null)
            duration += java.lang.Long.parseLong(min) * 60
        if (sec != null)
            duration += java.lang.Long.parseLong(sec)
        return duration
    }
    return 0
}