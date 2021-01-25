package com.cas.musicplayer.data.local.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.mousiki.shared.domain.models.MusicTrack

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-24.
 ***************************************
 */
@Entity(
    tableName = "playlist_tracks",
    indices = [Index(unique = true, value = arrayOf("youtube_id"))]
)
data class PlaylistSongEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "youtube_id") val youtubeId: String,
    val playlistId: String,
    val title: String,
    val duration: String
)

fun PlaylistSongEntity.toMusicTrack() = MusicTrack(
    youtubeId = youtubeId,
    title = title,
    duration = duration
)