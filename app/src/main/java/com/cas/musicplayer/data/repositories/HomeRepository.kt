package com.cas.musicplayer.data.repositories

import com.cas.common.result.Result
import com.cas.musicplayer.data.remote.models.mousiki.HomeRS
import com.cas.musicplayer.data.remote.retrofit.MousikiSearchApi
import com.cas.musicplayer.data.remote.retrofit.RetrofitRunner
import javax.inject.Inject

/**
 ************************************
 * Created by Abdelhadi on 11/22/20.
 * Copyright © 2020 Mousiki
 ************************************
 */
class HomeRepository @Inject constructor(
    private val retrofitRunner: RetrofitRunner,
    private val mousikiApi: MousikiSearchApi
) {

    suspend fun getHome(): Result<HomeRS> {
        return retrofitRunner.executeNetworkCall {
            mousikiApi.getHome("https://ktor-demo.herokuapp.com")
        }
    }
}