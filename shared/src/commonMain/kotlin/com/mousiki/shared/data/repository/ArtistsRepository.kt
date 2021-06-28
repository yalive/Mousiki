package com.mousiki.shared.data.repository

import com.mousiki.shared.data.datasource.ArtistsLocalDataSource
import com.mousiki.shared.data.datasource.ArtistsRemoteDataSource
import com.mousiki.shared.data.datasource.channel.ChannelSongsLocalDataSource
import com.mousiki.shared.data.datasource.channel.ChannelSongsRemoteDataSource
import com.mousiki.shared.data.models.Artist
import com.mousiki.shared.domain.models.YtbTrack
import com.mousiki.shared.domain.result.Result
import com.mousiki.shared.domain.result.Result.Success
import com.mousiki.shared.domain.result.alsoWhenSuccess
import com.mousiki.shared.fs.ContentEncoding
import com.mousiki.shared.fs.FileSystem
import com.mousiki.shared.fs.PathComponent
import com.mousiki.shared.fs.exists
import com.mousiki.shared.utils.ConnectivityChecker
import com.mousiki.shared.utils.StorageApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-10.
 ***************************************
 */

class ArtistsRepository(
    private var json: Json,
    private val localDataSource: ArtistsLocalDataSource,
    private val remoteDataSource: ArtistsRemoteDataSource,
    private val channelLocalDataSource: ChannelSongsLocalDataSource,
    private val channelRemoteDataSource: ChannelSongsRemoteDataSource,
    private val storage: StorageApi,
    private val connectivityState: ConnectivityChecker
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

    // TODO: Handle OutOfMemory error
    suspend fun getAllArtists(): List<Artist> {
        val localFile = downloadArtistsFile()
        return withContext(Dispatchers.Default) {
            if (localFile.exists()) {
                try {
                    val fileContent = FileSystem.readFile(localFile, ContentEncoding.Utf8).orEmpty()
                    val artistsObject = json.parseToJsonElement(fileContent).jsonObject
                    val artists = mutableListOf<Artist>()
                    artistsObject.forEach { entry ->
                        val jsonArray = entry.value.jsonArray.toString()
                        val countryArtists = json.decodeFromString<List<Artist>>(jsonArray)
                        val map = countryArtists.map { it.copy(countryCode = entry.key) }
                        artists.addAll(map)
                    }
                    val distinctBy = artists.distinctBy { it.channelId }
                    distinctBy
                } catch (e: Exception) {
                    //FirebaseCrashlytics.getInstance().recordException(e)
                    emptyList<Artist>()
                }
            } else emptyList<Artist>()
        }
    }


    suspend fun getArtistsByCountry(countryCode: String): List<Artist> {
        val localFile = downloadArtistsFile()
        return withContext(Dispatchers.Default) {
            if (localFile.exists()) {
                try {
                    val fileContent = FileSystem.readFile(localFile, ContentEncoding.Utf8).orEmpty()
                    val artistsJsonArray = json.parseToJsonElement(fileContent)
                        .jsonObject[countryCode.toUpperCase()]
                        ?.jsonArray.toString()
                    val countryArtists = json.decodeFromString<List<Artist>>(artistsJsonArray)
                    countryArtists.map { it.copy(countryCode = countryCode) }
                } catch (e: Exception) {
                    //FirebaseCrashlytics.getInstance().recordException(e)
                    emptyList<Artist>()
                }
            } else emptyList()
        }
    }

    suspend fun getArtistTracks(artist: Artist): Result<List<YtbTrack>> {
        val localChannelSongs = channelLocalDataSource.getChannelSongs(artist.channelId)
        if (localChannelSongs.isNotEmpty()) {
            return Success(localChannelSongs)
        }
        return channelRemoteDataSource.getChannelSongs(artist).alsoWhenSuccess {
            channelLocalDataSource.saveChannelSongs(artist.channelId, it)
        }
    }

    private suspend fun downloadArtistsFile(): PathComponent {
        val artistsPath = FileSystem.contentsDirectory
            .absolutePath!!
            .byAppending(LOCAL_FILE_NAME_ARTISTS)!!

        println("Artists path $artistsPath")
        if (artistsPath.exists()) {
            println("Artists file already downloaded")
            return artistsPath
        }
        return storage.downloadFile(
            remoteUrl = URL_STORAGE_ARTISTS,
            path = artistsPath,
            connectivityState = connectivityState,
            logErrorMessage = "Cannot load artists file from firebase"
        )
    }

    companion object {
        private const val URL_STORAGE_ARTISTS = "gs://mousiki-e3e22.appspot.com/artists.json"
        private const val LOCAL_FILE_NAME_ARTISTS = "artists.json"
    }
}