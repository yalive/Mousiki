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
}