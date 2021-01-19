package com.cas.musicplayer.domain.model

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