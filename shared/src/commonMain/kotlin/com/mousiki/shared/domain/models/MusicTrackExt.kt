package com.mousiki.shared.domain.models

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