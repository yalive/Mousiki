package com.cas.musicplayer.net

import com.cas.musicplayer.data.models.*
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
        private const val SNIPPET_TITLE = "%2Csnippet%2Ftitle"
        private const val SNIPPET_THUMBNAILS = "%2Csnippet%2Fthumbnails"
        private const val SNIPPET_CHANNEL_ID = "%2Csnippet%2FchannelId"
        private const val SNIPPET_CHANNEL_TITLE = "%2Csnippet%2FchannelTitle"
        private const val CONTENTDETAILS_DURATION = "%2CcontentDetails%2Fduration"

        const val TRENDING_ITEMS =
            "&fields=newReleaseItems(id$CONTENTDETAILS_DURATION$SNIPPET_CHANNEL_ID$SNIPPET_TITLE$SNIPPET_THUMBNAILS$SNIPPET_CHANNEL_TITLE)"

        const val PLAYLIST_ITEMS =
            "&fields=newReleaseItems(id%2CcontentDetails%2FitemCount$SNIPPET_TITLE$SNIPPET_THUMBNAILS$SNIPPET_CHANNEL_TITLE)"

        const val PLAYLISTS =
            "playlists?part=snippet%2CcontentDetails&maxResults=50&videoCategoryId=10$PLAYLIST_ITEMS"

        const val VIDEOS =
            "videos?part=contentDetails,snippet&videoCategoryId=10&fields=newReleaseItems(id$CONTENTDETAILS_DURATION$SNIPPET_CHANNEL_ID$SNIPPET_TITLE$SNIPPET_THUMBNAILS$SNIPPET_CHANNEL_TITLE)"

        // Get images
        const val CHANNELS =
            "https://www.googleapis.com/youtube/v3/channels?part=snippet&fields=newReleaseItems(id$SNIPPET_THUMBNAILS$SNIPPET_TITLE)"

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

    @GET("search?part=snippet&maxResults=50&videoCategory=10&type=video&fields=newReleaseItems(id%2FvideoId)")
    suspend fun channelVideoIds(
        @Query("channelId") channelId: String
    ): SearchResults

    @GET("playlistItems?part=contentDetails&fields=newReleaseItems(contentDetails%2FvideoId)")
    suspend fun playlistVideoIds(
        @Query("playlistId") playlistId: String,
        @Query("maxResults") maxResults: Int
    ): PlaylistItems

    @GET("search?part=snippet&videoCategory=10&videoCategoryId=10&type=video&fields=newReleaseItems(id%2FvideoId)")
    suspend fun searchVideoIdsByQuery(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int
    ): SearchResults

    @GET("search?part=snippet&videoCategory=10&fields=newReleaseItems(id%2FchannelId%2Cid%2FplaylistId)")
    suspend fun searchItemIdsByQuery(
        @Query("q") query: String,
        @Query("type") type: String,
        @Query("maxResults") maxResults: Int
    ): SearchResults

    // Return just title
    @GET("playlistItems?part=snippet,contentDetails&fields=newReleaseItems(snippet%2Ftitle)")
    suspend fun playlistVideoTitles(
        @Query("playlistId") playlistId: String,
        @Query("maxResults") maxResults: Int
    ): PlaylistItems

    @GET
    suspend fun suggestions(@Url url: String): ResponseBody
}
