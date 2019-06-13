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
        const val TRENDING =
            "https://www.googleapis.com/youtube/v3/videos?part=snippet,contentDetails&chart=mostPopular&videoCategoryId=10"

        const val PLAYLIST =
            "playlists?part=snippet%2CcontentDetails&maxResults=50&videoCategoryId=10"

        const val LIST_VIDEOS_DETAIL =
            "https://www.googleapis.com/youtube/v3/videos?part=contentDetails,snippet&videoCategoryId=10&fields=items(id%2CcontentDetails%2Fduration" +
                    "%2Csnippet%2FchannelId" +
                    "%2Csnippet%2Ftitle" +
                    "%2Csnippet%2Fthumbnails" +
                    "%2Csnippet%2FchannelTitle)"

        // Get images
        const val ARTISTS_THUMBNAILS =
            "https://www.googleapis.com/youtube/v3/channels?part=snippet&fields=items(id%2Csnippet%2Fthumbnails%2Csnippet%2Ftitle)"

        const val PLAYLISTITEMS =
            "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet,contentDetails"

    }

    @GET(TRENDING)
    suspend fun trending(
        @Query("maxResults") maxResults: Int,
        @Query("regionCode") regionCode: String
    ): Videos

    @GET(PLAYLIST)
    suspend fun playlists(
        @Query("channelId") channelId: String,
        @Query("regionCode") regionCode: String
    ): Playlists

    @GET(LIST_VIDEOS_DETAIL)
    suspend fun videos(
        @Query("id") ids: String
    ): Videos

    @GET("playlistItems?part=contentDetails&fields=items(contentDetails%2FvideoId)")
    suspend fun playlistVideoIds(
        @Query("playlistId") playlistId: String,
        @Query("maxResults") maxResults: Int
    ): PlaylistItems

    @GET(ARTISTS_THUMBNAILS)
    suspend fun getArtistsImages(
        @Query("id") ids: String
    ): Channels

    @GET("search?part=snippet&maxResults=50&videoCategory=10&type=video")
    suspend fun searchVideosInChannel(
        @Query("playlistId") channelId: String
    ): SearchResults


    @GET("search?part=snippet&videoCategory=10&videoCategoryId=10&type=video")
    suspend fun searchYoutubeMusic(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int
    ): SearchResults

    @GET("search?part=snippet&videoCategory=10")
    suspend fun searchYoutube(
        @Query("q") query: String,
        @Query("type") type: String,
        @Query("maxResults") maxResults: Int
    ): SearchResults

    @GET(PLAYLIST)
    suspend fun getPlaylistsDetail(
        @Query("id") channelId: String
    ): Playlists

    @GET(PLAYLISTITEMS)
    suspend fun playlistVideos(
        @Query("playlistId") playlistId: String,
        @Query("maxResults") maxResults: Int
    ): PlaylistItems

    @GET
    suspend fun getSuggestions(@Url url: String): ResponseBody
}
