package com.cas.musicplayer.data.local.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.cas.musicplayer.domain.model.MusicTrack

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-24.
 ***************************************
 */

@Entity(
    tableName = "channel_tracks",
    indices = [Index(unique = true, value = arrayOf("youtube_id"))]
)
data class ChannelSongEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "youtube_id") val youtubeId: String,
    val channelId: String,
    val title: String,
    val duration: String
)

fun ChannelSongEntity.toMusicTrack() = MusicTrack(
    youtubeId = youtubeId,
    title = title,
    duration = duration
)