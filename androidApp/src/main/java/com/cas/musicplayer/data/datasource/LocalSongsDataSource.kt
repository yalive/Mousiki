package com.cas.musicplayer.data.datasource

import android.os.SystemClock
import com.cas.musicplayer.MousikiDb
import com.cas.musicplayer.data.local.database.dao.TrendingSongsDao
import com.cas.musicplayer.data.local.models.TrendingSongEntity
import com.cas.musicplayer.data.preferences.PreferencesHelper
import com.mousiki.shared.data.db.toMusicTrack
import com.mousiki.shared.domain.models.MusicTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-20.
 ***************************************
 */
class LocalSongsDataSource @Inject constructor(
    private val preferences: PreferencesHelper,
    private val db: MousikiDb,
    private val trendingSongsDao: TrendingSongsDao
) {

    suspend fun getTrendingSongs(max: Int, lastKnown: MusicTrack? = null): List<MusicTrack> =
        withContext(Dispatchers.IO) {
            val dao = db.trendingTrackQueries
            if (lastKnown != null) {
                val songEntity = dao.getByYoutubeId(lastKnown.youtubeId).executeAsOneOrNull()
                if (songEntity != null) {
                    val songs =
                        dao.getSongsStartingFrom(songEntity.id, max.toLong()).executeAsList()
                    return@withContext songs.map { it.toMusicTrack() }
                }
            }
            val listRoom = dao.getSongs(max.toLong()).executeAsList()
            print("")
            return@withContext listRoom.map { it.toMusicTrack() }
        }

    suspend fun saveTrendingSongs(tracks: List<MusicTrack>) = withContext(Dispatchers.IO) {
        if (numberOfSongs() == 0) {
            preferences.setMostPopularSongsUpdateDate()
        }

        val trendingTracks = tracks.map {
            TrendingSongEntity(
                youtubeId = it.youtubeId,
                title = it.title,
                duration = it.duration
            )
        }
        trendingSongsDao.insert(trendingTracks)
    }

    suspend fun numberOfSongs(): Int = withContext(Dispatchers.IO) {
        return@withContext trendingSongsDao.count()
    }

    fun expired(): Boolean {
        val updateDate = preferences.getMostPopularSongsUpdateDate()
        val cacheDuration = (SystemClock.elapsedRealtime() - updateDate) / 1000
        return cacheDuration - CACHE_MAX_DURATION_SECONDS >= 0
    }

    suspend fun clear() = withContext(Dispatchers.IO) {
        trendingSongsDao.clear()
    }

    companion object {
        private const val ONE_DAY_SECONDS = 24 * 60 * 60
        private const val CACHE_MAX_DURATION_SECONDS = 7 * ONE_DAY_SECONDS // 7 days
    }
}