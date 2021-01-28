package com.mousiki.shared.data.remote.mapper

import com.mousiki.shared.data.models.YTBPlaylistItem
import com.mousiki.shared.data.models.urlOrEmpty
import com.mousiki.shared.domain.models.MusicTrack

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-12.
 ***************************************
 */
class YTBPlaylistItemToTrack : Mapper<YTBPlaylistItem, MusicTrack> {
    override suspend fun map(from: YTBPlaylistItem): MusicTrack {
        val id = from.contentDetails?.videoId.orEmpty()
        val title = from.snippet?.title.orEmpty()
        val track = MusicTrack(id, title, "")
        from.snippet?.thumbnails?.urlOrEmpty()?.let { url ->
            track.fullImageUrl = url
        }
        return track
    }
}