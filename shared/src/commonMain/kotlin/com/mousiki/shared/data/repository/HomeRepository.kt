package com.mousiki.shared.data.repository

import com.mousiki.shared.data.config.RemoteAppConfig
import com.mousiki.shared.data.config.apiList
import com.mousiki.shared.data.config.maxApi
import com.mousiki.shared.data.config.retryCount
import com.mousiki.shared.data.models.AiSearchRequest
import com.mousiki.shared.data.models.Artist
import com.mousiki.shared.data.models.HomeRS
import com.mousiki.shared.data.models.SearchQuery
import com.mousiki.shared.data.models.SearchResponse
import com.mousiki.shared.data.models.SongsResponse
import com.mousiki.shared.data.models.UdioUsersResponse
import com.mousiki.shared.data.remote.api.MousikiApi
import com.mousiki.shared.data.remote.api.UdioApi
import com.mousiki.shared.data.remote.runner.NetworkRunner
import com.mousiki.shared.domain.result.NO_RESULT
import com.mousiki.shared.domain.result.Result
import com.mousiki.shared.domain.result.map
import com.mousiki.shared.fs.ContentEncoding
import com.mousiki.shared.fs.FileSystem
import com.mousiki.shared.fs.PathComponent
import com.mousiki.shared.fs.exists
import com.mousiki.shared.preference.PreferencesHelper
import com.mousiki.shared.utils.TextResource
import com.mousiki.shared.utils.elapsedRealtime
import com.mousiki.shared.utils.getCurrentLocale
import com.mousiki.shared.utils.getOrEmpty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 ************************************
 * Created by Abdelhadi on 11/22/20.
 * Copyright © 2020 Mousiki
 ************************************
 */

class HomeRepository(
    private val networkRunner: NetworkRunner,
    private val preferences: PreferencesHelper,
    private val json: Json,
    private val appConfig: RemoteAppConfig,
    private val mousikiApi: MousikiApi
) {

    private suspend fun get(playlistId: String): Result<String> {
        return networkRunner.executeNetworkCall {
            mousikiApi.playlistSongsIds(UdioApi.PLAYLISTS, playlistId)
        }.map {
            it.playlists?.firstOrNull()?.songList?.joinToString(",").orEmpty()
        }

    }

    suspend fun getSongs(playlistId: String): Result<SongsResponse> {
        return when (val result = get(playlistId)) {
            is Result.Error -> {
                result
            }

            is Result.Success -> {
                networkRunner.executeNetworkCall {
                    mousikiApi.getSongs(result.data)
                }
            }
        }
    }

    suspend fun getTrending(): Result<SearchResponse> {
        val searchRequest = AiSearchRequest(
            searchQuery = SearchQuery(sort = "cache_trending_score"),
            pageSize = 24,
            pageParam = 0
        )
        return try {
            networkRunner.executeNetworkCall { mousikiApi.getSongsFromQuery(searchRequest) }
        } catch (e: Exception) {
            Result.Error(TextResource.fromText(e.toString()))
        }
    }

    suspend fun getUsers(ids: List<String>): Result<UdioUsersResponse> {
        val request = AiSearchRequest(
            pageSize = 24,
            pageParam = 0,
            userIds = ids
        )
        return try {
            networkRunner.executeNetworkCall { mousikiApi.getUsers(request) }
        } catch (e: Exception) {
            Result.Error(TextResource.fromText(e.toString()))
        }
    }

    suspend fun getUserSongs(artist: Artist) : Result<SearchResponse> {
        val searchRequest = AiSearchRequest(
            searchQuery = SearchQuery(userId = artist.channelId),
            pageSize = 24,
            pageParam = 0
        )
        return try {
            networkRunner.executeNetworkCall { mousikiApi.getSongsFromQuery(searchRequest) }
        } catch (e: Exception) {
            Result.Error(TextResource.fromText(e.toString()))
        }
    }

    suspend fun getSearchSongs(query: String) : Result<SearchResponse> {
        val searchRequest = AiSearchRequest(
            searchQuery = SearchQuery(
                searchTerm = query
            ),
            pageSize = 24,
            pageParam = 0
        )
        return try {
            networkRunner.executeNetworkCall { mousikiApi.getSongsFromQuery(searchRequest) }
        } catch (e: Exception) {
            Result.Error(TextResource.fromText(e.toString()))
        }
    }

    private val homeCachePath: PathComponent by lazy {
        val homeCache = FileSystem.contentsDirectory
            .absolutePath
            ?.byAppending("/home/")!!
        if (!homeCache.exists()) FileSystem.mkdir(homeCache, true)

        homeCache.byAppending(CACHE_FILE_NAME)!!
    }

    private val countryCode: String by lazy {
        getCurrentLocale().toUpperCase()
    }

    suspend fun getHome(): Result<HomeRS> {
        // 1 - get cached home if any
        val cachedHome = getCachedHome()

        // 2 - if there is cache and not expired ==> return it
        if (cachedHome != null && !homeExpired()) {
            return Result.Success(cachedHome)
        }

        // 3 - try to get home from api
        val remoteHome = loadHomeFromApi()

        // 4 - if remote success ==> cache it and return it
        if (remoteHome is Result.Success) {
            val homeRs = remoteHome.data
            writeHomeToCache(homeRs)
            return remoteHome
        }

        // 5 - if remote error ==> return cached or error
        return if (cachedHome != null) Result.Success(cachedHome) else remoteHome
    }

    private suspend fun getCachedHome(): HomeRS? = withContext(Dispatchers.Default) {
        return@withContext try {
            val fileContent = FileSystem.readFile(homeCachePath, ContentEncoding.Utf8).orEmpty()
            json.decodeFromString<HomeRS>(fileContent)
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun writeHomeToCache(homeRS: HomeRS) = withContext(Dispatchers.Default) {
        try {
            FileSystem.writeFile(
                homeCachePath,
                json.encodeToString(homeRS),
                true,
                ContentEncoding.Utf8
            )
            preferences.setHomeResponseDate()
        } catch (e: Exception) {
            /*FirebaseCrashlytics.getInstance()
                .recordException(Exception("Unable to encode home response", e))*/
        }
    }

    private fun homeExpired(): Boolean {
        val updateDate = preferences.getHomeResponseDate()
        val cacheDuration = (elapsedRealtime - updateDate) / 1000
        return (cacheDuration - appConfig.getHomeCacheDuration() * 60 * 60) >= 0
    }

    private suspend fun loadHomeFromApi(): Result<HomeRS> {
        val config = appConfig.homeApiConfig()
        val apiList = config.apiList()
        val maxApis = config.maxApi()

        var retries = 0
        var result: Result<HomeRS> = NO_RESULT
        do {
            retries++
            var apiIndex = 0
            var api = apiList.getOrEmpty(apiIndex)
            while (api.isNotEmpty() && !result.valid()) {
                result =
                    networkRunner.executeNetworkCall { mousikiApi.getHome(api, countryCode) }
                apiIndex++
                api = if (apiIndex < maxApis) apiList.getOrEmpty(apiIndex) else ""
            }
        } while (!result.valid() && retries < config.retryCount())
        return result
    }

    private fun Result<HomeRS>.valid(): Boolean {
        if (this is Result.Success) {
            return data.compactPlaylists.isNotEmpty()
                    || data.simplePlaylists.isNotEmpty()
                    || data.videoLists.isNotEmpty()
        }
        return false
    }

    companion object {
        private const val CACHE_FILE_NAME = "home.json"
    }
}