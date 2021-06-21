package com.mousiki.shared.data.remote.mapper

import com.mousiki.shared.data.models.YTBSearchResult
import com.mousiki.shared.domain.models.ChannelId

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-12.
 ***************************************
 */
class YTBSearchResultToChannelId : Mapper<YTBSearchResult, ChannelId> {
    override suspend fun map(from: YTBSearchResult): ChannelId {
        return ChannelId(from.id?.channelId.orEmpty())
    }
}