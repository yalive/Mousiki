package com.mousiki.shared.data.remote.mapper

import com.mousiki.shared.data.models.YTBPlaylistItem
import com.mousiki.shared.domain.models.VideoId

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-12.
 ***************************************
 */
class YTBPlaylistItemToVideoId : Mapper<YTBPlaylistItem, VideoId> {
    override suspend fun map(from: YTBPlaylistItem): VideoId {
        return VideoId(from.contentDetails?.videoId.orEmpty())
    }
}