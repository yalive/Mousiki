package com.cas.musicplayer.data.remote.retrofit

import com.cas.musicplayer.data.remote.models.mousiki.HomeRS
import com.cas.musicplayer.data.remote.models.mousiki.MousikiSearchApiRS
import retrofit2.http.GET
import retrofit2.http.Header
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
        @Query("q") query: String,
        @Header("continuationKey") key: String? = null,
        @Header("continuationToken") token: String? = null
    ): MousikiSearchApiRS


    @GET("{url}/api/channels/{channelId}/songs")
    suspend fun searchChannel(
        @Path(value = "url", encoded = true) apiUrl: String,
        @Path(value = "channelId") channelId: String
    ): MousikiSearchApiRS

    @GET("{url}/api/playlists/{playlistId}/songs")
    suspend fun getPlaylistDetail(
        @Path(value = "url", encoded = true) apiUrl: String,
        @Path(value = "playlistId") playlistId: String
    ): MousikiSearchApiRS

    @GET("{url}/api/home?gl=US")
    suspend fun getHome(
        @Path(value = "url", encoded = true) apiUrl: String
    ): HomeRS
}