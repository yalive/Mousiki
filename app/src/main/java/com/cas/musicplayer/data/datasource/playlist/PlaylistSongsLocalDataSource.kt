package com.cas.musicplayer.data.datasource.playlist

import com.cas.musicplayer.data.local.database.dao.PlaylistSongsDao
import com.cas.musicplayer.data.local.models.PlaylistSongEntity
import com.cas.musicplayer.data.local.models.toMusicTrack
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.utils.bgContext
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-24.
 ***************************************
 */

class PlaylistSongsLocalDataSource @Inject constructor(
    private val playlistSongsDao: PlaylistSongsDao
) {

    suspend fun getPlaylistSongs(playlistId: String): List<MusicTrack> = withContext(bgContext) {
        return@withContext playlistSongsDao.getPlaylistTracks(playlistId).map {
            it.toMusicTrack()
        }
    }

    suspend fun savePlaylistSongs(playlistId: String, tracks: List<MusicTrack>) = withContext(bgContext) {
        val channelSongs = tracks.map {
            PlaylistSongEntity(
                youtubeId = it.youtubeId,
                playlistId = playlistId,
                title = it.title,
                duration = it.duration
            )
        }
        playlistSongsDao.insert(channelSongs)
    }
}