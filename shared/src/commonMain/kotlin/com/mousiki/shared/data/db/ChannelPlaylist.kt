package com.mousiki.shared.data.db

import com.mousiki.shared.db.Channel_playlist
import com.mousiki.shared.domain.models.Playlist

typealias ChannelPlaylistEntity = Channel_playlist

fun Channel_playlist.toPlaylist() = Playlist(
    id = playlist_id,
    title = title,
    itemCount = itemCount.toInt(),
    urlImage = urlImage
)