package com.mousiki.shared.data.remote.mapper

import com.mousiki.shared.data.models.YTBSearchResult
import com.mousiki.shared.domain.models.PlaylistId

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-12.
 ***************************************
 */
class YTBSearchResultToPlaylistId : Mapper<YTBSearchResult, PlaylistId> {
    override suspend fun map(from: YTBSearchResult): PlaylistId {
        return PlaylistId(from.id?.playlistId.orEmpty())
    }
}