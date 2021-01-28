package com.mousiki.shared.data.remote.api

import com.mousiki.shared.data.models.*
import com.mousiki.shared.preference.PreferencesHelper
import io.ktor.client.*
import io.ktor.client.request.*

// TODO: Retry when key down
internal class YoutubeApiImpl(
    private val client: HttpClient,
    private val preferences: PreferencesHelper
) : YoutubeApi {

    override suspend fun suggestions(url: String): String {
        return client.get(url)
    }

    override suspend fun videos(ids: String): Videos {
        return client.get(YoutubeApi.VIDEOS) {
            parameter("id", ids)
            parameter(QUERY_KEY, preferences.getCurrentYtbApiKey())
        }
    }

    override suspend fun channels(ids: String): Channels {
        return client.get(YoutubeApi.CHANNELS) {
            parameter("id", ids)
            parameter(QUERY_KEY, preferences.getCurrentYtbApiKey())
        }
    }

    override suspend fun channelVideoIds(channelId: String): SearchResults {
        val url = YoutubeApi.BASE_URL +
                "search?part=snippet&maxResults=50&videoCategory=10&type=video&fields=items(id%2FvideoId)"
        return client.get(url) {
            parameter("channelId", channelId)
            parameter(QUERY_KEY, preferences.getCurrentYtbApiKey())
        }
    }

    override suspend fun playlistVideoIds(playlistId: String, maxResults: Int): PlaylistItems {
        val url = YoutubeApi.BASE_URL +
                "playlistItems?part=contentDetails&fields=items(contentDetails%2FvideoId)"
        return client.get(url) {
            parameter("playlistId", playlistId)
            parameter("maxResults", maxResults)
            parameter(QUERY_KEY, preferences.getCurrentYtbApiKey())
        }
    }

    override suspend fun searchVideoIdsByQuery(query: String, maxResults: Int): SearchResults {
        val url = YoutubeApi.BASE_URL +
                "search?part=snippet&videoCategory=10&videoCategoryId=10&type=video&fields=items(id%2FvideoId)"
        return client.get(url) {
            parameter("q", query)
            parameter("maxResults", maxResults)
            parameter(QUERY_KEY, preferences.getCurrentYtbApiKey())
        }
    }

    override suspend fun searchItemIdsByQuery(
        query: String,
        type: String,
        maxResults: Int
    ): SearchResults {
        val url = YoutubeApi.BASE_URL +
                "search?part=snippet&videoCategory=10&fields=items(id%2FchannelId%2Cid%2FplaylistId)"
        return client.get(url) {
            parameter("q", query)
            parameter("type", type)
            parameter("maxResults", maxResults)
            parameter(QUERY_KEY, preferences.getCurrentYtbApiKey())
        }
    }

    override suspend fun channelPlaylists(channelId: String): Playlists {
        return client.get(YoutubeApi.PLAYLISTS) {
            parameter("channelId", channelId)
            parameter(QUERY_KEY, preferences.getCurrentYtbApiKey())
        }
    }

    override suspend fun playlists(channelId: String): Playlists {
        return client.get(YoutubeApi.PLAYLISTS) {
            parameter("id", channelId)
            parameter(QUERY_KEY, preferences.getCurrentYtbApiKey())
        }
    }

    override suspend fun trending(maxResults: Int, regionCode: String, pageToken: String): Videos {
        return client.get(YoutubeApi.TRENDING) {
            parameter("maxResults", maxResults)
            parameter("regionCode", regionCode)
            parameter("pageToken", pageToken)
            parameter(QUERY_KEY, preferences.getCurrentYtbApiKey())
        }
    }

    companion object {
        private const val QUERY_KEY = "key"
    }
}