package com.mousiki.shared.domain.models

import kotlin.math.roundToLong

val Track.imgUrlDef0: String
    get() = when (this) {
        is LocalSong -> albumImage
        is YtbTrack -> this.imgUrlDef0
        is AiTrack -> this.image
    }


val YtbTrack.imgUrlDef0: String
    get() = "https://img.youtube.com/vi/$youtubeId/0.jpg"

val YtbTrack.imgUrlDefault: String
    get() = "https://img.youtube.com/vi/$youtubeId/default.jpg"

val YtbTrack.imgUrl: String
    get() {
        if (fullImageUrl.startsWith("http")) {
            return fullImageUrl
        }
        return "https://img.youtube.com/vi/$youtubeId/maxresdefault.jpg"
    }

val Track.imgUrlDefault: String
    get() = when (this) {
        is LocalSong -> albumImage
        is YtbTrack -> this.imgUrlDefault
        is AiTrack -> this.image
    }

val Track.imgUrl: String
    get() = when (this) {
        is LocalSong -> albumImage
        is YtbTrack -> this.imgUrl
        is AiTrack -> this.image
    }

val Track.durationFormatted: String
    get() = when (this) {
        is LocalSong -> {
            val milliseconds = duration.toLong()
            val seconds = (milliseconds / 1000).toInt() % 60
            val minutes = (milliseconds / (1000 * 60) % 60).toInt()
            val hours = (milliseconds / (1000 * 60 * 60) % 24).toInt()
            if (hours == 0) {
                "${minutes.twoDigits()}:${seconds.twoDigits()}"
            } else {
                "$hours:${minutes.twoDigits()}:${seconds.twoDigits()}"
            }
        }
        is YtbTrack -> this.durationFormatted
        is AiTrack -> {
            val milliseconds = (duration.toDouble()*1000).roundToLong()
            val seconds = (milliseconds / 1000).toInt() % 60
            val minutes = (milliseconds / (1000 * 60) % 60).toInt()
            val hours = (milliseconds / (1000 * 60 * 60) % 24).toInt()
            if (hours == 0) {
                "${minutes.twoDigits()}:${seconds.twoDigits()}"
            } else {
                "$hours:${minutes.twoDigits()}:${seconds.twoDigits()}"
            }
        }
    }

val YtbTrack.durationFormatted: String
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
            // min
            val isMinute = duration.contains("M")
            if (isMinute) {
                timeString = "${arr[0].toInt().twoDigits()}:00"
            } else {
                timeString = "${arr[0].toInt().twoDigits()}s"
            }

        } else if (arr.size == 2) {
            // min:ss
            val args = arr[0].toInt()
            val args1 = arr[1].toInt()
            timeString = "${args.twoDigits()}:${args1.twoDigits()}"
        } else if (arr.size == 3) {
            // h:min:ss
            timeString = "${arr[0].toInt()}:${arr[1].toInt().twoDigits()}:${arr[2].toInt()}"
        }

        return timeString
    }

fun Int.twoDigits(): String {
    if (this < 10) return "0$this"
    return "$this"
}