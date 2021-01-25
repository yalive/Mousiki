package com.mousiki.shared.domain.models

import com.mousiki.shared.utils.format

val MusicTrack.imgUrlDef0: String
    get() = "https://img.youtube.com/vi/$youtubeId/0.jpg"

val MusicTrack.imgUrlDefault: String
    get() = "https://img.youtube.com/vi/$youtubeId/default.jpg"

val MusicTrack.imgUrl: String
    get() {
        if (fullImageUrl.startsWith("http")) {
            return fullImageUrl
        }
        return "https://img.youtube.com/vi/$youtubeId/maxresdefault.jpg"
    }

val MusicTrack.durationFormatted: String
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
                timeString = "%02d:00".format(arr[0].toInt())
            } else {
                timeString = "%02ds".format(arr[0].toInt())
            }

        } else if (arr.size == 2) {
            timeString = "%02d:%02d".format(arr[0].toInt(), arr[1].toInt())
        } else if (arr.size == 3) {
            timeString = "%d:%02d:%02d".format(arr[0].toInt(), arr[1].toInt(), arr[2].toInt())
        }

        return timeString
    }