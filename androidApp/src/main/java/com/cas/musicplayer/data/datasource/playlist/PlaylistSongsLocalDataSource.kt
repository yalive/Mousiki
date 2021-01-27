package com.cas.musicplayer.data.datasource.playlist

import android.os.SystemClock
import com.cas.musicplayer.data.local.database.dao.LightSongDao
import com.cas.musicplayer.data.local.database.dao.PlaylistSongsDao
import com.cas.musicplayer.data.local.models.LightSongEntity
import com.cas.musicplayer.data.local.models.PlaylistSongEntity
import com.cas.musicplayer.data.local.models.toMusicTrack
import com.cas.musicplayer.utils.bgContext
import com.mousiki.shared.domain.models.MusicTrack
import com.mousiki.shared.preference.PreferencesHelper
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-24.
 ***************************************
 */

class PlaylistSongsLocalDataSource @Inject constructor(
    private val playlistSongsDao: PlaylistSongsDao,
    private val lightSongDao: LightSongDao,
    private val preferences: PreferencesHelper
) {

    suspend fun getPlaylistSongs(playlistId: String): List<MusicTrack> = withContext(bgContext) {
        return@withContext playlistSongsDao.getPlaylistTracks(playlistId).map {
            it.toMusicTrack()
        }
    }

    suspend fun savePlaylistSongs(playlistId: String, tracks: List<MusicTrack>) =
        withContext(bgContext) {
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

    suspend fun getPlaylistLightSongs(playlistId: String): List<MusicTrack> =
        withContext(bgContext) {
            return@withContext lightSongDao.getPlaylistSongs(playlistId).map {
                it.toMusicTrack()
            }
        }

    suspend fun savePlaylistLightSongs(playlistId: String, tracks: List<MusicTrack>) =
        withContext(bgContext) {
            if (numberOfRows() == 0) {
                preferences.setChartsUpdateDate()
            }
            val channelSongs = tracks.map {
                LightSongEntity(
                    playlistId = playlistId,
                    title = it.title,
                    youtubeId = it.youtubeId
                )
            }
            lightSongDao.insert(channelSongs)
        }

    suspend fun numberOfRows(): Int = withContext(bgContext) {
        return@withContext lightSongDao.count()
    }

    fun expired(): Boolean {
        val updateDate = preferences.getChartsUpdateDate()
        val cacheDuration = (SystemClock.elapsedRealtime() - updateDate) / 1000
        return cacheDuration - CACHE_MAX_DURATION_SECONDS >= 0
    }

    suspend fun clear() = withContext(bgContext) {
        lightSongDao.clear()
    }

    companion object {
        private const val ONE_DAY_SECONDS = 24 * 60 * 60
        private const val CACHE_MAX_DURATION_SECONDS = 30 * ONE_DAY_SECONDS // 8 days
    }
}