package com.cas.musicplayer.domain.model

import com.mousiki.shared.Parcelable
import com.mousiki.shared.Parcelize

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

    companion object
}

private val REGEX_DURATION =
    Regex("^PT(?:([0-9]+)H)?(?:([0-9]+)M)?(?:([0-9]+)S)?$", RegexOption.IGNORE_CASE)

fun MusicTrack.durationToSeconds(): Long {
    if (duration.matches(REGEX_DURATION)) {
        val groups = REGEX_DURATION.matchEntire(duration)?.groups
        val hr = groups?.get(1)?.value
        val min = groups?.get(2)?.value
        val sec = groups?.get(3)?.value

        var duration: Long = 0
        if (hr != null)
            duration += hr.toLong() * 60 * 60
        if (min != null)
            duration += min.toLong() * 60
        if (sec != null)
            duration += sec.toLong()
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
        /*FirebaseCrashlytics.getInstance()
            .recordException(Exception("Unable to parse custom duration part: $part", e))*/
        0
    }
}

val MusicTrack.Companion.EMPTY: MusicTrack
    get() = MusicTrack(
        youtubeId = "",
        title = "",
        duration = ""
    )