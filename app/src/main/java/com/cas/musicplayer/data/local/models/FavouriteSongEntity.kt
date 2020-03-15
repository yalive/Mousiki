package com.cas.musicplayer.data.local.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.cas.musicplayer.domain.model.MusicTrack


@Entity(
    tableName = "favourite_tracks",
    indices = [Index(unique = true, value = arrayOf("youtube_id"))]
)
data class FavouriteSongEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "youtube_id") val youtubeId: String,
    val title: String,
    val duration: String
)

fun FavouriteSongEntity.toMusicTrack() = MusicTrack(
    youtubeId = youtubeId,
    title = title,
    duration = duration
)