package com.mousiki.shared.data.remote.mapper

import com.mousiki.shared.data.models.Artist
import com.mousiki.shared.data.models.YTBChannel
import com.mousiki.shared.data.models.urlOrEmpty

class YTBChannelToArtist : Mapper<YTBChannel, Artist> {
    override suspend fun map(from: YTBChannel): Artist {
        val channelId = from.id.orEmpty()
        val title = from.snippet?.title.orEmpty()
        val urlImage = from.snippet?.thumbnails?.urlOrEmpty().orEmpty()
        return Artist(title, "", channelId, urlImage)
    }
}