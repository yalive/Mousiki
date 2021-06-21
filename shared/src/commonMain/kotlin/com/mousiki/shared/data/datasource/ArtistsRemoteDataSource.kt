package com.mousiki.shared.data.datasource

import com.mousiki.shared.data.models.Artist
import com.mousiki.shared.data.remote.api.MousikiApi
import com.mousiki.shared.data.remote.mapper.YTBChannelToArtist
import com.mousiki.shared.data.remote.mapper.toListMapper
import com.mousiki.shared.data.remote.runner.NetworkRunner
import com.mousiki.shared.domain.result.Result

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-24.
 ***************************************
 */

class ArtistsRemoteDataSource constructor(
    private var mousikiApi: MousikiApi,
    private val networkRunner: NetworkRunner,
    private val artistMapper: YTBChannelToArtist
) {

    suspend fun getArtists(ids: List<String>): Result<List<Artist>> {
        return networkRunner.executeNetworkCall(artistMapper.toListMapper()) {
            val idsList = ids.joinToString { it }
            mousikiApi.channels(idsList).items ?: emptyList()
        }
    }
}