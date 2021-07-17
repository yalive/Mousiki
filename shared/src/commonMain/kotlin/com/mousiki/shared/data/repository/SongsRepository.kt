package com.mousiki.shared.data.repository

import com.cas.musicplayer.MousikiDb
import com.mousiki.shared.data.datasource.LocalSongsDataSource
import com.mousiki.shared.data.datasource.RemoteSongsDataSource
import com.mousiki.shared.data.db.FavouriteTrackEntity
import com.mousiki.shared.data.db.toTrack
import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.domain.models.YtbTrack
import com.mousiki.shared.domain.result.Result
import com.mousiki.shared.domain.result.alsoWhenSuccess
import com.mousiki.shared.preference.UserPrefs
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
        lastKnown: YtbTrack? = null
    ): Result<List<YtbTrack>> {
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

    suspend fun getFavouriteSongs(max: Int = 10): List<Track> =
        withContext(Dispatchers.Default) {
            return@withContext favouriteTracksDaoSql.getTracks(max.toLong()).executeAsList().map {
                it.toTrack()
            }
        }

    suspend fun getFavouriteSongsFlow(max: Int = 10): Flow<List<Track>> =
        withContext(Dispatchers.Default) {
            return@withContext favouriteTracksDaoSql.getTracks(max.toLong())
                .asFlow()
                .mapToList()
                .map { it.map(FavouriteTrackEntity::toTrack) }
        }

    suspend fun addSongToFavourite(track: Track) = withContext(Dispatchers.Default) {
        favouriteTracksDaoSql.insert(
            FavouriteTrackEntity(
                id = 0,
                track_id = track.id,
                title = track.title,
                duration = track.duration,
                type = track.type,
                artist_name = track.artistName,
                artist_id = track.artistId
            )
        )
        UserPrefs.saveFav(track.id, true)
    }

    suspend fun removeSongFromFavourite(trackId: String) = withContext(Dispatchers.Default) {
        favouriteTracksDaoSql.deleteTrack(trackId)
        UserPrefs.saveFav(trackId, false)
    }
}