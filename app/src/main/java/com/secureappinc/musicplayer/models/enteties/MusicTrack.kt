package com.secureappinc.musicplayer.models.enteties

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 **********************************
 * Created by Abdelhadi on 4/12/19.
 **********************************
 */
@Entity(tableName = "music_track")
data class MusicTrack(
    @PrimaryKey @ColumnInfo(name = "youtube_id") val youtubeId: String, val title: String,
    val duration: String
) {
    val imgUrl: String
        get() {
            return "https://img.youtube.com/vi/$youtubeId/maxresdefault.jpg"
        }
    val shareVideoUrl: String
        get() {
            return "https://www.youtube.com/watch?v=$youtubeId"
        }
}