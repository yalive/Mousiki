package com.secureappinc.musicplayer.net

import com.secureappinc.musicplayer.data.models.*
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

/**
 **********************************
 * Created by Abdelhadi on 4/6/19.
 **********************************
 */
interface YoutubeService {

    companion object {

        const val TRENDING_ITEMS = "&fields=items(id%2CcontentDetails%2Fduration" +
                "%2Csnippet%2FchannelId" +
                "%2Csnippet%2Ftitle" +
                "%2Csnippet%2Fthumbnails" +
                "%2Csnippet%2FchannelTitle)"

        const val PLAYLIST_ITEMS = "&fields=items(id%2CcontentDetails%2FitemCount" +
                "%2Csnippet%2Ftitle" +
                "%2Csnippet%2Fthumbnails" +
                "%2Csnippet%2FchannelTitle)"

        const val PLAYLISTS =
            "playlists?part=snippet%2CcontentDetails&maxResults=50&videoCategoryId=10$PLAYLIST_ITEMS"

        const val VIDEOS =
            "videos?part=contentDetails,snippet&videoCategoryId=10&fields=items(id%2CcontentDetails%2Fduration" +
                    "%2Csnippet%2FchannelId" +
                    "%2Csnippet%2Ftitle" +
                    "%2Csnippet%2Fthumbnails" +
                    "%2Csnippet%2FchannelTitle)"

        // Get images
        const val CHANNELS =
            "https://www.googleapis.com/youtube/v3/channels?part=snippet&fields=items(id%2Csnippet%2Fthumbnails%2Csnippet%2Ftitle)"

    }

    @GET(VIDEOS)
    suspend fun videos(
        @Query("id") ids: String
    ): Videos

    @GET(PLAYLISTS)
    suspend fun playlists(
        @Query("id") channelId: String
    ): Playlists


    @GET(CHANNELS)
    suspend fun channels(
        @Query("id") ids: String
    ): Channels

    @GET("videos?part=snippet,contentDetails&chart=mostPopular&videoCategoryId=10$TRENDING_ITEMS")
    suspend fun trending(
        @Query("maxResults") maxResults: Int,
        @Query("regionCode") regionCode: String
    ): Videos

    @GET(PLAYLISTS)
    suspend fun channelPlaylists(
        @Query("channelId") channelId: String
    ): Playlists

    @GET("search?part=snippet&maxResults=50&videoCategory=10&type=video&fields=items(id%2FvideoId)")
    suspend fun channelVideoIds(
        @Query("channelId") channelId: String
    ): SearchResults

    @GET("playlistItems?part=contentDetails&fields=items(contentDetails%2FvideoId)")
    suspend fun playlistVideoIds(
        @Query("playlistId") playlistId: String,
        @Query("maxResults") maxResults: Int
    ): PlaylistItems

    @GET("search?part=snippet&videoCategory=10&videoCategoryId=10&type=video&fields=items(id%2FvideoId)")
    suspend fun searchVideoIdsByQuery(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int
    ): SearchResults

    @GET("search?part=snippet&videoCategory=10&fields=items(id%2FchannelId%2Cid%2FplaylistId)")
    suspend fun searchItemIdsByQuery(
        @Query("q") query: String,
        @Query("type") type: String,
        @Query("maxResults") maxResults: Int
    ): SearchResults

    // Return just title
    @GET("playlistItems?part=snippet,contentDetails&fields=items(snippet%2Ftitle)")
    suspend fun playlistVideoTitles(
        @Query("playlistId") playlistId: String,
        @Query("maxResults") maxResults: Int
    ): PlaylistItems

    @GET
    suspend fun suggestions(@Url url: String): ResponseBody
}
