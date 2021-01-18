package com.cas.musicplayer.data.remote.mappers

import com.cas.musicplayer.data.remote.models.YTBPlaylistItem
import com.cas.musicplayer.domain.model.VideoId
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
        return VideoId(from.contentDetails?.videoId.orEmpty())
    }
}