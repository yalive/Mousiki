package com.cas.musicplayer.data.mappers

import com.cas.musicplayer.data.enteties.VideoId
import com.cas.musicplayer.data.models.YTBSearchResult
import javax.inject.Inject
import javax.inject.Singleton

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-12.
 ***************************************
 */
@Singleton
class YTBSearchResultToVideoId @Inject constructor() : Mapper<YTBSearchResult, VideoId> {
    override suspend fun map(from: YTBSearchResult): VideoId {
        return VideoId(from.id?.videoId ?: "")
    }
}