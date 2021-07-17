package com.mousiki.shared.data.remote.mapper

import com.mousiki.shared.data.models.YTBVideo
import com.mousiki.shared.data.models.urlOrEmpty
import com.mousiki.shared.domain.models.YtbTrack

class YTBVideoToTrack : Mapper<YTBVideo, YtbTrack> {
    override suspend fun map(from: YTBVideo): YtbTrack {
        val id = from.id.orEmpty()
        val title = from.snippet?.title.orEmpty()
        val duration = from.contentDetails?.duration.orEmpty()
        val artistName = from.snippet?.channelTitle.orEmpty()
        val channelId = from.snippet?.channelId.orEmpty()
        val track = YtbTrack(id, title, duration, artistName, channelId)
        from.snippet?.thumbnails?.urlOrEmpty()?.let { url ->
            track.fullImageUrl = url
        }
        return track
    }
}