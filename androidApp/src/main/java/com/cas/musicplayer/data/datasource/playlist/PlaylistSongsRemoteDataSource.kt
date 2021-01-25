package com.cas.musicplayer.data.datasource.playlist

import android.content.Context
import com.cas.common.connectivity.ConnectivityState
import com.cas.common.result.NO_RESULT
import com.cas.common.result.Result
import com.cas.musicplayer.data.config.RemoteAppConfig
import com.cas.musicplayer.data.remote.mappers.YTBPlaylistItemToTrack
import com.cas.musicplayer.data.remote.mappers.YTBPlaylistItemToVideoId
import com.cas.musicplayer.data.remote.mappers.YTBVideoToTrack
import com.cas.musicplayer.data.remote.mappers.toListMapper
import com.mousiki.shared.data.models.TrackDto
import com.mousiki.shared.data.models.tracks
import com.mousiki.shared.data.models.toDomainModel
import com.cas.musicplayer.data.remote.retrofit.MousikiSearchApi
import com.cas.musicplayer.data.remote.retrofit.RetrofitRunner
import com.cas.musicplayer.data.remote.retrofit.YoutubeService
import com.cas.musicplayer.data.repositories.ChartsRepository
import com.mousiki.shared.data.repository.GenresRepository
import com.mousiki.shared.domain.models.MusicTrack
import com.cas.musicplayer.utils.Utils
import com.cas.musicplayer.utils.bgContext
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.json.JSONArray
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-24.
 ***************************************
 */
class PlaylistSongsRemoteDataSource @Inject constructor(
    private var youtubeService: YoutubeService,
    private val mousikiSearchApi: MousikiSearchApi,
    private val retrofitRunner: RetrofitRunner,
    private val videoIdMapper: YTBPlaylistItemToVideoId,
    private val trackMapper: YTBVideoToTrack,
    private val json: Json,
    private val playlistTrackMapper: YTBPlaylistItemToTrack,
    private var appConfig: RemoteAppConfig,
    private val chartsRepository: ChartsRepository,
    private val genresRepository: GenresRepository,
    private val appContext: Context,
    private val connectivityState: ConnectivityState,
    private val storage: FirebaseStorage
) {

    suspend fun getPlaylistSongs(playlistId: String): Result<List<MusicTrack>> {
        val resultFromApi = retrofitRunner.loadWithRetry(appConfig.playlistApiConfig()) { apiUrl ->
            mousikiSearchApi.getPlaylistDetail(apiUrl, playlistId).tracks()
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
            val firebaseTracks = withContext(bgContext) {
                val directoryName = if (isChart) STORAGE_CHARTS_DIR else STORAGE_GENRES_DIR
                val file = downloadPlaylistFile(playlistId, directoryName)
                val tracks = mutableListOf<MusicTrack>()
                if (file.exists()) {
                    try {
                        val fileContent = Utils.fileContent(file)
                        val songsJsonArray = JSONArray(fileContent).toString()
                        val trackDtos: List<TrackDto> = json.decodeFromString(songsJsonArray)
                        val musicTracks = trackDtos.map { it.toDomainModel() }
                        tracks.addAll(musicTracks)
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                    }
                }
                tracks
            }

            if (firebaseTracks.isNotEmpty()) {
                return Result.Success(firebaseTracks)
            }
        }

        // 1 - Get video ids
        val idsResult = retrofitRunner.executeNetworkCall(videoIdMapper.toListMapper()) {
            youtubeService.playlistVideoIds(playlistId, 50).items ?: emptyList()
        } as? Result.Success ?: return NO_RESULT

        // 2 - Get videos
        val ids = idsResult.data.joinToString { it.id }
        return retrofitRunner.executeNetworkCall(trackMapper.toListMapper()) {
            youtubeService.videos(ids).items!!
        }
    }

    private suspend fun downloadPlaylistFile(playlistId: String, directoryName: String): File {
        val fileName = "$playlistId.json"
        val fileDirPath =
            appContext.filesDir.absolutePath + File.separator + directoryName + File.separator
        val directory = File(fileDirPath)
        if (!directory.exists()) directory.mkdirs()
        val localFile = File(fileDirPath, fileName)
        if (!localFile.exists()) {
            val connectedBeforeCall = connectivityState.isConnected()
            var retryCount = 0
            var fileDownloaded = false
            var fileExist = true
            while (retryCount < MAX_RETRY_FIREBASE_STORAGE && !fileDownloaded && fileExist) {
                retryCount++
                fileDownloaded = suspendCoroutine { continuation ->
                    val ref =
                        storage.getReferenceFromUrl("$BASE_URL_STORAGE$directoryName/$fileName")
                    ref.getFile(localFile).addOnSuccessListener {
                        continuation.resume(true)
                    }.addOnFailureListener {
                        if ((it as? StorageException)?.errorCode == StorageException.ERROR_OBJECT_NOT_FOUND) {
                            fileExist = false
                        }
                        continuation.resume(false)
                    }
                }
            }
            if (!fileDownloaded) {
                // Log error
                FirebaseCrashlytics.getInstance().log(
                    "Cannot load $playlistId songs file from firebase after $retryCount retries," +
                            "\n Is Connected before call: $connectedBeforeCall" +
                            "\n Is Connected after call:${connectivityState.isConnected()}"
                )
            }
        }
        return localFile
    }

    suspend fun getPlaylistLightSongs(playlistId: String): Result<List<MusicTrack>> {
        return retrofitRunner.executeNetworkCall(playlistTrackMapper.toListMapper()) {
            youtubeService.playlistLightTracks(playlistId, 3).items ?: emptyList()
        }
    }

    companion object {
        private const val BASE_URL_STORAGE = "gs://mousiki-e3e22.appspot.com/"
        private const val STORAGE_CHARTS_DIR = "charts"
        private const val STORAGE_GENRES_DIR = "genres"
        private const val MAX_RETRY_FIREBASE_STORAGE = 4
    }
}