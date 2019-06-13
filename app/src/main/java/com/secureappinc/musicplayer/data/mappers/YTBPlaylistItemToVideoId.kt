package com.secureappinc.musicplayer.data.mappers

import com.secureappinc.musicplayer.data.enteties.VideoId
import com.secureappinc.musicplayer.data.models.YTBPlaylistItem
import javax.inject.Inject
import javax.inject.Singleton

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-12.
 ***************************************
 */
@Singleton
class YTBPlaylistItemToVideoId @Inject constructor() : Mapper<YTBPlaylistItem, VideoId> {
    override suspend fun map(from: YTBPlaylistItem): VideoId {
        return VideoId(from.contentDetails?.videoId ?: "")
    }
}