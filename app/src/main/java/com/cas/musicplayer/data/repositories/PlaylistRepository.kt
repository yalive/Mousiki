package com.cas.musicplayer.data.repositories

import com.cas.common.result.Result
import com.cas.common.result.Result.Success
import com.cas.common.result.alsoWhenSuccess
import com.cas.musicplayer.data.datasource.channel.ChannelPlaylistsLocalDataSource
import com.cas.musicplayer.data.datasource.channel.ChannelPlaylistsRemoteDataSource
import com.cas.musicplayer.data.datasource.playlist.PlaylistSongTitleLocalDataSource
import com.cas.musicplayer.data.datasource.playlist.PlaylistSongTitleRemoteDataSource
import com.cas.musicplayer.data.datasource.playlist.PlaylistSongsLocalDataSource
import com.cas.musicplayer.data.datasource.playlist.PlaylistSongsRemoteDataSource
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.domain.model.Playlist
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
    private val songTitleLocalDataSource: PlaylistSongTitleLocalDataSource,
    private val songTitleRemoteDataSource: PlaylistSongTitleRemoteDataSource
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
        val localPlaylists = songTitleLocalDataSource.getPlaylistSongs(playlistId)
        if (localPlaylists.isNotEmpty()) {
            return Success(localPlaylists)
        }
        return songTitleRemoteDataSource.getPlaylistSongs(playlistId).alsoWhenSuccess {
            songTitleLocalDataSource.savePlaylistSongs(playlistId, it)
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