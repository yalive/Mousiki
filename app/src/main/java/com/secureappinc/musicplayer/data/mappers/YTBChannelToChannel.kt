package com.secureappinc.musicplayer.data.mappers

import com.secureappinc.musicplayer.data.enteties.Channel
import com.secureappinc.musicplayer.data.models.YTBChannel
import com.secureappinc.musicplayer.data.models.urlOrEmpty
import javax.inject.Inject
import javax.inject.Singleton

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-12.
 ***************************************
 */
@Singleton
class YTBChannelToChannel @Inject constructor() : Mapper<YTBChannel, Channel> {
    override suspend fun map(from: YTBChannel): Channel {
        return Channel(
            from.id ?: "",
            from.snippet?.title ?: "",
            "",
            from.snippet?.thumbnails?.urlOrEmpty() ?: ""
        )
    }
}