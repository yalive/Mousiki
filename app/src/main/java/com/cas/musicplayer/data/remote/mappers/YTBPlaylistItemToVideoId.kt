package com.cas.musicplayer.data.remote.mappers

import com.mousiki.shared.data.models.YTBPlaylistItem
import com.mousiki.shared.domain.models.VideoId
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