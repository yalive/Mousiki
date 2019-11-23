package com.cas.musicplayer.data.datasource

import com.cas.common.result.Result
import com.cas.musicplayer.data.preferences.PreferencesHelper
import com.cas.musicplayer.data.remote.mappers.YTBVideoToTrack
import com.cas.musicplayer.data.remote.mappers.toListMapper
import com.cas.musicplayer.data.remote.retrofit.RetrofitRunner
import com.cas.musicplayer.data.remote.retrofit.YoutubeService
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.utils.getCurrentLocale
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-20.
 ***************************************
 */
class RemoteSongsDataSource @Inject constructor(
    private var youtubeService: YoutubeService,
    private val retrofitRunner: RetrofitRunner,
    private val trackMapper: YTBVideoToTrack,
    private val preferences: PreferencesHelper
) {

    suspend fun getTrendingSongs(max: Int): Result<List<MusicTrack>> {
        return retrofitRunner.executeNetworkCall(trackMapper.toListMapper()) {
            val resource = youtubeService.trending(max, getCurrentLocale(), preferences.mostPopularNextPageToken())
            val nextPageToken = resource.nextPageToken ?: ""
            preferences.setMostPopularNextPageToken(nextPageToken)
            resource.items ?: emptyList()
        }
    }
}