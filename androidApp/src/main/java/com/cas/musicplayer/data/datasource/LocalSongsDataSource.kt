package com.cas.musicplayer.data.datasource

import android.os.SystemClock
import com.cas.musicplayer.data.local.database.dao.TrendingSongsDao
import com.cas.musicplayer.data.local.models.TrendingSongEntity
import com.cas.musicplayer.data.local.models.toMusicTrack
import com.cas.musicplayer.data.preferences.PreferencesHelper
import com.mousiki.shared.domain.models.MusicTrack
import com.cas.musicplayer.utils.bgContext
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-20.
 ***************************************
 */
class LocalSongsDataSource @Inject constructor(
    private val trendingSongsDao: TrendingSongsDao,
    private val preferences: PreferencesHelper
) {

    suspend fun getTrendingSongs(max: Int, lastKnown: MusicTrack? = null): List<MusicTrack> =
        withContext(bgContext) {
            if (lastKnown != null) {
                val songEntity = trendingSongsDao.getByYoutubeId(lastKnown.youtubeId)
                val songs = trendingSongsDao.getSongsStartingFrom(songEntity.id, max)
                return@withContext songs.map { it.toMusicTrack() }
            }
            return@withContext trendingSongsDao.getSongs(max).map { it.toMusicTrack() }
        }

    suspend fun saveTrendingSongs(tracks: List<MusicTrack>) = withContext(bgContext) {
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

    suspend fun numberOfSongs(): Int = withContext(bgContext) {
        return@withContext trendingSongsDao.count()
    }

    fun expired(): Boolean {
        val updateDate = preferences.getMostPopularSongsUpdateDate()
        val cacheDuration = (SystemClock.elapsedRealtime() - updateDate) / 1000
        return cacheDuration - CACHE_MAX_DURATION_SECONDS >= 0
    }

    suspend fun clear() = withContext(bgContext) {
        trendingSongsDao.clear()
    }

    companion object {
        private const val ONE_DAY_SECONDS = 24 * 60 * 60
        private const val CACHE_MAX_DURATION_SECONDS = 7 * ONE_DAY_SECONDS // 7 days
    }
}