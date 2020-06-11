package com.cas.musicplayer.data.remote.retrofit

import com.cas.musicplayer.data.remote.models.mousiki.MousikiPlaylistRS
import com.cas.musicplayer.data.remote.models.mousiki.MousikiSearchApiRS
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 ***************************************
 * Created by Y.Abdelhadi on 5/9/20.
 ***************************************
 */
interface MousikiSearchApi {

    @GET("{url}/api/search")
    suspend fun search(
        @Path(value = "url", encoded = true) url: String,
        @Query("q") query: String
    ): MousikiSearchApiRS

    @GET("{url}/api/{channelId}/songs")
    suspend fun searchChannel(
        @Path(value = "url", encoded = true) apiUrl: String,
        @Path(value = "channelId") channelId: String
    ): MousikiSearchApiRS

    @GET("{url}/api/playlistSongs")
    suspend fun getPlaylistDetail(
        @Path(value = "url", encoded = true) apiUrl: String,
        @Query(value = "id") playlistId: String
    ): MousikiPlaylistRS
}