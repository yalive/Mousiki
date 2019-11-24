package com.cas.musicplayer.data.datasource

import com.cas.common.result.Result
import com.cas.musicplayer.data.preferences.PreferencesHelper
import com.cas.musicplayer.data.remote.mappers.YTBChannelToArtist
import com.cas.musicplayer.data.remote.mappers.YTBSearchResultToVideoId
import com.cas.musicplayer.data.remote.mappers.YTBVideoToTrack
import com.cas.musicplayer.data.remote.mappers.toListMapper
import com.cas.musicplayer.data.remote.models.Artist
import com.cas.musicplayer.data.remote.retrofit.RetrofitRunner
import com.cas.musicplayer.data.remote.retrofit.YoutubeService
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-24.
 ***************************************
 */

class ArtistsRemoteDataSource @Inject constructor(
    private var youtubeService: YoutubeService,
    private val retrofitRunner: RetrofitRunner,
    private val artistMapper: YTBChannelToArtist
) {

    suspend fun getArtists(ids: List<String>): Result<List<Artist>> {
        return retrofitRunner.executeNetworkCall(artistMapper.toListMapper()) {
            val idsList = ids.joinToString { it }
            youtubeService.channels(idsList).items ?: emptyList()
        }
    }
}