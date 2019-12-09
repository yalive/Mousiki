package com.cas.musicplayer.data.local.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.cas.musicplayer.domain.model.Playlist

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-09.
 ***************************************
 */

@Entity(tableName = "playlists_search_result", indices = [Index(unique = true, value = arrayOf("playlist_id"))])
data class SearchPlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "playlist_id") val playlistId: String,
    val title: String,
    val urlImage: String,
    val itemCount: Int,
    val query: String
)

fun SearchPlaylistEntity.toPlaylist() = Playlist(
    id = playlistId,
    title = title,
    itemCount = itemCount,
    urlImage = urlImage
)