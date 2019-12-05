package com.cas.musicplayer.data.local.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cas.musicplayer.domain.model.MusicTrack

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-24.
 ***************************************
 */
@Entity(tableName = "playlist_first_three_tracks")
data class LightSongEntity(
    @PrimaryKey @ColumnInfo(name = "youtube_id") val youtubeId: String,
    val playlistId: String,
    val title: String
)

fun LightSongEntity.toMusicTrack() = MusicTrack(
    youtubeId = youtubeId,
    title = title,
    duration = ""
)