package com.mousiki.shared.data.remote.mapper

import com.mousiki.shared.data.models.YTBChannel
import com.mousiki.shared.data.models.urlOrEmpty
import com.mousiki.shared.domain.models.Channel

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-12.
 ***************************************
 */
class YTBChannelToChannel : Mapper<YTBChannel, Channel> {
    override suspend fun map(from: YTBChannel): Channel {
        return Channel(
            from.id.orEmpty(),
            from.snippet?.title.orEmpty(),
            "",
            from.snippet?.thumbnails?.urlOrEmpty().orEmpty()
        )
    }
}