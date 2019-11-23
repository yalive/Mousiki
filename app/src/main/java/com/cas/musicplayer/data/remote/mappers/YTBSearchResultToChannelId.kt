package com.cas.musicplayer.data.remote.mappers

import com.cas.musicplayer.domain.model.ChannelId
import com.cas.musicplayer.data.remote.models.YTBSearchResult
import javax.inject.Inject
import javax.inject.Singleton

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-12.
 ***************************************
 */
@Singleton
class YTBSearchResultToChannelId @Inject constructor() : Mapper<YTBSearchResult, ChannelId> {
    override suspend fun map(from: YTBSearchResult): ChannelId {
        return ChannelId(from.id?.channelId ?: "")
    }
}