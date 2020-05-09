package com.cas.musicplayer.data.remote.retrofit

import com.cas.musicplayer.data.remote.models.ScrappedResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 ***************************************
 * Created by Y.Abdelhadi on 5/9/20.
 ***************************************
 */
interface ScrapService {

    @GET("https://youtube-scrape.herokuapp.com/api/search")
    suspend fun search(
        @Query("q") query: String
    ): ScrappedResponse
}