package com.cas.musicplayer.data.remote.mappers

import com.cas.musicplayer.data.remote.models.YTBPlaylist
import com.cas.musicplayer.data.remote.models.urlOrEmpty
import com.cas.musicplayer.domain.model.Playlist
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