package com.cas.musicplayer.data.repositories

import com.cas.musicplayer.data.local.database.dao.HistoricTracksDao
import com.cas.musicplayer.data.local.database.dao.RecentlyPlayedTracksDao
import com.cas.musicplayer.data.local.models.HistoricTrackEntity
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
    private val recentlyPlayedTracksDao: RecentlyPlayedTracksDao,
    private val historicTracksDao: HistoricTracksDao
) {

    suspend fun addTrackToRecent(track: MusicTrack) {
        recentlyPlayedTracksDao.insert(RecentlyPlayedTrack.fromMusicTrack(track))
        val historicTrackEntity = historicTracksDao.getByYoutubeId(track.youtubeId)
        if (historicTrackEntity == null) {
            historicTracksDao.insert(
                HistoricTrackEntity(
                    youtubeId = track.youtubeId,
                    title = track.title,
                    duration = track.duration,
                    count = 1
                )
            )
        } else {
            historicTracksDao.incrementPlayCount(track.youtubeId)
        }
    }

    suspend fun getRecentlyPlayedTracks(): List<MusicTrack> {
        return recentlyPlayedTracksDao.getSongs().map {
            it.toMusicTrack()
        }
    }

    suspend fun getHeavyList(max: Int = 10): List<MusicTrack> {
        return historicTracksDao.getHeavyList(max).map {
            it.toMusicTrack()
        }
    }
}