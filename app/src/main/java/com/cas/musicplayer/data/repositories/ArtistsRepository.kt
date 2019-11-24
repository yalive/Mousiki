package com.cas.musicplayer.data.repositories

import com.cas.common.result.Result
import com.cas.common.result.Result.Success
import com.cas.common.result.alsoWhenSuccess
import com.cas.musicplayer.data.datasource.ArtistsLocalDataSource
import com.cas.musicplayer.data.datasource.ArtistsRemoteDataSource
import com.cas.musicplayer.data.datasource.channel.ChannelSongsLocalDataSource
import com.cas.musicplayer.data.datasource.channel.ChannelSongsRemoteDataSource
import com.cas.musicplayer.data.remote.models.Artist
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.utils.Utils
import com.cas.musicplayer.utils.bgContext
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-10.
 ***************************************
 */

@Singleton
class ArtistsRepository @Inject constructor(
    private var gson: Gson,
    private val localDataSource: ArtistsLocalDataSource,
    private val remoteDataSource: ArtistsRemoteDataSource,
    private val channelLocalDataSource: ChannelSongsLocalDataSource,
    private val channelRemoteDataSource: ChannelSongsRemoteDataSource
) {

    suspend fun getArtistsChannels(ids: List<String>): Result<List<Artist>> {
        val artistsDb = localDataSource.getArtists(ids)
        if (artistsDb.isNotEmpty()) {
            return Success(artistsDb)
        }
        return remoteDataSource.getArtists(ids).alsoWhenSuccess {
            localDataSource.saveArtists(it)
        }
    }

    suspend fun getArtistsFromFile(): List<Artist> = withContext(bgContext) {
        val json = Utils.loadStringJSONFromAsset("artists.json")
        val artists = gson.fromJson<List<Artist>>(json, object : TypeToken<List<Artist>>() {}.type)
        val distinctBy = artists.distinctBy { artist -> artist.channelId }
        distinctBy.sortedBy { artist -> artist.name }
    }

    suspend fun getArtistTracks(artistChannelId: String): Result<List<MusicTrack>> {
        val localChannelSongs = channelLocalDataSource.getChannelSongs(artistChannelId)
        if (localChannelSongs.isNotEmpty()) {
            return Success(localChannelSongs)
        }
        return channelRemoteDataSource.getChannelSongs(artistChannelId).alsoWhenSuccess {
            channelLocalDataSource.saveChannelSongs(artistChannelId, it)
        }
    }
}