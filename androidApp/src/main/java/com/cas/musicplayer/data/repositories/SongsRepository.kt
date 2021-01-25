package com.cas.musicplayer.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.cas.musicplayer.data.datasource.LocalSongsDataSource
import com.cas.musicplayer.data.datasource.RemoteSongsDataSource
import com.cas.musicplayer.data.local.database.dao.FavouriteTracksDao
import com.cas.musicplayer.data.local.models.FavouriteSongEntity
import com.cas.musicplayer.data.local.models.toMusicTrack
import com.cas.musicplayer.utils.NetworkUtils
import com.cas.musicplayer.utils.UserPrefs
import com.cas.musicplayer.utils.bgContext
import com.mousiki.shared.domain.models.MusicTrack
import com.mousiki.shared.domain.result.Result
import com.mousiki.shared.domain.result.alsoWhenSuccess
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
    suspend fun getTrendingSongs(
        max: Int,
        lastKnown: MusicTrack? = null
    ): Result<List<MusicTrack>> {
        if (lastKnown == null) {
            // First load
            if (localDataSource.numberOfSongs() > 0 && localDataSource.expired() && networkUtils.hasNetworkConnection()) {
                localDataSource.clear()
                remoteDataSource.deleteLocalTrendingFile()
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

    suspend fun getFavouriteSongsLive(max: Int = 10): LiveData<List<MusicTrack>> =
        withContext(bgContext) {
            return@withContext Transformations.map(favouriteTracksDao.getSongsLive(max)) { input ->
                input.map { it.toMusicTrack() }
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
        UserPrefs.saveFav(track.youtubeId, true)
    }

    suspend fun removeSongFromFavourite(trackId: String) = withContext(bgContext) {
        favouriteTracksDao.deleteSong(trackId)
        UserPrefs.saveFav(trackId, false)
    }
}