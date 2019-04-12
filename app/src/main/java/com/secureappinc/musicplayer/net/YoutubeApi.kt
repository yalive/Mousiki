package com.secureappinc.musicplayer.net

import com.secureappinc.musicplayer.models.YTCategoryMusicRS
import com.secureappinc.musicplayer.models.YTTrendingMusicRS
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
        val TRENDING =
            "https://www.googleapis.com/youtube/v3/videos?part=snippet,contentDetails&chart=mostPopular&regionCode=MA&maxResults=25&videoCategoryId=10&key=AIzaSyABJ_DecXWPIkB8R80i3pDJMcmkcnPLuwk"


        const val PLAYLIST =
            "playlists?part=snippet%2CcontentDetails&maxResults=10&key=AIzaSyABJ_DecXWPIkB8R80i3pDJMcmkcnPLuwk&videoCategoryId=10"

        const val DUMMY_CHANNEL_ID = "UCmhzb5pJId5QY0r6gZqMDdA"

        const val GENRE_VIDEOS_DETAIL =
            "https://www.googleapis.com/youtube/v3/videos?part=contentDetails,snippet&key=AIzaSyABJ_DecXWPIkB8R80i3pDJMcmkcnPLuwk"
    }

    @GET
    fun getTrending(@Url url: String): Call<YTTrendingMusicRS>

    @GET("search?part=snippet&chart=mostPopular&maxResults=25&videoCategory=10&key=AIzaSyABJ_DecXWPIkB8R80i3pDJMcmkcnPLuwk")
    fun getCategoryMusic(@Query("topicId") topicId: String, @Query("regionCode") regionCode: String): Call<YTCategoryMusicRS>

    @GET(PLAYLIST)
    fun getPlaylist(@Query("channelId") channelId: String, @Query("regionCode") regionCode: String): Call<YTTrendingMusicRS>

    @GET(GENRE_VIDEOS_DETAIL)
    fun getCategoryMusicDetail(@Query("id") ids: String): Call<YTTrendingMusicRS>
}