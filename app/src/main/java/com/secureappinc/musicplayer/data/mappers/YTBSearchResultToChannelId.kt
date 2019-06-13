package com.secureappinc.musicplayer.data.mappers

import com.secureappinc.musicplayer.data.enteties.ChannelId
import com.secureappinc.musicplayer.data.models.YTBSearchResult
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