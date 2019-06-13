package com.secureappinc.musicplayer.data.mappers

import com.secureappinc.musicplayer.data.enteties.PlaylistId
import com.secureappinc.musicplayer.data.models.YTBSearchResult
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
        return PlaylistId(from.id?.playlistId ?: "")
    }
}