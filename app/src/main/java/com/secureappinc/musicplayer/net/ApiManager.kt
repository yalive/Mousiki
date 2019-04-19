/*
 * Created by A.Yabahddou on 05/02/2019
 * Copyright (c) Marsa Maroc 2019 . All rights reserved.
 */

package com.secureappinc.musicplayer.net

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.readystatesoftware.chuck.ChuckInterceptor
import com.secureappinc.musicplayer.MusicApp
import com.secureappinc.musicplayer.utils.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import java.util.concurrent.TimeUnit

object ApiManager {

    val DEBUG_NETWORK = true

    lateinit var api: YoutubeApi
        private set

    lateinit var gson: Gson
        private set

    init {
        initHttpClient()
    }

    /**
     * Initialize Http client
     */
    private fun initHttpClient() {
        gson = GsonBuilder()
            .setLenient()
            .create()

        val client = OkHttpClient.Builder()
            .connectTimeout((5 * 60).toLong(), TimeUnit.SECONDS)
            .writeTimeout((5 * 60).toLong(), TimeUnit.SECONDS)
            .readTimeout((5 * 60).toLong(), TimeUnit.SECONDS)

        if (DEBUG_NETWORK) {
            client.addInterceptor(ChuckInterceptor(MusicApp.get()))
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client.build())
            .build()

        api = retrofit.create(YoutubeApi::class.java)
    }
}


