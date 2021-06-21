package com.mousiki.shared.data.datasource.channel

import com.mousiki.shared.data.datasource.musicTracks
import com.mousiki.shared.data.config.RemoteAppConfig
import com.mousiki.shared.data.models.Artist
import com.mousiki.shared.data.models.tracks
import com.mousiki.shared.data.remote.api.MousikiApi
import com.mousiki.shared.data.remote.mapper.YTBSearchResultToVideoId
import com.mousiki.shared.data.remote.mapper.YTBVideoToTrack
import com.mousiki.shared.data.remote.mapper.toListMapper
import com.mousiki.shared.data.remote.runner.NetworkRunner
import com.mousiki.shared.domain.models.MusicTrack
import com.mousiki.shared.domain.result.NO_RESULT
import com.mousiki.shared.domain.result.Result
import com.mousiki.shared.fs.FileSystem
import com.mousiki.shared.fs.PathComponent
import com.mousiki.shared.utils.ConnectivityChecker
import com.mousiki.shared.utils.StorageApi
import kotlinx.serialization.json.Json

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-24.
 ***************************************
 */
class ChannelSongsRemoteDataSource(
    private val mousikiApi: MousikiApi,
    private val networkRunner: NetworkRunner,
    private val searchMapper: YTBSearchResultToVideoId,
    private val trackMapper: YTBVideoToTrack,
    private val json: Json,
    private val connectivityState: ConnectivityChecker,
    private val storage: StorageApi,
    private val appConfig: RemoteAppConfig
) {

    suspend fun getChannelSongs(artist: Artist): Result<List<MusicTrack>> {
        if (appConfig.searchArtistTracksFromMousikiApi()) {
            val resultSearch = loadArtistTracksFromMousikiApi(artist)
            if (resultSearch is Result.Success && resultSearch.data.isNotEmpty()) {
                return resultSearch
            }
        }

        // Check firebase
        val firebaseTracks = downloadArtistTracksFile(artist).musicTracks(json)

        if (firebaseTracks.isNotEmpty()) return Result.Success(firebaseTracks)

        // Go to youtube!
        // Get ids
        val result = networkRunner.executeNetworkCall(searchMapper.toListMapper()) {
            mousikiApi.channelVideoIds(artist.channelId).items ?: emptyList()
        } as? Result.Success ?: return NO_RESULT

        // 2 - Get videos
        val ids = result.data.joinToString { it.id }
        val resultYoutube = networkRunner.executeNetworkCall(trackMapper.toListMapper()) {
            mousikiApi.videos(ids).items ?: emptyList()
        }
        if (resultYoutube is Result.Success && resultYoutube.data.size > 3) {
            return resultYoutube
        }

        // Fallback to Mousiki api
        return loadArtistTracksFromMousikiApi(artist)
    }

    private suspend fun loadArtistTracksFromMousikiApi(artist: Artist): Result<List<MusicTrack>> {
        return networkRunner.loadWithRetry(appConfig.artistSongsApiConfig()) { apiUrl ->
            mousikiApi.searchChannel(apiUrl, artist.channelId).tracks()
        }
    }


    private suspend fun downloadArtistTracksFile(artist: Artist): PathComponent {
        val localFile = artistSongsFile(artist)
        return storage.downloadFile(
            remoteUrl = "$BASE_URL_STORAGE$STORAGE_ARTISTS_SONGS_DIR/${
                artist.countryCode.orEmpty().toLowerCase()
            }/${FileSystem.stat(localFile)?.name}",
            path = localFile,
            connectivityState = connectivityState
        )
    }

    private fun artistSongsFile(artist: Artist): PathComponent {
        val fileName = "${artist.channelId}.json"
        val artistsSongsHome = FileSystem.contentsDirectory
            .absolutePath
            ?.byAppending("/$STORAGE_ARTISTS_SONGS_DIR/")!!
        if (!FileSystem.exists(artistsSongsHome)) FileSystem.mkdir(artistsSongsHome, true)
        return artistsSongsHome.byAppending(fileName)!!
    }

    companion object {
        private const val BASE_URL_STORAGE = "gs://mousiki-e3e22.appspot.com/"
        private const val STORAGE_ARTISTS_SONGS_DIR = "artistsTracks"
    }
}