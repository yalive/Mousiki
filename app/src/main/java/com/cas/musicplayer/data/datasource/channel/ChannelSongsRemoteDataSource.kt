package com.cas.musicplayer.data.datasource.channel

import android.content.Context
import com.cas.common.connectivity.ConnectivityState
import com.cas.common.result.NO_RESULT
import com.cas.common.result.Result
import com.cas.musicplayer.data.datasource.musicTracks
import com.cas.musicplayer.data.remote.mappers.YTBSearchResultToVideoId
import com.cas.musicplayer.data.remote.mappers.YTBVideoToTrack
import com.cas.musicplayer.data.remote.mappers.toListMapper
import com.cas.musicplayer.data.remote.models.Artist
import com.cas.musicplayer.data.remote.retrofit.RetrofitRunner
import com.cas.musicplayer.data.remote.retrofit.YoutubeService
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.utils.bgContext
import com.cas.musicplayer.utils.getCurrentLocale
import com.crashlytics.android.Crashlytics
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.gson.Gson
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-24.
 ***************************************
 */
class ChannelSongsRemoteDataSource @Inject constructor(
    private var youtubeService: YoutubeService,
    private val retrofitRunner: RetrofitRunner,
    private val searchMapper: YTBSearchResultToVideoId,
    private val trackMapper: YTBVideoToTrack,
    private val appContext: Context,
    private val gson: Gson,
    private val connectivityState: ConnectivityState,
    private val storage: FirebaseStorage
) {

    suspend fun getChannelSongs(artist: Artist): Result<List<MusicTrack>> {
        val firebaseTracks = withContext(bgContext) {
            downloadArtistTracksFile(artist).musicTracks(gson)
        }
        if (firebaseTracks.isNotEmpty()) return Result.Success(firebaseTracks)

        // Go to youtube!
        // Get ids
        val result = retrofitRunner.executeNetworkCall(searchMapper.toListMapper()) {
            youtubeService.channelVideoIds(artist.channelId).items ?: emptyList()
        } as? Result.Success ?: return NO_RESULT

        // 2 - Get videos
        val ids = result.data.joinToString { it.id }
        return retrofitRunner.executeNetworkCall(trackMapper.toListMapper()) {
            youtubeService.videos(ids).items ?: emptyList()
        }
    }


    private suspend fun downloadArtistTracksFile(artist: Artist): File {
        val localFile = artistSongsFile(artist)
        if (!localFile.exists()) {
            val connectedBeforeCall = connectivityState.isConnected()
            var retryCount = 0
            var fileDownloaded = false
            var fileExist = true
            while (retryCount < MAX_RETRY_FIREBASE_STORAGE && !fileDownloaded && fileExist) {
                retryCount++
                fileDownloaded = suspendCoroutine { continuation ->
                    val ref =
                        storage.getReferenceFromUrl(
                            "${BASE_URL_STORAGE}${STORAGE_ARTISTS_SONGS_DIR}/${artist.countryCode.toLowerCase(
                                Locale.getDefault()
                            )}/${localFile.name}"
                        )
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
                Crashlytics.log(
                    "Cannot load ${getCurrentLocale()} trending songs file from firebase after $retryCount retries," +
                            "\n Is Connected before call: $connectedBeforeCall" +
                            "\n Is Connected after call:${connectivityState.isConnected()}"
                )
            }
        }
        return localFile
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
        private const val MAX_RETRY_FIREBASE_STORAGE = 4
    }
}