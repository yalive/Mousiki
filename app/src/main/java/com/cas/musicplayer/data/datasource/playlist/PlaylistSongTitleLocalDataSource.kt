package com.cas.musicplayer.data.datasource.playlist

import com.cas.musicplayer.data.local.database.dao.SongTitleDao
import com.cas.musicplayer.data.local.models.SongTitleEntity
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

class PlaylistSongTitleLocalDataSource @Inject constructor(
    private val songTitleDao: SongTitleDao
) {

    suspend fun getPlaylistSongs(playlistId: String): List<MusicTrack> = withContext(bgContext) {
        return@withContext songTitleDao.getPlaylistSongs(playlistId).map {
            it.toMusicTrack()
        }
    }

    suspend fun savePlaylistSongs(playlistId: String, tracks: List<MusicTrack>) = withContext(bgContext) {
        val channelSongs = tracks.map {
            SongTitleEntity(
                playlistId = playlistId,
                title = it.title
            )
        }
        songTitleDao.insert(channelSongs)
    }
}