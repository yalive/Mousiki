package com.mousiki.shared.data.db

import com.mousiki.shared.db.Db_playlist
import com.mousiki.shared.domain.models.Playlist

fun Db_playlist.asPlaylist(itemCount: Int = 0, urlFirstTrack: String = ""): Playlist {
    return Playlist(
        id = "$id",
        title = name,
        itemCount = itemCount,
        urlImage = urlFirstTrack,
        type = type
    )
}