package com.cas.musicplayer.data.remote.retrofit

import com.cas.musicplayer.data.remote.models.MousikiSearchApiRS
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 ***************************************
 * Created by Y.Abdelhadi on 5/9/20.
 ***************************************
 */
interface MousikiSearchApi {

    @GET("{url}")
    suspend fun search(
        @Path(value = "url", encoded = true) url: String,
        @Query("q") query: String
    ): MousikiSearchApiRS

    @GET("https://mousikiapp.herokuapp.com/api/{channelId}/songs")
    suspend fun searchChannel(
        @Path(value = "channelId") channelId: String
    ): MousikiSearchApiRS
}