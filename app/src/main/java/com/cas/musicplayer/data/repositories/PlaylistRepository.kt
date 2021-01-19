package com.cas.musicplayer.data.repositories

import com.cas.common.result.Result
import com.cas.common.result.Result.Success
import com.cas.common.result.alsoWhenSuccess
import com.cas.musicplayer.data.datasource.channel.ChannelPlaylistsLocalDataSource
import com.cas.musicplayer.data.datasource.channel.ChannelPlaylistsRemoteDataSource
import com.cas.musicplayer.data.datasource.playlist.PlaylistSongsLocalDataSource
import com.cas.musicplayer.data.datasource.playlist.PlaylistSongsRemoteDataSource
import com.cas.musicplayer.data.local.database.dao.CustomPlaylistTrackDao
import com.cas.musicplayer.domain.model.MusicTrack
import com.mousiki.shared.domain.models.Playlist
import com.cas.musicplayer.utils.NetworkUtils
import javax.inject.Inject
import javax.inject.Singleton

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-12.
 ***************************************
 */

@Singleton
class PlaylistRepository @Inject constructor(
    private val channelPlaylistsLocalDataSource: ChannelPlaylistsLocalDataSource,
    private val channelPlaylistsRemoteDataSource: ChannelPlaylistsRemoteDataSource,
    private val playlistSongsLocalDataSource: PlaylistSongsLocalDataSource,
    private val playlistSongsRemoteDataSource: PlaylistSongsRemoteDataSource,
    private val customPlaylistTrackDao: CustomPlaylistTrackDao,
    private val networkUtils: NetworkUtils
) {

    suspend fun playlistVideos(playlistId: String): Result<List<MusicTrack>> {
        val localPlaylists = playlistSongsLocalDataSource.getPlaylistSongs(playlistId)
        if (localPlaylists.isNotEmpty()) {
            return Success(localPlaylists)
        }
        return playlistSongsRemoteDataSource.getPlaylistSongs(playlistId).alsoWhenSuccess {
            playlistSongsLocalDataSource.savePlaylistSongs(playlistId, it)
        }
    }

    suspend fun firstThreeVideo(playlistId: String): Result<List<MusicTrack>> {

        if (playlistSongsLocalDataSource.expired() && networkUtils.hasNetworkConnection()) {
            playlistSongsLocalDataSource.clear()
        }

        val localTracks = playlistSongsLocalDataSource.getPlaylistLightSongs(playlistId)
        if (localTracks.isNotEmpty()) {
            return Success(localTracks)
        }
        return playlistSongsRemoteDataSource.getPlaylistLightSongs(playlistId)
            .alsoWhenSuccess { tracks ->
                playlistSongsLocalDataSource.savePlaylistLightSongs(playlistId, tracks)
            }
    }

    suspend fun getPlaylists(channelId: String): Result<List<Playlist>> {
        val localPlaylists = channelPlaylistsLocalDataSource.getChannelPlaylists(channelId)
        if (localPlaylists.isNotEmpty()) {
            return Success(localPlaylists)
        }
        return channelPlaylistsRemoteDataSource.getChannelPlaylists(channelId).alsoWhenSuccess {
            channelPlaylistsLocalDataSource.saveChannelPlaylists(channelId, it)
        }
    }
}