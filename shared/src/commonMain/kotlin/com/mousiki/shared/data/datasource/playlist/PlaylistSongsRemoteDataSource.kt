package com.mousiki.shared.data.datasource.playlist

import com.mousiki.shared.data.datasource.musicTracks
import com.mousiki.shared.data.repository.ChartsRepository
import com.mousiki.shared.data.config.RemoteAppConfig
import com.mousiki.shared.data.models.tracks
import com.mousiki.shared.data.remote.api.MousikiApi
import com.mousiki.shared.data.remote.mapper.YTBPlaylistItemToVideoId
import com.mousiki.shared.data.remote.mapper.YTBVideoToTrack
import com.mousiki.shared.data.remote.mapper.toListMapper
import com.mousiki.shared.data.remote.runner.NetworkRunner
import com.mousiki.shared.data.repository.GenresRepository
import com.mousiki.shared.domain.models.YtbTrack
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
class PlaylistSongsRemoteDataSource(
    private val mousikiApi: MousikiApi,
    private val networkRunner: NetworkRunner,
    private val videoIdMapper: YTBPlaylistItemToVideoId,
    private val trackMapper: YTBVideoToTrack,
    private val json: Json,
    private var appConfig: RemoteAppConfig,
    private val chartsRepository: ChartsRepository,
    private val genresRepository: GenresRepository,
    private val connectivityState: ConnectivityChecker,
    private val storage: StorageApi
) {

    suspend fun getPlaylistSongs(playlistId: String): Result<List<YtbTrack>> {
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

    private suspend fun downloadPlaylistFile(
        playlistId: String,
        directoryName: String
    ): PathComponent {
        val fileName = "$playlistId.json"
        val playlistsHomeHome = FileSystem.contentsDirectory
            .absolutePath
            ?.byAppending("/$directoryName/")!!
        if (!FileSystem.exists(playlistsHomeHome)) FileSystem.mkdir(playlistsHomeHome, true)
        val playlistPath = playlistsHomeHome.byAppending(fileName)!!

        return storage.downloadFile(
            remoteUrl = "$BASE_URL_STORAGE$directoryName/$fileName",
            path = playlistPath,
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