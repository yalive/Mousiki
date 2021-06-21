package com.mousiki.shared.data.remote.api

import com.mousiki.shared.data.models.*
import com.mousiki.shared.preference.PreferencesHelper
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*

internal class YoutubeApiImpl(
    private val client: HttpClient,
    private val preferences: PreferencesHelper
) : YoutubeApi {

    override suspend fun suggestions(url: String): String {
        return client.get(url)
    }

    override suspend fun videos(ids: String): Videos {
        return ytbCall {
            client.get(YoutubeApi.VIDEOS) {
                parameter("id", ids)
                parameter(QUERY_KEY, preferences.getCurrentYtbApiKey())
            }
        }
    }

    override suspend fun channels(ids: String): Channels {
        return ytbCall {
            client.get(YoutubeApi.CHANNELS) {
                parameter("id", ids)
                parameter(QUERY_KEY, preferences.getCurrentYtbApiKey())
            }
        }
    }

    override suspend fun channelVideoIds(channelId: String): SearchResults {
        val url = YoutubeApi.BASE_URL +
                "search?part=snippet&maxResults=50&videoCategory=10&type=video&fields=items(id%2FvideoId)"
        return ytbCall {
            client.get(url) {
                parameter("channelId", channelId)
                parameter(QUERY_KEY, preferences.getCurrentYtbApiKey())
            }
        }
    }

    override suspend fun playlistVideoIds(playlistId: String, maxResults: Int): PlaylistItems {
        val url = YoutubeApi.BASE_URL +
                "playlistItems?part=contentDetails&fields=items(contentDetails%2FvideoId)"
        return ytbCall {
            client.get(url) {
                parameter("playlistId", playlistId)
                parameter("maxResults", maxResults)
                parameter(QUERY_KEY, preferences.getCurrentYtbApiKey())
            }
        }
    }

    override suspend fun searchVideoIdsByQuery(query: String, maxResults: Int): SearchResults {
        val url = YoutubeApi.BASE_URL +
                "search?part=snippet&videoCategory=10&videoCategoryId=10&type=video&fields=items(id%2FvideoId)"
        return ytbCall {
            client.get(url) {
                parameter("q", query)
                parameter("maxResults", maxResults)
                parameter(QUERY_KEY, preferences.getCurrentYtbApiKey())
            }
        }
    }

    override suspend fun searchItemIdsByQuery(
        query: String,
        type: String,
        maxResults: Int
    ): SearchResults {
        val url = YoutubeApi.BASE_URL +
                "search?part=snippet&videoCategory=10&fields=items(id%2FchannelId%2Cid%2FplaylistId)"
        return ytbCall {
            client.get(url) {
                parameter("q", query)
                parameter("type", type)
                parameter("maxResults", maxResults)
                parameter(QUERY_KEY, preferences.getCurrentYtbApiKey())
            }
        }
    }

    override suspend fun channelPlaylists(channelId: String): Playlists {
        return ytbCall {
            client.get(YoutubeApi.PLAYLISTS) {
                parameter("channelId", channelId)
                parameter(QUERY_KEY, preferences.getCurrentYtbApiKey())
            }
        }
    }

    override suspend fun playlists(channelId: String): Playlists {
        return ytbCall {
            client.get(YoutubeApi.PLAYLISTS) {
                parameter("id", channelId)
                parameter(QUERY_KEY, preferences.getCurrentYtbApiKey())
            }
        }
    }

    override suspend fun trending(maxResults: Int, regionCode: String, pageToken: String): Videos {
        return ytbCall {
            client.get(YoutubeApi.TRENDING) {
                parameter("maxResults", maxResults)
                parameter("regionCode", regionCode)
                if (pageToken.isNotEmpty()) {
                    parameter("pageToken", pageToken)
                }
                parameter(QUERY_KEY, preferences.getCurrentYtbApiKey())
            }
        }
    }

    // TODO: May be we can use inline to improve performance!!
    private suspend fun <T> ytbCall(
        apiCall: suspend () -> T
    ): T {
        var callingKey = preferences.getCurrentYtbApiKey()
        var retryCount = 0
        var response: T? = null
        var exception: Exception?

        do {
            try {
                retryCount++
                response = apiCall()
                exception = null
            } catch (e: Exception) {
                exception = e
            }

            if (exception != null && exception.keyDown) {
                // Change current key if not changed by another call
                if (callingKey == preferences.getCurrentYtbApiKey()) {
                    val keys = preferences.getYtbApiKeys()
                    val nextKey = keys.getOrNull(keys.indexOf(callingKey) + 1)
                        ?: keys.firstOrNull().orEmpty()
                    if (nextKey.isNotEmpty()) preferences.setCurrentYtbApiKey(nextKey)
                }
                callingKey = preferences.getCurrentYtbApiKey()
            }
        } while (exception != null && exception.keyDown && retryCount < MAX_RETRIES)
        return response
            ?: throw Exception("Cannot load data from ytb api after $retryCount retries with exception $exception")
    }

    private val Exception.keyDown: Boolean
        get() = this is ClientRequestException && (response.status.value == 403 || response.status.value == 400)

    companion object {
        private const val QUERY_KEY = "key"
        private const val MAX_RETRIES = 4
    }
}