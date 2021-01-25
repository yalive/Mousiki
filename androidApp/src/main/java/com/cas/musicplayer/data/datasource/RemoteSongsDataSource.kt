package com.cas.musicplayer.data.datasource

import android.content.Context
import com.cas.common.connectivity.ConnectivityState
import com.mousiki.shared.domain.result.Result
import com.cas.musicplayer.data.preferences.PreferencesHelper
import com.cas.musicplayer.data.remote.mappers.YTBVideoToTrack
import com.cas.musicplayer.data.remote.mappers.toListMapper
import com.mousiki.shared.data.models.TrackDto
import com.mousiki.shared.data.models.toDomainModel
import com.cas.musicplayer.data.remote.retrofit.RetrofitRunner
import com.cas.musicplayer.data.remote.retrofit.YoutubeService
import com.mousiki.shared.domain.models.MusicTrack
import com.cas.musicplayer.utils.Utils
import com.cas.musicplayer.utils.bgContext
import com.cas.musicplayer.utils.getCurrentLocale
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-20.
 ***************************************
 */
class RemoteSongsDataSource @Inject constructor(
    private var youtubeService: YoutubeService,
    private val retrofitRunner: RetrofitRunner,
    private val trackMapper: YTBVideoToTrack,
    private val preferences: PreferencesHelper,
    private val appContext: Context,
    private val json: Json,
    private val analytics: FirebaseAnalytics,
    private val connectivityState: ConnectivityState,
    private val storage: FirebaseStorage
) {

    suspend fun getTrendingSongs(max: Int): Result<List<MusicTrack>> {
        val firebaseTracks = withContext(bgContext) {
            downloadTrendingFile().musicTracks(json)
        }
        if (firebaseTracks.isNotEmpty()) return Result.Success(firebaseTracks)
        if (getCurrentLocale().toLowerCase(Locale.getDefault()) == "mx") {
            analytics.logEvent(ANALYTICS_KEY_MX_CANNOT_LOAD_TRENDING, null)
        }
        return retrofitRunner.executeNetworkCall(trackMapper.toListMapper()) {
            val resource = youtubeService.trending(
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
        if (!localFile.exists()) {
            val connectedBeforeCall = connectivityState.isConnected()
            var retryCount = 0
            var fileDownloaded = false
            var fileExist = true
            while (retryCount < MAX_RETRY_FIREBASE_STORAGE && !fileDownloaded && fileExist) {
                retryCount++
                fileDownloaded = suspendCoroutine { continuation ->
                    val ref =
                        storage.getReferenceFromUrl("${BASE_URL_STORAGE}$STORAGE_TRENDING_DIR/${localFile.name}")
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
                    "Cannot load ${getCurrentLocale()} trending songs file from firebase after $retryCount retries," +
                            "\n Is Connected before call: $connectedBeforeCall" +
                            "\n Is Connected after call:${connectivityState.isConnected()}"
                )
            }
        }
        return localFile
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
        private const val MAX_RETRY_FIREBASE_STORAGE = 4
    }
}

private val ANALYTICS_KEY_MX_CANNOT_LOAD_TRENDING = "mexico_cannot_load_trending"

fun File.musicTracks(json: Json): List<MusicTrack> = if (exists()) {
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