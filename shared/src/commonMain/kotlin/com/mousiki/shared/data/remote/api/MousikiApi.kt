package com.mousiki.shared.data.remote.api

import com.mousiki.shared.data.models.HomeRS
import com.mousiki.shared.data.models.MousikiSearchApiRS

interface MousikiApi : YoutubeApi {

    suspend fun getHome(url: String, gl: String): HomeRS

    suspend fun search(
        url: String,
        query: String,
        continuationKey: String? = null,
        continuationToken: String? = null
    ): MousikiSearchApiRS

    suspend fun searchChannel(
        url: String,
        channelId: String
    ): MousikiSearchApiRS


    suspend fun getPlaylistDetail(
        url: String,
        playlistId: String
    ): MousikiSearchApiRS
}