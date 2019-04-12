package com.secureappinc.musicplayer.models.enteties

/**
 **********************************
 * Created by Abdelhadi on 4/12/19.
 **********************************
 */
data class MusicTrack(val youtubeId: String, val title: String, val duration: String) {
    val imgUrl: String
        get() {
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

            val result = duration.replace("PT", "").replace("H", ":").replace("M", ":").replace("S", "")
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