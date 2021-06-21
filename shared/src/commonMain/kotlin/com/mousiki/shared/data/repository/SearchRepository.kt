package com.mousiki.shared.data.repository

import com.cas.musicplayer.MousikiDb
import com.mousiki.shared.data.datasource.search.SearchLocalDataSource
import com.mousiki.shared.data.datasource.search.SearchRemoteDataSource
import com.mousiki.shared.data.remote.api.MousikiApi
import com.mousiki.shared.db.Search_queries
import com.mousiki.shared.domain.models.Channel
import com.mousiki.shared.domain.models.Playlist
import com.mousiki.shared.domain.models.SearchTracksResult
import com.mousiki.shared.domain.result.Result
import com.mousiki.shared.domain.result.alsoWhenSuccess
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-12.
 ***************************************
 */
class SearchRepository(
    private val mousikiApi: MousikiApi,
    private val json: Json,
    private val searchRemoteDataSource: SearchRemoteDataSource,
    private val searchLocalDataSource: SearchLocalDataSource,
    private val db: MousikiDb
) {

    private val searchQueryDao by lazy { db.searchQueriesQueries }

    suspend fun searchTracks(
        query: String,
        key: String? = null,
        token: String? = null
    ): Result<SearchTracksResult> {

        // Check cache only if no pagination is specified
        if (key == null && token == null) {
            val localResult = searchLocalDataSource.getSearchSongsResultForQuery(query)
            if (localResult.isNotEmpty()) {
                return Result.Success(SearchTracksResult(localResult))
            }
        }

        return searchRemoteDataSource.searchTracks(query, key, token).alsoWhenSuccess {
            searchLocalDataSource.saveSongs(query, it.tracks)
        }
    }

    suspend fun searchPlaylists(query: String): Result<List<Playlist>> {
        val localResult = searchLocalDataSource.getSearchPlaylistsResultForQuery(query)
        if (localResult.isNotEmpty()) {
            return Result.Success(localResult)
        }
        return searchRemoteDataSource.searchPlaylists(query).alsoWhenSuccess {
            searchLocalDataSource.savePlaylists(query, it)
        }
    }


    suspend fun searchChannels(query: String): Result<List<Channel>> {
        val localResult = searchLocalDataSource.getSearchChannelsResultForQuery(query)
        if (localResult.isNotEmpty()) {
            return Result.Success(localResult)
        }
        return searchRemoteDataSource.searchChannels(query).alsoWhenSuccess {
            searchLocalDataSource.saveChannels(query, it)
        }
    }

    suspend fun getSuggestions(url: String): List<String> {
        try {
            val stringResponse = mousikiApi.suggestions(url)
            if (stringResponse.startsWith("window.google.ac.h")) {
                val jsonContent = stringResponse.substring(
                    stringResponse.indexOf("(") + 1,
                    stringResponse.indexOf(")")
                )

                val jsonArray = json.parseToJsonElement(jsonContent).jsonArray[1].jsonArray

                val suggestions = mutableListOf<String>()
                jsonArray.forEach { element ->
                    val suggestion = element.jsonArray
                        .firstOrNull()
                        ?.jsonPrimitive?.content.orEmpty()
                    if (suggestion.isNotEmpty()) {
                        suggestions.add(suggestion)
                    }
                }
                return suggestions
            }
        } catch (e: Exception) {
            return emptyList()
        }
        return emptyList()
    }

    suspend fun saveSearchQuery(query: String) {
        searchQueryDao.insert(Search_queries(query))
    }

    suspend fun removeSearchQuery(query: String) {
        searchQueryDao.deleteSearchQuery(query)
    }

    suspend fun searchRecentQueries(query: String): List<String> {
        return searchQueryDao.search("%$query%").executeAsList()
    }

    suspend fun clearSearchHistory() {
        searchQueryDao.deleteAll()
    }
}