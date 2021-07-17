package com.mousiki.shared.data.remote.mapper

import com.mousiki.shared.data.models.YTBPlaylistItem
import com.mousiki.shared.data.models.urlOrEmpty
import com.mousiki.shared.domain.models.YtbTrack

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-12.
 ***************************************
 */
class YTBPlaylistItemToTrack : Mapper<YTBPlaylistItem, YtbTrack> {
    override suspend fun map(from: YTBPlaylistItem): YtbTrack {
        val id = from.contentDetails?.videoId.orEmpty()
        val title = from.snippet?.title.orEmpty()
        val track = YtbTrack(
            youtubeId = id,
            title = title,
            duration = "",
            artistName = from.snippet?.channelTitle.orEmpty(),
            artistId = ""
        )
        from.snippet?.thumbnails?.urlOrEmpty()?.let { url ->
            track.fullImageUrl = url
        }
        return track
    }
}