package com.secureappinc.musicplayer.data.mappers

import com.secureappinc.musicplayer.data.enteties.Playlist
import com.secureappinc.musicplayer.data.models.YTBPlaylist
import com.secureappinc.musicplayer.data.models.urlOrEmpty
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class YTBPlaylistToPlaylist @Inject constructor() : Mapper<YTBPlaylist, Playlist> {
    override suspend fun map(from: YTBPlaylist): Playlist {
        val id = from.id ?: ""
        val title = from.snippet?.title ?: ""
        val itemCount = from.contentDetails?.itemCount ?: 0
        val urlImage = from.snippet?.thumbnails?.urlOrEmpty() ?: ""

        val playlist = Playlist(id, title, itemCount, urlImage)

        return playlist
    }
}