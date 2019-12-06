package com.cas.musicplayer.data.repositories

import com.cas.common.result.Result
import com.cas.common.result.alsoWhenSuccess
import com.cas.musicplayer.data.datasource.LocalSongsDataSource
import com.cas.musicplayer.data.datasource.RemoteSongsDataSource
import com.cas.musicplayer.data.local.database.dao.FavouriteTracksDao
import com.cas.musicplayer.data.local.models.FavouriteSongEntity
import com.cas.musicplayer.data.local.models.toMusicTrack
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.utils.NetworkUtils
import com.cas.musicplayer.utils.bgContext
import kotlinx.coroutines.withContext
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
    private val localDataSource: LocalSongsDataSource,
    private val favouriteTracksDao: FavouriteTracksDao,
    private val networkUtils: NetworkUtils
) {
    suspend fun getTrendingSongs(max: Int, lastKnown: MusicTrack? = null): Result<List<MusicTrack>> {
        if (lastKnown == null) {
            // First load
            if (localDataSource.numberOfSongs() > 0 && localDataSource.expired() && networkUtils.hasNetworkConnection()) {
                localDataSource.clear()
            }
        }
        val cachedTracks = localDataSource.getTrendingSongs(max, lastKnown)
        if (cachedTracks.isNotEmpty()) return Result.Success(cachedTracks)
        return remoteDataSource.getTrendingSongs(max).alsoWhenSuccess {
            localDataSource.saveTrendingSongs(it)
        }
    }

    suspend fun getFavouriteSongs(max: Int = 10): List<MusicTrack> = withContext(bgContext) {
        return@withContext favouriteTracksDao.getSongs(max).map {
            it.toMusicTrack()
        }
    }

    suspend fun addSongToFavourite(track: MusicTrack) = withContext(bgContext) {
        favouriteTracksDao.insertMusicTrack(
            FavouriteSongEntity(
                youtubeId = track.youtubeId,
                title = track.title,
                duration = track.duration
            )
        )
    }

    suspend fun removeSongFromFavourite(track: MusicTrack) = withContext(bgContext) {
        favouriteTracksDao.deleteSong(track.youtubeId)
    }
}