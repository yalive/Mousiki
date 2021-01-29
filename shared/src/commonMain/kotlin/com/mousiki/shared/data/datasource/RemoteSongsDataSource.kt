package com.mousiki.shared.data.datasource

import com.mousiki.shared.data.models.TrackDto
import com.mousiki.shared.data.models.toDomainModel
import com.mousiki.shared.data.remote.api.MousikiApi
import com.mousiki.shared.data.remote.mapper.YTBVideoToTrack
import com.mousiki.shared.data.remote.mapper.toListMapper
import com.mousiki.shared.data.remote.runner.NetworkRunner
import com.mousiki.shared.domain.models.MusicTrack
import com.mousiki.shared.domain.result.Result
import com.mousiki.shared.fs.ContentEncoding
import com.mousiki.shared.fs.FileSystem
import com.mousiki.shared.fs.PathComponent
import com.mousiki.shared.preference.PreferencesHelper
import com.mousiki.shared.utils.AnalyticsApi
import com.mousiki.shared.utils.ConnectivityChecker
import com.mousiki.shared.utils.StorageApi
import com.mousiki.shared.utils.getCurrentLocale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-20.
 ***************************************
 */
class RemoteSongsDataSource(
    private val mousikiApi: MousikiApi,
    private val networkRunner: NetworkRunner,
    private val trackMapper: YTBVideoToTrack,
    private val preferences: PreferencesHelper,
    private val json: Json,
    private val analytics: AnalyticsApi,
    private val connectivityState: ConnectivityChecker,
    private val storage: StorageApi
) {

    suspend fun getTrendingSongs(max: Int): Result<List<MusicTrack>> {
        val firebaseTracks = downloadTrendingFile().musicTracks(json)
        if (firebaseTracks.isNotEmpty()) return Result.Success(firebaseTracks)
        if (getCurrentLocale().toLowerCase() == "mx") {
            analytics.logEvent(ANALYTICS_KEY_MX_CANNOT_LOAD_TRENDING)
        }
        return networkRunner.executeNetworkCall(trackMapper.toListMapper()) {
            val resource = mousikiApi.trending(
                max,
                getCurrentLocale(),
                preferences.mostPopularNextPageToken()
            )
            val nextPageToken = resource.nextPageToken.orEmpty()
            preferences.setMostPopularNextPageToken(nextPageToken)
            resource.items ?: emptyList()
        }
    }

    private suspend fun downloadTrendingFile(): PathComponent {
        val localFile = trendingLocalFile()
        return storage.downloadFile(
            remoteUrl = "$BASE_URL_STORAGE$STORAGE_TRENDING_DIR/${FileSystem.stat(localFile)?.name}",
            path = localFile,
            connectivityState = connectivityState,
            logErrorMessage = "Cannot load ${getCurrentLocale()} trending songs file from firebase"
        )
    }

    fun deleteLocalTrendingFile() {
        FileSystem.unlink(trendingLocalFile())
    }

    private fun trendingLocalFile(): PathComponent {
        val trendingHome = FileSystem.contentsDirectory
            .absolutePath
            ?.byAppending("/$STORAGE_TRENDING_DIR/")!!
        if (!FileSystem.exists(trendingHome)) FileSystem.mkdir(trendingHome, true)
        val trendingFileName = "${getCurrentLocale().toLowerCase()}.json"
        return trendingHome.byAppending(trendingFileName)!!
    }

    companion object {
        private const val BASE_URL_STORAGE = "gs://mousiki-e3e22.appspot.com/"
        private const val STORAGE_TRENDING_DIR = "trending"
    }
}

private val ANALYTICS_KEY_MX_CANNOT_LOAD_TRENDING = "mexico_cannot_load_trending"

suspend fun PathComponent.musicTracks(json: Json): List<MusicTrack> =
    withContext(Dispatchers.Default) {
        val absolutePath = component ?: return@withContext emptyList()
        return@withContext if (FileSystem.exists(absolutePath)) {
            val tracksFromFile: List<MusicTrack> = try {
                val fileContent = FileSystem.readFile(absolutePath, ContentEncoding.Utf8).orEmpty()
                val trackDtos: List<TrackDto> = json.decodeFromString(fileContent)
                trackDtos.map { it.toDomainModel() }
            } catch (e: Exception) {
                //FirebaseCrashlytics.getInstance().recordException(e)
                emptyList()
            }
            tracksFromFile
        } else emptyList()
    }