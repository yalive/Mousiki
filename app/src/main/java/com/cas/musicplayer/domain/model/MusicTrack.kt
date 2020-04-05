package com.cas.musicplayer.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.regex.Pattern

/**
 **********************************
 * Created by Abdelhadi on 4/12/19.
 **********************************
 */

@Parcelize
data class MusicTrack(
    val youtubeId: String,
    val title: String,
    val duration: String
) : Parcelable {

    var fullImageUrl = ""

    val imgUrl: String
        get() {

            if (fullImageUrl.startsWith("http")) {
                return fullImageUrl
            }

            return "https://img.youtube.com/vi/$youtubeId/maxresdefault.jpg"
        }

    val shareVideoUrl: String
        get() {
            return "https://www.youtube.com/watch?v=$youtubeId"
        }

    val durationFormatted: String
        get() {

            if (!duration.startsWith("PT")) {
                return duration
            }

            val result =
                duration.replace("PT", "").replace("H", ":").replace("M", ":").replace("S", "")
            val arr = result.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            var timeString = ""
            //PT3M
            if (arr.size == 1) {
                val isMinute = duration.contains("M")

                if (isMinute) {
                    timeString = String.format(
                        "%02d:00",
                        Integer.parseInt(arr[0])
                    )
                } else {
                    timeString = String.format(
                        "%02ds",
                        Integer.parseInt(arr[0])
                    )
                }

            } else if (arr.size == 2) {
                timeString = String.format(
                    "%02d:%02d",
                    Integer.parseInt(arr[0]),
                    Integer.parseInt(arr[1])
                )
            } else if (arr.size == 3) {
                timeString = String.format(
                    "%d:%02d:%02d",
                    Integer.parseInt(arr[0]),
                    Integer.parseInt(arr[1]),
                    Integer.parseInt(arr[2])
                )
            }

            return timeString
        }
}

val LENGTH_PATTERN =
    Pattern.compile("^PT(?:([0-9]+)H)?(?:([0-9]+)M)?(?:([0-9]+)S)?$", Pattern.CASE_INSENSITIVE)

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