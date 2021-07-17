package com.mousiki.shared.data.repository

import com.cas.musicplayer.MousikiDb
import com.mousiki.shared.data.db.RecentPlayedTrack
import com.mousiki.shared.data.db.toTrack
import com.mousiki.shared.db.Db_recentTrack
import com.mousiki.shared.domain.models.Track
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
    private val recentDao by lazy { db.recentPlayedTracksQueries }

    suspend fun addTrackToRecent(track: Track) {
        val recentTrack = recentDao.getByTrackId(track.id).executeAsOneOrNull()
        val playCount = if (recentTrack != null) recentTrack.play_count + 1 else 1
        recentDao.insert(
            RecentPlayedTrack(
                id = 0,
                track_id = track.id,
                title = track.title,
                duration = track.duration,
                play_count = playCount,
                type = track.type,
                artist_name = track.artistName,
                artist_id = track.artistId
            )
        )
    }

    suspend fun getRecentlyPlayedTracks(max: Int = 10): List<Track> {
        return recentDao.getTracks(max.toLong()).executeAsList().map {
            it.toTrack()
        }
    }

    suspend fun getRecentlyPlayedTracksFlow(max: Int = 10): Flow<List<Track>> {
        return recentDao.getTracks(max.toLong())
            .asFlow()
            .mapToList()
            .map { it.map(RecentPlayedTrack::toTrack) }
    }

    suspend fun getHeavyList(max: Int = 10): List<Track> {
        return recentDao.getHeavyList(max.toLong()).executeAsList().map {
            it.toTrack()
        }
    }

    suspend fun getHeavyListFlow(max: Int = 10): Flow<List<Track>> =
        withContext(Dispatchers.Default) {
            return@withContext recentDao.getHeavyList(max.toLong())
                .asFlow()
                .mapToList()
                .map { it.map(Db_recentTrack::toTrack) }
        }
}