package com.mousiki.shared.data.remote.mapper

import com.mousiki.shared.data.models.YTBSearchResult
import com.mousiki.shared.domain.models.VideoId

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-12.
 ***************************************
 */
class YTBSearchResultToVideoId : Mapper<YTBSearchResult, VideoId> {
    override suspend fun map(from: YTBSearchResult): VideoId {
        return VideoId(from.id?.videoId.orEmpty())
    }
}