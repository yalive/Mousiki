package com.mousiki.shared.data.datasource

import com.cas.musicplayer.MousikiDb
import com.mousiki.shared.data.db.TrendingTrackEntity
import com.mousiki.shared.data.db.toMusicTrack
import com.mousiki.shared.db.TrendingTrackQueries
import com.mousiki.shared.domain.models.MusicTrack
import com.mousiki.shared.preference.PreferencesHelper
import com.mousiki.shared.utils.elapsedRealtime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-20.
 ***************************************
 */
class LocalSongsDataSource constructor(
    private val preferences: PreferencesHelper,
    private val db: MousikiDb
) {

    private val trendingQuery: TrendingTrackQueries = db.trendingTrackQueries

    suspend fun getTrendingSongs(max: Int, lastKnown: MusicTrack? = null): List<MusicTrack> =
        withContext(Dispatchers.Default) {
            numberOfSongs()
            val dao = db.trendingTrackQueries
            if (lastKnown != null) {
                val songEntity = dao.getByYoutubeId(lastKnown.youtubeId).executeAsOneOrNull()
                if (songEntity != null) {
                    val songs =
                        dao.getSongsStartingFrom(songEntity.id, max.toLong()).executeAsList()
                    return@withContext songs.map { it.toMusicTrack() }
                }
            }
            return@withContext dao.getSongs(max.toLong())
                .executeAsList()
                .map { it.toMusicTrack() }
        }

    suspend fun saveTrendingSongs(tracks: List<MusicTrack>) = withContext(Dispatchers.Default) {
        if (numberOfSongs() == 0) {
            preferences.setMostPopularSongsUpdateDate()
        }

        val trendingTracks = tracks.map {
            TrendingTrackEntity(
                id = 0,
                youtube_id = it.youtubeId,
                title = it.title,
                duration = it.duration
            )
        }

        db.transaction {
            trendingTracks.forEach { track ->
                trendingQuery.insert(track)
            }
        }
    }

    suspend fun numberOfSongs(): Int = withContext(Dispatchers.Default) {
        return@withContext trendingQuery.count().executeAsOneOrNull()?.toInt() ?: 0
    }

    fun expired(): Boolean {
        val updateDate = preferences.getMostPopularSongsUpdateDate()
        val cacheDuration = (elapsedRealtime - updateDate) / 1000
        return cacheDuration - CACHE_MAX_DURATION_SECONDS >= 0
    }

    suspend fun clear() = withContext(Dispatchers.Default) {
        trendingQuery.clear()
    }

    companion object {
        private const val ONE_DAY_SECONDS = 24 * 60 * 60
        private const val CACHE_MAX_DURATION_SECONDS = 7 * ONE_DAY_SECONDS // 7 days
    }
}