package com.cas.musicplayer.data.repositories

import com.cas.musicplayer.data.local.database.dao.RecentlyPlayedTracksDao
import com.cas.musicplayer.data.local.models.RecentlyPlayedTrack
import com.cas.musicplayer.data.local.models.toMusicTrack
import com.cas.musicplayer.domain.model.MusicTrack
import javax.inject.Inject
import javax.inject.Singleton

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-26.
 ***************************************
 */
@Singleton
class StatisticsRepository @Inject constructor(
    private val recentlyPlayedTracksDao: RecentlyPlayedTracksDao
) {

    suspend fun addTrackToRecent(track: MusicTrack) {
        recentlyPlayedTracksDao.insert(RecentlyPlayedTrack.fromMusicTrack(track))
    }

    suspend fun getRecentlyPlayedTracks(): List<MusicTrack> {
        return recentlyPlayedTracksDao.getSongs().map {
            it.toMusicTrack()
        }
    }
}