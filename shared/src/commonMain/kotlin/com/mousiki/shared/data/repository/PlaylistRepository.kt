package com.mousiki.shared.data.repository

import com.mousiki.shared.data.datasource.channel.ChannelPlaylistsLocalDataSource
import com.mousiki.shared.data.datasource.channel.ChannelPlaylistsRemoteDataSource
import com.mousiki.shared.data.datasource.playlist.PlaylistSongsLocalDataSource
import com.mousiki.shared.data.datasource.playlist.PlaylistSongsRemoteDataSource
import com.mousiki.shared.domain.models.YtbTrack
import com.mousiki.shared.domain.models.Playlist
import com.mousiki.shared.domain.result.Result
import com.mousiki.shared.domain.result.Result.Success
import com.mousiki.shared.domain.result.alsoWhenSuccess

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-12.
 ***************************************
 */

class PlaylistRepository(
    private val channelPlaylistsLocalDataSource: ChannelPlaylistsLocalDataSource,
    private val channelPlaylistsRemoteDataSource: ChannelPlaylistsRemoteDataSource,
    private val playlistSongsLocalDataSource: PlaylistSongsLocalDataSource,
    private val playlistSongsRemoteDataSource: PlaylistSongsRemoteDataSource
) {

    suspend fun playlistVideos(playlistId: String): Result<List<YtbTrack>> {
        val localPlaylists = playlistSongsLocalDataSource.getPlaylistSongs(playlistId)
        if (localPlaylists.isNotEmpty()) {
            return Success(localPlaylists)
        }
        return playlistSongsRemoteDataSource.getPlaylistSongs(playlistId).alsoWhenSuccess {
            playlistSongsLocalDataSource.savePlaylistSongs(playlistId, it)
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