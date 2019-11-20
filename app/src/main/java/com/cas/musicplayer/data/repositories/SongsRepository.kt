package com.cas.musicplayer.data.repositories

import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.data.mappers.YTBVideoToTrack
import com.cas.musicplayer.data.mappers.toListMapper
import com.cas.common.result.Result
import com.cas.musicplayer.data.net.RetrofitRunner
import com.cas.musicplayer.data.net.YoutubeService
import com.cas.musicplayer.utils.getCurrentLocale
import javax.inject.Inject
import javax.inject.Singleton

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-09.
 ***************************************
 */
@Singleton
class SongsRepository @Inject constructor(
    private var youtubeService: YoutubeService,
    private val retrofitRunner: RetrofitRunner,
    private val trackMapper: YTBVideoToTrack
) {

    suspend fun getTrendingSongs(max: Int): Result<List<MusicTrack>> {
        return retrofitRunner.executeNetworkCall(trackMapper.toListMapper()) {
            youtubeService.trending(max, getCurrentLocale()).items!!
        }
    }
}