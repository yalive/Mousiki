package com.cas.musicplayer.data.remote.mappers

import com.mousiki.shared.data.models.YTBVideo
import com.mousiki.shared.data.models.urlOrEmpty
import com.cas.musicplayer.domain.model.MusicTrack
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class YTBVideoToTrack @Inject constructor() : Mapper<YTBVideo, MusicTrack> {
    override suspend fun map(from: YTBVideo): MusicTrack {
        val id = from.id.orEmpty()
        val title = from.snippet?.title.orEmpty()
        val duration = from.contentDetails?.duration.orEmpty()
        val track = MusicTrack(id, title, duration)
        from.snippet?.thumbnails?.urlOrEmpty()?.let { url ->
            track.fullImageUrl = url
        }
        return track
    }
}