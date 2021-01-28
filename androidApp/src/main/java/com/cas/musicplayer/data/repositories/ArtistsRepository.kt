package com.cas.musicplayer.data.repositories

import android.annotation.SuppressLint
import android.content.Context
import com.cas.common.connectivity.ConnectivityState
import com.cas.musicplayer.data.datasource.ArtistsRemoteDataSource
import com.cas.musicplayer.data.datasource.channel.ChannelSongsRemoteDataSource
import com.cas.musicplayer.utils.Utils
import com.cas.musicplayer.utils.bgContext
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.storage.FirebaseStorage
import com.mousiki.shared.data.datasource.ArtistsLocalDataSource
import com.mousiki.shared.data.datasource.channel.ChannelSongsLocalDataSource
import com.mousiki.shared.data.models.Artist
import com.mousiki.shared.domain.models.MusicTrack
import com.mousiki.shared.domain.result.Result
import com.mousiki.shared.domain.result.Result.Success
import com.mousiki.shared.domain.result.alsoWhenSuccess
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
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
    private var json: Json,
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
                        val countryArtists = json.decodeFromString<List<Artist>>(jsonArray)
                        val map = countryArtists.map { it.copy(countryCode = code) }
                        artists.addAll(map)
                    }
                    val distinctBy = artists.distinctBy { it.channelId }
                    return@withContext distinctBy
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
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
                    val countryArtists =
                        json.decodeFromString<List<Artist>>(artistsJsonArray)
                    countryArtists.map { it.copy(countryCode = countryCode) }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    emptyList<Artist>()
                }
            } else emptyList()
        }

    suspend fun getArtistTracks(artist: Artist): Result<List<MusicTrack>> {
        val localChannelSongs = channelLocalDataSource.getChannelSongs(artist.channelId)
        if (localChannelSongs.isNotEmpty()) {
            return Success(localChannelSongs)
        }
        return channelRemoteDataSource.getChannelSongs(artist).alsoWhenSuccess {
            channelLocalDataSource.saveChannelSongs(artist.channelId, it)
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
                FirebaseCrashlytics.getInstance().log(
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