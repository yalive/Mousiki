package com.cas.musicplayer.data.datasource.playlist

import android.content.Context
import com.cas.musicplayer.data.datasource.musicTracks
import com.cas.musicplayer.data.firebase.downloadFile
import com.cas.musicplayer.data.repositories.ChartsRepository
import com.google.firebase.storage.FirebaseStorage
import com.mousiki.shared.data.config.RemoteAppConfig
import com.mousiki.shared.data.models.tracks
import com.mousiki.shared.data.remote.api.MousikiApi
import com.mousiki.shared.data.remote.mapper.YTBPlaylistItemToVideoId
import com.mousiki.shared.data.remote.mapper.YTBVideoToTrack
import com.mousiki.shared.data.remote.mapper.toListMapper
import com.mousiki.shared.data.remote.runner.NetworkRunner
import com.mousiki.shared.data.repository.GenresRepository
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
class PlaylistSongsRemoteDataSource @Inject constructor(
    private val mousikiApi: MousikiApi,
    private val networkRunner: NetworkRunner,
    private val videoIdMapper: YTBPlaylistItemToVideoId,
    private val trackMapper: YTBVideoToTrack,
    private val json: Json,
    private var appConfig: RemoteAppConfig,
    private val chartsRepository: ChartsRepository,
    private val genresRepository: GenresRepository,
    private val appContext: Context,
    private val connectivityState: ConnectivityChecker,
    private val storage: FirebaseStorage
) {

    suspend fun getPlaylistSongs(playlistId: String): Result<List<MusicTrack>> {
        val resultFromApi = networkRunner.loadWithRetry(appConfig.playlistApiConfig()) { apiUrl ->
            mousikiApi.getPlaylistDetail(apiUrl, playlistId).tracks()
        }
        if (resultFromApi is Result.Success && resultFromApi.data.size > 3) {
            return resultFromApi
        }

        val isTopTrackOfGenre = genresRepository.isTopTrackOfGenre(playlistId)
        val isChart = chartsRepository.isChart(playlistId)
        if ((appConfig.loadChartSongsFromFirebase() && isChart)
            || (appConfig.loadGenreSongsFromFirebase() && isTopTrackOfGenre)
        ) {
            // Load from firebase
            val directoryName = if (isChart) STORAGE_CHARTS_DIR else STORAGE_GENRES_DIR
            val firebaseTracks = downloadPlaylistFile(playlistId, directoryName).musicTracks(json)

            if (firebaseTracks.isNotEmpty()) {
                return Result.Success(firebaseTracks)
            }
        }

        // 1 - Get video ids
        val idsResult = networkRunner.executeNetworkCall(videoIdMapper.toListMapper()) {
            mousikiApi.playlistVideoIds(playlistId, 50).items ?: emptyList()
        } as? Result.Success ?: return NO_RESULT

        // 2 - Get videos
        val ids = idsResult.data.joinToString { it.id }
        return networkRunner.executeNetworkCall(trackMapper.toListMapper()) {
            mousikiApi.videos(ids).items!!
        }
    }

    private suspend fun downloadPlaylistFile(playlistId: String, directoryName: String): File {
        val fileName = "$playlistId.json"
        val fileDirPath =
            appContext.filesDir.absolutePath + File.separator + directoryName + File.separator
        val directory = File(fileDirPath)
        if (!directory.exists()) directory.mkdirs()
        val localFile = File(fileDirPath, fileName)

        return storage.downloadFile(
            remoteUrl = "$BASE_URL_STORAGE$directoryName/$fileName",
            localFile = localFile,
            connectivityState = connectivityState,
            logErrorMessage = "Cannot load $playlistId songs file from firebase"
        )
    }

    companion object {
        private const val BASE_URL_STORAGE = "gs://mousiki-e3e22.appspot.com/"
        private const val STORAGE_CHARTS_DIR = "charts"
        private const val STORAGE_GENRES_DIR = "genres"
    }
}