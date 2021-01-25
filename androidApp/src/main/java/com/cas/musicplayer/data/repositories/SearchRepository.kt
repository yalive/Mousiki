package com.cas.musicplayer.data.repositories

import com.mousiki.shared.domain.result.Result
import com.cas.musicplayer.data.datasource.search.SearchLocalDataSource
import com.cas.musicplayer.data.datasource.search.SearchRemoteDataSource
import com.cas.musicplayer.data.local.database.dao.SearchQueryDao
import com.cas.musicplayer.data.local.models.SearchQueryEntity
import com.cas.musicplayer.data.remote.retrofit.YoutubeService
import com.mousiki.shared.domain.models.Channel
import com.mousiki.shared.domain.models.Playlist
import com.mousiki.shared.domain.models.SearchTracksResult
import com.mousiki.shared.domain.result.alsoWhenSuccess
import org.json.JSONArray
import javax.inject.Inject
import javax.inject.Singleton

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-12.
 ***************************************
 */
@Singleton
class SearchRepository @Inject constructor(
    private var youtubeService: YoutubeService,
    private val searchRemoteDataSource: SearchRemoteDataSource,
    private val searchLocalDataSource: SearchLocalDataSource,
    private val searchQueryDao: SearchQueryDao
) {

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
            val responseBody = youtubeService.suggestions(url)
            val stringResponse = responseBody.string()
            if (stringResponse.startsWith("window.google.ac.h")) {
                val json =
                    stringResponse.substring(
                        stringResponse.indexOf("(") + 1,
                        stringResponse.indexOf(")")
                    )

                val jsonArray = JSONArray(json).getJSONArray(1)

                val suggestions = mutableListOf<String>()
                for (i in 0 until jsonArray.length()) {
                    suggestions.add(jsonArray.getJSONArray(i).getString(0))
                }
                return suggestions
            }
        } catch (e: Exception) {
            return emptyList()
        }
        return emptyList()
    }

    suspend fun saveSearchQuery(query: String) {
        searchQueryDao.insert(SearchQueryEntity(query))
    }

    suspend fun searchRecentQueries(query: String): List<String> {
        return searchQueryDao.search("%$query%").map { it.query }
    }
}