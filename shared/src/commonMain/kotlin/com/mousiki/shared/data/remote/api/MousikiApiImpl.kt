package com.mousiki.shared.data.remote.api

import com.mousiki.shared.data.models.*
import com.mousiki.shared.preference.PreferencesHelper
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*

class MousikiApiImpl(
    private val preferencesHelper: PreferencesHelper
) : MousikiApi {

    //private val kermit = Kermit(logger)

    private val client = HttpClient {
        install(JsonFeature) {
            val json = kotlinx.serialization.json.Json {
                ignoreUnknownKeys = true
                isLenient = true
            }
            serializer = KotlinxSerializer(json)
        }

        install(Logging) {
            this.logger = object : io.ktor.client.features.logging.Logger {
                override fun log(message: String) {
                    println("Network_mousiki: $message")
                }
            }
            level = LogLevel.ALL
        }
    }

    private val youtubeApi by lazy {
        YoutubeApiImpl(client, preferencesHelper)
    }

    override suspend fun getHome(url: String, gl: String): HomeRS {
        return client.get("$url/api/home") {
            parameter("gl", gl)
        }
    }

    override suspend fun search(
        url: String,
        query: String,
        continuationKey: String?,
        continuationToken: String?
    ): MousikiSearchApiRS {
        return client.get("$url/api/search") {
            parameter("q", query)
            headers {
                if (continuationKey != null) {
                    append("continuationKey", continuationKey)
                }

                if (continuationToken != null) {
                    append("continuationToken", continuationToken)
                }
            }
        }
    }

    override suspend fun searchChannel(url: String, channelId: String): MousikiSearchApiRS {
        return client.get("$url/api/channels/$channelId/songs")
    }

    override suspend fun getPlaylistDetail(url: String, playlistId: String): MousikiSearchApiRS {
        return client.get("$url/api/playlists/$playlistId/songs")
    }

    override suspend fun getAudioInfo(url: String, videoId: String): AudioInfoRS {
        return client.get("$url/api/videos/$videoId/audio")
    }

    override suspend fun getVideo(url: String, videoId: String): MousikiSearchApiResult {
        return client.get("$url/api/videos/$videoId")
    }

    /////    Ytb API   /////

    override suspend fun suggestions(url: String): String {
        return youtubeApi.suggestions(url)
    }

    override suspend fun videos(ids: String): Videos {
        return youtubeApi.videos(ids)
    }

    override suspend fun trending(maxResults: Int, regionCode: String, pageToken: String): Videos {
        return youtubeApi.trending(maxResults, regionCode, pageToken)
    }

    override suspend fun playlists(channelId: String): Playlists {
        return youtubeApi.playlists(channelId)
    }

    override suspend fun channels(ids: String): Channels {
        return youtubeApi.channels(ids)
    }

    override suspend fun channelPlaylists(channelId: String): Playlists {
        return youtubeApi.channelPlaylists(channelId)
    }

    override suspend fun channelVideoIds(channelId: String): SearchResults {
        return youtubeApi.channelVideoIds(channelId)
    }

    override suspend fun playlistVideoIds(playlistId: String, maxResults: Int): PlaylistItems {
        return youtubeApi.playlistVideoIds(playlistId, maxResults)
    }

    override suspend fun searchVideoIdsByQuery(query: String, maxResults: Int): SearchResults {
        return youtubeApi.searchVideoIdsByQuery(query, maxResults)
    }

    override suspend fun searchItemIdsByQuery(
        query: String,
        type: String,
        maxResults: Int
    ): SearchResults {
        return youtubeApi.searchItemIdsByQuery(query, type, maxResults)
    }
}