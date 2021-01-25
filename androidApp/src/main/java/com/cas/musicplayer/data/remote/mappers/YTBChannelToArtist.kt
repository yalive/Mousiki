package com.cas.musicplayer.data.remote.mappers

import com.mousiki.shared.data.models.Artist
import com.mousiki.shared.data.models.YTBChannel
import com.mousiki.shared.data.models.urlOrEmpty
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class YTBChannelToArtist @Inject constructor() : Mapper<YTBChannel, Artist> {
    override suspend fun map(from: YTBChannel): Artist {
        val channelId = from.id.orEmpty()
        val title = from.snippet?.title.orEmpty()
        val urlImage = from.snippet?.thumbnails?.urlOrEmpty().orEmpty()
        return Artist(title, "", channelId, urlImage)
    }
}