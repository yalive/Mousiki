package com.cas.musicplayer.data.datasource.playlist

import com.cas.common.result.Result
import com.cas.musicplayer.data.remote.mappers.YTBPlaylistItemToTrack
import com.cas.musicplayer.data.remote.mappers.toListMapper
import com.cas.musicplayer.data.remote.retrofit.RetrofitRunner
import com.cas.musicplayer.data.remote.retrofit.YoutubeService
import com.cas.musicplayer.domain.model.MusicTrack
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-24.
 ***************************************
 */
class PlaylistSongTitleRemoteDataSource @Inject constructor(
    private var youtubeService: YoutubeService,
    private val retrofitRunner: RetrofitRunner,
    private val playlistTrackMapper: YTBPlaylistItemToTrack
) {

    suspend fun getPlaylistSongs(playlistId: String): Result<List<MusicTrack>> {
        return retrofitRunner.executeNetworkCall(playlistTrackMapper.toListMapper()) {
            youtubeService.playlistVideosTitle(playlistId, 3).items ?: emptyList()
        }
    }
}