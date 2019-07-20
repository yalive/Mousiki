package com.cas.musicplayer.repository

import com.cas.musicplayer.data.enteties.MusicTrack
import com.cas.musicplayer.data.mappers.YTBVideoToTrack
import com.cas.musicplayer.data.mappers.toListMapper
import com.cas.musicplayer.base.common.Resource
import com.cas.musicplayer.net.RetrofitRunner
import com.cas.musicplayer.net.YoutubeService
import com.cas.musicplayer.net.toResource
import com.cas.musicplayer.utils.getCurrentLocale
import javax.inject.Inject
import javax.inject.Singleton

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-10.
 ***************************************
 */

@Singleton
class NewReleaseRepository @Inject constructor(
    private var youtubeService: YoutubeService,
    private val retrofitRunner: RetrofitRunner,
    private val trackMapper: YTBVideoToTrack
) {

    suspend fun loadNewReleases(): Resource<List<MusicTrack>> =
        retrofitRunner.executeNetworkCall(trackMapper.toListMapper()) {
            youtubeService.trending(50, getCurrentLocale()).items!!
        }.toResource()
}