package com.cas.musicplayer.data.datasource.channel

import android.annotation.SuppressLint
import android.content.Context
import com.cas.musicplayer.data.datasource.musicTracks
import com.cas.musicplayer.data.firebase.downloadFile
import com.google.firebase.storage.FirebaseStorage
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
import com.mousiki.shared.utils.ConnectivityChecker
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-24.
 ***************************************
 */
class ChannelSongsRemoteDataSource @Inject constructor(
    private val mousikiApi: MousikiApi,
    private val networkRunner: NetworkRunner,
    private val searchMapper: YTBSearchResultToVideoId,
    private val trackMapper: YTBVideoToTrack,
    private val appContext: Context,
    private val json: Json,
    private val connectivityState: ConnectivityChecker,
    private val storage: FirebaseStorage,
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


    @SuppressLint("DefaultLocale")
    private suspend fun downloadArtistTracksFile(artist: Artist): File {
        val localFile = artistSongsFile(artist)
        return storage.downloadFile(
            remoteUrl = "${BASE_URL_STORAGE}${STORAGE_ARTISTS_SONGS_DIR}/${
                artist.countryCode.orEmpty().toLowerCase()
            }/${localFile.name}",
            localFile = localFile,
            connectivityState = connectivityState
        )
    }

    private fun artistSongsFile(artist: Artist): File {
        val fileName = "${artist.channelId}.json"
        val fileDirPath =
            appContext.filesDir.absolutePath + File.separator + STORAGE_ARTISTS_SONGS_DIR + File.separator
        val directory = File(fileDirPath)
        if (!directory.exists()) directory.mkdirs()
        return File(fileDirPath, fileName)
    }

    companion object {
        private const val BASE_URL_STORAGE = "gs://mousiki-e3e22.appspot.com/"
        private const val STORAGE_ARTISTS_SONGS_DIR = "artistsTracks"
    }
}