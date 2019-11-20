package com.cas.musicplayer.data.mappers

import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.data.models.YTBVideo
import com.cas.musicplayer.data.models.urlOrEmpty
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class YTBVideoToTrack @Inject constructor() : Mapper<YTBVideo, MusicTrack> {
    override suspend fun map(from: YTBVideo): MusicTrack {
        val id = from.id ?: ""
        val title = from.snippet?.title ?: ""
        val duration = from.contentDetails?.duration ?: ""
        val track = MusicTrack(id, title, duration)
        from.snippet?.thumbnails?.urlOrEmpty()?.let { url ->
            track.fullImageUrl = url
        }
        return track
    }
}