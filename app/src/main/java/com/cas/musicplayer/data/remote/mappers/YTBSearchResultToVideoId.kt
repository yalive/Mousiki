package com.cas.musicplayer.data.remote.mappers

import com.cas.musicplayer.data.remote.models.YTBSearchResult
import com.cas.musicplayer.domain.model.VideoId
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