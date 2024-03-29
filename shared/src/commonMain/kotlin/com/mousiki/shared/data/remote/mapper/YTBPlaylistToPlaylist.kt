package com.mousiki.shared.data.remote.mapper

import com.mousiki.shared.data.models.YTBPlaylist
import com.mousiki.shared.data.models.urlOrEmpty
import com.mousiki.shared.domain.models.Playlist

class YTBPlaylistToPlaylist : Mapper<YTBPlaylist, Playlist> {
    override suspend fun map(from: YTBPlaylist): Playlist {
        val id = from.id.orEmpty()
        val title = from.snippet?.title.orEmpty()
        val itemCount = from.contentDetails?.itemCount ?: 0
        val urlImage = from.snippet?.thumbnails?.urlOrEmpty().orEmpty()

        return Playlist(
            id = id,
            title = title,
            itemCount = itemCount,
            urlImage = urlImage,
            type = Playlist.TYPE_YTB,
            createdBy = Playlist.CREATED_BY_APP
        )
    }
}