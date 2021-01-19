package com.cas.musicplayer.data.remote.mappers

import com.mousiki.shared.data.models.YTBSearchResult
import com.mousiki.shared.domain.models.PlaylistId
import javax.inject.Inject
import javax.inject.Singleton

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-12.
 ***************************************
 */
@Singleton
class YTBSearchResultToPlaylistId @Inject constructor() : Mapper<YTBSearchResult, PlaylistId> {
    override suspend fun map(from: YTBSearchResult): PlaylistId {
        return PlaylistId(from.id?.playlistId.orEmpty())
    }
}