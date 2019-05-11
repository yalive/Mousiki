package com.secureappinc.musicplayer.net

import com.secureappinc.musicplayer.models.YTCategoryMusicRS
import com.secureappinc.musicplayer.models.YTTrendingMusicRS
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

/**
 **********************************
 * Created by Abdelhadi on 4/6/19.
 **********************************
 */
interface YoutubeApi {

    companion object {
        const val TRENDING =
            "https://www.googleapis.com/youtube/v3/videos?part=snippet,contentDetails&chart=mostPopular&videoCategoryId=10"

        const val PLAYLIST =
            "playlists?part=snippet%2CcontentDetails&maxResults=50&videoCategoryId=10"

        const val DUMMY_CHANNEL_ID = "UCmhzb5pJId5QY0r6gZqMDdA"

        const val GENRE_VIDEOS_DETAIL =
            "https://www.googleapis.com/youtube/v3/videos?part=contentDetails,snippet&videoCategoryId=10"

        // Get images
        const val ARTISTS_THUMBNAILS =
            "https://www.googleapis.com/youtube/v3/channels?part=snippet&fields=items(id%2Csnippet%2Fthumbnails)"

        const val PLAYLISTITEMS =
            "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet,contentDetails"

    }

    @GET(TRENDING)
    fun getTrending(@Query("maxResults") maxResults: Int, @Query("regionCode") regionCode: String): Call<YTTrendingMusicRS>

    @GET("search?part=snippet&chart=mostPopular&maxResults=50&videoCategory=10")
    fun getCategoryMusic(@Query("topicId") topicId: String, @Query("regionCode") regionCode: String): Call<YTCategoryMusicRS>

    @GET(PLAYLIST)
    fun getPlaylist(@Query("channelId") channelId: String, @Query("regionCode") regionCode: String): Call<YTTrendingMusicRS>

    @GET(GENRE_VIDEOS_DETAIL)
    fun getCategoryMusicDetail(@Query("id") ids: String): Call<YTTrendingMusicRS>

    @GET(ARTISTS_THUMBNAILS)
    fun getArtistsImages(@Query("id") ids: String): Call<YTTrendingMusicRS>


    @GET("search?part=snippet&maxResults=50&videoCategory=10&type=video")
    fun getArtistTracks(@Query("channelId") channelId: String): Call<YTCategoryMusicRS>

    @GET(PLAYLISTITEMS)
    fun getPlaylistVideos(@Query("playlistId") playlistId: String, @Query("maxResults") maxResults: String): Call<YTTrendingMusicRS>

    @GET("search?part=snippet&videoCategory=10")
    fun searchYoutube(@Query("q") query: String, @Query("type") type: String, @Query("maxResults") maxResults: Int): Call<YTCategoryMusicRS>

    @GET("search?part=snippet&videoCategory=10&videoCategoryId=10")
    fun searchYoutubeMusic(@Query("q") query: String, @Query("type") type: String, @Query("maxResults") maxResults: Int): Call<YTCategoryMusicRS>

    @GET(PLAYLIST)
    fun getPlaylistsDetail(@Query("id") channelId: String): Call<YTTrendingMusicRS>

    @GET
    fun getSuggestions(@Url url: String): Call<ResponseBody>

}