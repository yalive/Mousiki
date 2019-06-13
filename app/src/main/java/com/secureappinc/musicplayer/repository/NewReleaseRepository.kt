package com.secureappinc.musicplayer.repository

import com.secureappinc.musicplayer.data.enteties.MusicTrack
import com.secureappinc.musicplayer.data.mappers.YTBVideoToTrack
import com.secureappinc.musicplayer.data.mappers.toListMapper
import com.secureappinc.musicplayer.base.common.Resource
import com.secureappinc.musicplayer.net.RetrofitRunner
import com.secureappinc.musicplayer.net.YoutubeService
import com.secureappinc.musicplayer.net.toResource
import com.secureappinc.musicplayer.utils.getCurrentLocale
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