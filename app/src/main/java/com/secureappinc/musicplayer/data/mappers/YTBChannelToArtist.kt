package com.secureappinc.musicplayer.data.mappers

import com.secureappinc.musicplayer.data.models.Artist
import com.secureappinc.musicplayer.data.models.YTBChannel
import com.secureappinc.musicplayer.data.models.urlOrEmpty
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class YTBChannelToArtist @Inject constructor() : Mapper<YTBChannel, Artist> {
    override suspend fun map(from: YTBChannel): Artist {
        val channelId = from.id ?: ""
        val title = from.snippet?.title ?: ""
        val urlImage = from.snippet?.thumbnails?.urlOrEmpty() ?: ""
        return Artist(title, "", channelId, urlImage)
    }
}