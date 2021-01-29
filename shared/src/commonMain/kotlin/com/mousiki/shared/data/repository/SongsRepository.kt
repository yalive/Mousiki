package com.mousiki.shared.data.repository

import com.cas.musicplayer.MousikiDb
import com.mousiki.shared.data.datasource.LocalSongsDataSource
import com.mousiki.shared.data.datasource.RemoteSongsDataSource
import com.mousiki.shared.data.db.toMusicTrack
import com.mousiki.shared.db.Favourite_tracks
import com.mousiki.shared.domain.models.MusicTrack
import com.mousiki.shared.domain.result.Result
import com.mousiki.shared.domain.result.alsoWhenSuccess
import com.mousiki.shared.utils.NetworkUtils
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-09.
 ***************************************
 */
class SongsRepository(
    private val db: MousikiDb,
    private val remoteDataSource: RemoteSongsDataSource,
    private val localDataSource: LocalSongsDataSource,
    private val networkUtils: NetworkUtils
) {
    private val favouriteTracksDaoSql by lazy { db.favouriteTracksQueries }

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

    suspend fun getFavouriteSongs(max: Int = 10): List<MusicTrack> =
        withContext(Dispatchers.Default) {
            return@withContext favouriteTracksDaoSql.getSongs(max.toLong()).executeAsList().map {
                it.toMusicTrack()
            }
        }

    suspend fun getFavouriteSongsFlow(max: Int = 10): Flow<List<MusicTrack>> =
        withContext(Dispatchers.Default) {
            return@withContext favouriteTracksDaoSql.getSongs(max.toLong())
                .asFlow()
                .mapToList()
                .map { it.map(Favourite_tracks::toMusicTrack) }
        }

    suspend fun addSongToFavourite(track: MusicTrack) = withContext(Dispatchers.Default) {
        favouriteTracksDaoSql.insert(
            Favourite_tracks(
                id = 0,
                youtube_id = track.youtubeId,
                title = track.title,
                duration = track.duration
            )
        )
        //UserPrefs.saveFav(track.youtubeId, true)
    }

    suspend fun removeSongFromFavourite(trackId: String) = withContext(Dispatchers.Default) {
        favouriteTracksDaoSql.deleteSong(trackId)
       // UserPrefs.saveFav(trackId, false)
    }
}