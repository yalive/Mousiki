package com.cas.musicplayer.data.remote.mappers

import com.mousiki.shared.data.models.YTBChannel
import com.mousiki.shared.data.models.urlOrEmpty
import com.mousiki.shared.domain.models.Channel
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