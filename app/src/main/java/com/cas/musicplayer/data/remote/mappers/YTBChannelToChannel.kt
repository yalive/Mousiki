package com.cas.musicplayer.data.remote.mappers

import com.cas.musicplayer.data.remote.models.YTBChannel
import com.cas.musicplayer.data.remote.models.urlOrEmpty
import com.cas.musicplayer.domain.model.Channel
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
            from.id.orEmpty(),
            from.snippet?.title.orEmpty(),
            "",
            from.snippet?.thumbnails?.urlOrEmpty().orEmpty()
        )
    }
}