package com.cas.musicplayer.data.remote.mappers

import com.mousiki.shared.data.models.YTBPlaylist
import com.mousiki.shared.data.models.urlOrEmpty
import com.mousiki.shared.domain.models.Playlist
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class YTBPlaylistToPlaylist @Inject constructor() : Mapper<YTBPlaylist, Playlist> {
    override suspend fun map(from: YTBPlaylist): Playlist {
        val id = from.id.orEmpty()
        val title = from.snippet?.title.orEmpty()
        val itemCount = from.contentDetails?.itemCount ?: 0
        val urlImage = from.snippet?.thumbnails?.urlOrEmpty().orEmpty()

        val playlist = Playlist(id, title, itemCount, urlImage)

        return playlist
    }
}