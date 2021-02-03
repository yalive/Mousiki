package com.mousiki.shared.data.repository

import com.cas.musicplayer.MousikiDb
import com.mousiki.shared.data.db.toMusicTrack
import com.mousiki.shared.db.Historic_tracks
import com.mousiki.shared.db.Recent_played_tracks
import com.mousiki.shared.domain.models.MusicTrack
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-26.
 ***************************************
 */
class StatisticsRepository(
    private val db: MousikiDb
) {

    private val recentlyPlayedTracksDao by lazy { db.recentPlayedTracksQueries }
    private val historicTracksDao by lazy { db.historicTracksQueries }

    suspend fun addTrackToRecent(track: MusicTrack) {
        recentlyPlayedTracksDao.insert(
            Recent_played_tracks(
                id = 0,
                youtube_id = track.youtubeId,
                title = track.title,
                duration = track.duration
            )
        )
        val historicTrackEntity =
            historicTracksDao.getByYoutubeId(track.youtubeId).executeAsOneOrNull()
        if (historicTrackEntity == null) {
            historicTracksDao.insert(
                Historic_tracks(
                    id = 0,
                    youtube_id = track.youtubeId,
                    title = track.title,
                    duration = track.duration,
                    count = 1
                )
            )
        } else {
            historicTracksDao.incrementPlayCount(track.youtubeId)
        }
    }

    suspend fun getRecentlyPlayedTracks(max: Int = 10): List<MusicTrack> {
        return recentlyPlayedTracksDao.getSongs(max.toLong()).executeAsList().map {
            it.toMusicTrack()
        }
    }

    suspend fun getRecentlyPlayedTracksFlow(max: Int = 10): Flow<List<MusicTrack>> {
        return recentlyPlayedTracksDao.getSongs(max.toLong())
            .asFlow()
            .mapToList()
            .map { it.map(Recent_played_tracks::toMusicTrack) }
    }

    suspend fun getHeavyList(max: Int = 10): List<MusicTrack> {
        return historicTracksDao.getHeavyList(max.toLong()).executeAsList().map {
            it.toMusicTrack()
        }
    }

    suspend fun getHeavyListFlow(max: Int = 10): Flow<List<MusicTrack>> =
        withContext(Dispatchers.Default) {
            return@withContext historicTracksDao.getHeavyList(max.toLong())
                .asFlow()
                .mapToList()
                .map { it.map(Historic_tracks::toMusicTrack) }
        }
}