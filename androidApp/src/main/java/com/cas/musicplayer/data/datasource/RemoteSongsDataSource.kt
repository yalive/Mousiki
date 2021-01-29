package com.cas.musicplayer.data.datasource

import android.content.Context
import com.cas.musicplayer.data.firebase.downloadFile
import com.cas.musicplayer.utils.Utils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.storage.FirebaseStorage
import com.mousiki.shared.data.models.TrackDto
import com.mousiki.shared.data.models.toDomainModel
import com.mousiki.shared.data.remote.api.MousikiApi
import com.mousiki.shared.data.remote.mapper.YTBVideoToTrack
import com.mousiki.shared.data.remote.mapper.toListMapper
import com.mousiki.shared.data.remote.runner.NetworkRunner
import com.mousiki.shared.domain.models.MusicTrack
import com.mousiki.shared.domain.result.Result
import com.mousiki.shared.preference.PreferencesHelper
import com.mousiki.shared.utils.AnalyticsApi
import com.mousiki.shared.utils.ConnectivityChecker
import com.mousiki.shared.utils.getCurrentLocale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.*
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-20.
 ***************************************
 */
class RemoteSongsDataSource @Inject constructor(
    private val mousikiApi: MousikiApi,
    private val networkRunner: NetworkRunner,
    private val trackMapper: YTBVideoToTrack,
    private val preferences: PreferencesHelper,
    private val appContext: Context,
    private val json: Json,
    private val analytics: AnalyticsApi,
    private val connectivityState: ConnectivityChecker,
    private val storage: FirebaseStorage
) {

    suspend fun getTrendingSongs(max: Int): Result<List<MusicTrack>> {
        val firebaseTracks = downloadTrendingFile().musicTracks(json)
        if (firebaseTracks.isNotEmpty()) return Result.Success(firebaseTracks)
        if (getCurrentLocale().toLowerCase(Locale.getDefault()) == "mx") {
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

    private suspend fun downloadTrendingFile(): File {
        val localFile = trendingLocalFile()
        return storage.downloadFile(
            remoteUrl = "${BASE_URL_STORAGE}$STORAGE_TRENDING_DIR/${localFile.name}",
            localFile = localFile,
            connectivityState = connectivityState,
            logErrorMessage = "Cannot load ${getCurrentLocale()} trending songs file from firebase"
        )
    }

    fun deleteLocalTrendingFile() {
        trendingLocalFile().delete()
    }

    private fun trendingLocalFile(): File {
        val countryCode = getCurrentLocale().toLowerCase()
        val fileName = "$countryCode.json"
        val fileDirPath =
            appContext.filesDir.absolutePath + File.separator + STORAGE_TRENDING_DIR + File.separator
        val directory = File(fileDirPath)
        if (!directory.exists()) directory.mkdirs()
        return File(fileDirPath, fileName)
    }

    companion object {
        private const val BASE_URL_STORAGE = "gs://mousiki-e3e22.appspot.com/"
        private const val STORAGE_TRENDING_DIR = "trending"
    }
}

private val ANALYTICS_KEY_MX_CANNOT_LOAD_TRENDING = "mexico_cannot_load_trending"

suspend fun File.musicTracks(json: Json): List<MusicTrack> = withContext(Dispatchers.Default) {
    return@withContext if (exists()) {
        val tracksFromFile: List<MusicTrack> = try {
            val fileContent = Utils.fileContent(this@musicTracks)
            val trackDtos: List<TrackDto> = json.decodeFromString(fileContent)
            trackDtos.map { it.toDomainModel() }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            emptyList()
        }
        tracksFromFile
    } else emptyList()
}