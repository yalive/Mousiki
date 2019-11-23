package com.cas.musicplayer.data.datasource

import com.cas.musicplayer.data.local.database.dao.TrendingSongsDao
import com.cas.musicplayer.data.local.database.dao.toMusicTrack
import com.cas.musicplayer.data.local.models.TrendingSongEntity
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.utils.bgContext
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-20.
 ***************************************
 */
class LocalSongsDataSource @Inject constructor(
    private val trendingSongsDao: TrendingSongsDao
) {

    suspend fun getTrendingSongs(max: Int, lastKnown: MusicTrack? = null): List<MusicTrack> = withContext(bgContext) {
        if (lastKnown != null) {
            val songEntity = trendingSongsDao.getByYoutubeId(lastKnown.youtubeId)
            val songs = trendingSongsDao.getSongsStartingFrom(songEntity.id, max)
            return@withContext songs.map { it.toMusicTrack() }
        }
        return@withContext trendingSongsDao.getSongs(max).map { it.toMusicTrack() }
    }

    suspend fun saveTrendingSongs(tracks: List<MusicTrack>) = withContext(bgContext) {
        val trendingTracks = tracks.map {
            TrendingSongEntity(
                youtubeId = it.youtubeId,
                title = it.title,
                duration = it.duration
            )
        }
        trendingSongsDao.insert(trendingTracks)
    }
}