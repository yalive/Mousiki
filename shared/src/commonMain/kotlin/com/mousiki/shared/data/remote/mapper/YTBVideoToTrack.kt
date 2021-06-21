package com.mousiki.shared.data.remote.mapper

import com.mousiki.shared.data.models.YTBVideo
import com.mousiki.shared.data.models.urlOrEmpty
import com.mousiki.shared.domain.models.MusicTrack

class YTBVideoToTrack : Mapper<YTBVideo, MusicTrack> {
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