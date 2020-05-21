package com.cas.musicplayer.domain.model

import android.os.Parcelable
import com.crashlytics.android.Crashlytics
import kotlinx.android.parcel.IgnoredOnParcel
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

    val imgUrlDef0: String
        get() = "https://img.youtube.com/vi/$youtubeId/0.jpg"

    val imgUrlDefault: String
        get() = "https://img.youtube.com/vi/$youtubeId/default.jpg"

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

    @IgnoredOnParcel
    @delegate:Transient
    val totalSeconds: Long by lazy { durationToSeconds() }

    companion object
}

val LENGTH_PATTERN =
    Pattern.compile("^PT(?:([0-9]+)H)?(?:([0-9]+)M)?(?:([0-9]+)S)?$", Pattern.CASE_INSENSITIVE)

private fun MusicTrack.durationToSeconds(): Long {
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

fun MusicTrack.Companion.toYoutubeDuration(notificationDuration: String): String {
    val parts = notificationDuration.split(":")
    if (parts.size < 2) return ""
    if (parts.size == 2) {
        val minute = parseDurationPart(parts[0])
        val second = parseDurationPart(parts[1])
        return "PT${minute}M${second}S"
    } else {
        val hour = parseDurationPart(parts[0])
        val minute = parseDurationPart(parts[1])
        val second = parseDurationPart(parts[2])
        return "PT${hour}H${minute}M${second}S"
    }
}

private fun parseDurationPart(part: String): Int {
    return try {
        part.toInt()
    } catch (e: Exception) {
        Crashlytics.logException(Exception("Unable to parse custom duration part: $part", e))
        0
    }
}

val MusicTrack.Companion.EMPTY: MusicTrack
    get() = MusicTrack(
        youtubeId = "",
        title = "",
        duration = ""
    )

