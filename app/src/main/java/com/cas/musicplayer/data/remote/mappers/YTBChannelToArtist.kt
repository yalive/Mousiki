package com.cas.musicplayer.data.remote.mappers

import com.cas.musicplayer.data.remote.models.Artist
import com.cas.musicplayer.data.remote.models.YTBChannel
import com.cas.musicplayer.data.remote.models.urlOrEmpty
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