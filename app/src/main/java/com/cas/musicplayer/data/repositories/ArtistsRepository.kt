package com.cas.musicplayer.data.repositories

import android.annotation.SuppressLint
import android.content.Context
import com.cas.common.connectivity.ConnectivityState
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
import com.crashlytics.android.Crashlytics
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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
    private val channelRemoteDataSource: ChannelSongsRemoteDataSource,
    private val storage: FirebaseStorage,
    private val appContext: Context,
    private val connectivityState: ConnectivityState
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

    suspend fun getAllArtists(): List<Artist> =
        withContext(bgContext) {
            val localFile = downloadArtistsFile()
            if (localFile.exists()) {
                try {
                    val fileContent = Utils.fileContent(localFile)
                    val artistsObject = JSONObject(fileContent)
                    val artists = mutableListOf<Artist>()
                    artistsObject.keys().forEach { code ->
                        val jsonArray = artistsObject.getJSONArray(code).toString()
                        val typeTokenArtists = object : TypeToken<List<Artist>>() {}.type
                        val countryArtists =
                            gson.fromJson<List<Artist>>(jsonArray, typeTokenArtists)
                        artists.addAll(countryArtists)
                    }
                    val distinctBy = artists.distinctBy { it.channelId }
                    return@withContext distinctBy
                } catch (e: Exception) {
                    Crashlytics.logException(e)
                }
            }
            return@withContext emptyList<Artist>()
        }


    @SuppressLint("DefaultLocale")
    suspend fun getArtistsByCountry(countryCode: String): List<Artist> =
        withContext(bgContext) {
            val localFile = downloadArtistsFile()
            if (localFile.exists()) {
                try {
                    val fileContent = Utils.fileContent(localFile)
                    val artistsJsonArray = JSONObject(fileContent)
                        .getJSONArray(countryCode.toUpperCase()).toString()
                    val typeTokenArtists = object : TypeToken<List<Artist>>() {}.type
                    gson.fromJson<List<Artist>>(artistsJsonArray, typeTokenArtists)
                } catch (e: Exception) {
                    Crashlytics.logException(e)
                    emptyList<Artist>()
                }
            } else emptyList()
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

    private suspend fun downloadArtistsFile(): File {
        val localFile = File(appContext.filesDir, LOCAL_FILE_NAME_ARTISTS)
        if (!localFile.exists()) {
            val connectedBeforeCall = connectivityState.isConnected()
            var retryCount = 0
            var fileDownloaded = false
            while (retryCount < MAX_RETRY_FIREBASE_STORAGE && !fileDownloaded) {
                retryCount++
                fileDownloaded = suspendCoroutine { continuation ->
                    val ref = storage.getReferenceFromUrl(URL_STORAGE_ARTISTS)
                    ref.getFile(localFile).addOnSuccessListener {
                        continuation.resume(true)
                    }.addOnFailureListener {
                        continuation.resume(false)
                    }
                }
            }
            if (!fileDownloaded) {
                // Log error
                Crashlytics.log(
                    "Cannot load artists file from firebase after $retryCount retries," +
                            "\n Is Connected before call: $connectedBeforeCall" +
                            "\n Is Connected after call:${connectivityState.isConnected()}"
                )
            }
        }
        return localFile
    }

    companion object {
        private const val URL_STORAGE_ARTISTS = "gs://mousiki-e3e22.appspot.com/artists.json"
        private const val LOCAL_FILE_NAME_ARTISTS = "artists.json"
        private const val MAX_RETRY_FIREBASE_STORAGE = 4
    }
}