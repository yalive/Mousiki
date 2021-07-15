package com.mousiki.shared.domain.models

import com.mousiki.shared.Parcelable
import com.mousiki.shared.Parcelize

/**
 **********************************
 * Created by Abdelhadi on 4/12/19.
 **********************************
 */

sealed class Track : Parcelable {
    abstract val id: String /* id must be unique for any type of tracks: YTB, Local, remote... */
    abstract val title: String
    abstract val artistName: String
    abstract val duration: String

    val type: String
        get() = when (this) {
            is LocalSong -> TYPE_LOCAL_AUDIO
            is YtbTrack -> TYPE_YTB
        }

    companion object {
        const val TYPE_YTB = "YTB"
        const val TYPE_LOCAL_AUDIO = "LOCAL_AUD"
    }
}

@Parcelize
data class LocalSong(val song: Song) : Track() {
    override val id: String = "${song.id}"
    override val title: String = song.title
    override val duration: String = "${song.duration}"
    override val artistName: String = song.artistName
    val data: String get() = song.data
    val albumId: Long get() = song.albumId
}

@Parcelize
data class YtbTrack(
    val youtubeId: String,
    override val title: String,
    override val duration: String,
    override val artistName: String = "",
) : Track() {

    override val id: String get() = youtubeId

    var fullImageUrl = ""

    companion object
}

private val REGEX_YTB_DURATION =
    Regex("^PT(?:([0-9]+)H)?(?:([0-9]+)M)?(?:([0-9]+)S)?$", RegexOption.IGNORE_CASE)

fun Track.durationToSeconds(): Long {
    return when (this) {
        is LocalSong -> {
            try {
                val milliseconds = duration.toLong()
                milliseconds / 1000
            } catch (e: Exception) {
                0
            }
        }
        is YtbTrack -> this.durationToSeconds()
    }
}

fun YtbTrack.durationToSeconds(): Long {
    if (duration.matches(REGEX_YTB_DURATION)) {
        val groups = REGEX_YTB_DURATION.matchEntire(duration)?.groups
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

fun YtbTrack.Companion.toYoutubeDuration(notificationDuration: String): String {
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

val YtbTrack.Companion.EMPTY: YtbTrack
    get() = YtbTrack(
        youtubeId = "",
        title = "",
        duration = "",
        artistName = ""
    )