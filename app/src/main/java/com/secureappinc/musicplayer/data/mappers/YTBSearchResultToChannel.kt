package com.secureappinc.musicplayer.data.mappers

import com.secureappinc.musicplayer.data.enteties.Channel
import com.secureappinc.musicplayer.data.models.YTBSearchResult
import com.secureappinc.musicplayer.data.models.urlOrEmpty
import javax.inject.Inject
import javax.inject.Singleton

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-12.
 ***************************************
 */
@Singleton
class YTBSearchResultToChannel @Inject constructor() : Mapper<YTBSearchResult, Channel> {
    override suspend fun map(from: YTBSearchResult): Channel {
        return Channel(
            from.id?.channelId ?: "",
            from.snippet?.title ?: "",
            "",
            from.snippet?.thumbnails?.urlOrEmpty() ?: ""
        )
    }
}