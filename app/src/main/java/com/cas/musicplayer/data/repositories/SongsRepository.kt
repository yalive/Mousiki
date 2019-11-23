package com.cas.musicplayer.data.repositories

import com.cas.common.result.Result
import com.cas.common.result.alsoWhenSuccess
import com.cas.musicplayer.data.datasource.LocalSongsDataSource
import com.cas.musicplayer.data.datasource.RemoteSongsDataSource
import com.cas.musicplayer.domain.model.MusicTrack
import javax.inject.Inject
import javax.inject.Singleton

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-09.
 ***************************************
 */
@Singleton
class SongsRepository @Inject constructor(
    private val remoteDataSource: RemoteSongsDataSource,
    private val localDataSource: LocalSongsDataSource
) {
    suspend fun getTrendingSongs(max: Int, lastKnown: MusicTrack? = null): Result<List<MusicTrack>> {
        val cachedTracks = localDataSource.getTrendingSongs(max, lastKnown)
        if (cachedTracks.isNotEmpty()) return Result.Success(cachedTracks)
        return remoteDataSource.getTrendingSongs(max).alsoWhenSuccess {
            localDataSource.saveTrendingSongs(it)
        }
    }
}