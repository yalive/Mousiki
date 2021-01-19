package com.cas.musicplayer.data.local.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.mousiki.shared.domain.models.Playlist

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-24.
 ***************************************
 */

@Entity(
    tableName = "channel_playlist",
    indices = [Index(unique = true, value = arrayOf("playlist_id"))]
)
data class ChannelPlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "playlist_id") val playlistId: String,
    val channelId: String,
    val title: String,
    val urlImage: String,
    val itemCount: Int
)

fun ChannelPlaylistEntity.toPlaylist() = Playlist(
    id = playlistId,
    title = title,
    itemCount = itemCount,
    urlImage = urlImage
)