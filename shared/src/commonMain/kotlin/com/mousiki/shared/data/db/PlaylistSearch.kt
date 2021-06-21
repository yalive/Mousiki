package com.mousiki.shared.data.db

import com.mousiki.shared.db.Playlists_search_result
import com.mousiki.shared.domain.models.Playlist

typealias PlaylistSearchEntity = Playlists_search_result

fun Playlists_search_result.toPlaylist() = Playlist(
    id = playlist_id,
    title = title,
    itemCount = itemCount.toInt(),
    urlImage = urlImage
)