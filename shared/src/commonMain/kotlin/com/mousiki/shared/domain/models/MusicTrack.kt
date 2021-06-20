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
}

@Parcelize
data class LocalSong(
    override val id: String,
    override val title: String,
    override val duration: String,
    override val artistName: String,
    val path: String
) : Track()

@Parcelize
data class MusicTrack(
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
        is MusicTrack -> this.durationToSeconds()
    }
}

fun MusicTrack.durationToSeconds(): Long {
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
        duration = "",
        artistName = ""
    )