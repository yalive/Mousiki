package com.cas.musicplayer.data.mappers

import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.data.models.YTBPlaylistItem
import com.cas.musicplayer.data.models.urlOrEmpty
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