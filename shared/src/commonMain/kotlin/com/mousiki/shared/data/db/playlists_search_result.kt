package com.mousiki.shared.data.db

import com.mousiki.shared.db.Playlists_search_result
import com.mousiki.shared.domain.models.Playlist

fun Playlists_search_result.toPlaylist() = Playlist(
    id = playlist_id,
    title = title,
    itemCount = itemCount.toInt(),
    urlImage = urlImage
)