package com.cas.musicplayer.data.local.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/4/20.
 ***************************************
 */
@Entity(
    tableName = "custom_playlist_track",
    indices = [Index(unique = true, value = arrayOf("youtube_id", "playlist_name"))]
)
data class CustomPlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "youtube_id") val youtubeId: String,
    val title: String,
    val duration: String,
    @ColumnInfo(name = "playlist_name") val playlistName: String
)

val CustomPlaylistEntity.imgUrl: String
    get() {
        return "https://img.youtube.com/vi/$youtubeId/maxresdefault.jpg"
    }