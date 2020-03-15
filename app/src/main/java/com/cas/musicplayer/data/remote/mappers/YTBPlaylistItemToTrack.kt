package com.cas.musicplayer.data.remote.mappers

import com.cas.musicplayer.data.remote.models.YTBPlaylistItem
import com.cas.musicplayer.data.remote.models.urlOrEmpty
import com.cas.musicplayer.domain.model.MusicTrack
import javax.inject.Inject
import javax.inject.Singleton

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-12.
 ***************************************
 */
@Singleton
class YTBPlaylistItemToTrack @Inject constructor() : Mapper<YTBPlaylistItem, MusicTrack> {
    override suspend fun map(from: YTBPlaylistItem): MusicTrack {
        val id = from.contentDetails?.videoId ?: ""
        val title = from.snippet?.title ?: ""
        val track = MusicTrack(id, title, "")
        from.snippet?.thumbnails?.urlOrEmpty()?.let { url ->
            track.fullImageUrl = url
        }
        return track
    }
}