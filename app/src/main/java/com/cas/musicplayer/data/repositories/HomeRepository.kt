package com.cas.musicplayer.data.repositories

import android.content.Context
import android.os.SystemClock
import com.cas.common.result.NO_RESULT
import com.cas.common.result.Result
import com.cas.musicplayer.data.config.RemoteAppConfig
import com.cas.musicplayer.data.config.apiList
import com.cas.musicplayer.data.config.maxApi
import com.cas.musicplayer.data.config.retryCount
import com.cas.musicplayer.data.datasource.search.getOrEmpty
import com.cas.musicplayer.data.preferences.PreferencesHelper
import com.cas.musicplayer.data.remote.models.mousiki.HomeRS
import com.cas.musicplayer.data.remote.retrofit.MousikiSearchApi
import com.cas.musicplayer.data.remote.retrofit.RetrofitRunner
import com.cas.musicplayer.utils.Utils
import com.cas.musicplayer.utils.getCurrentLocale
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

/**
 ************************************
 * Created by Abdelhadi on 11/22/20.
 * Copyright © 2020 Mousiki
 ************************************
 */

class HomeRepository @Inject constructor(
    private val retrofitRunner: RetrofitRunner,
    private val mousikiApi: MousikiSearchApi,
    private val preferences: PreferencesHelper,
    private val appContext: Context,
    private val gson: Gson,
    private val appConfig: RemoteAppConfig
) {

    private val cacheDirectory: File by lazy {
        val directory = File(
            appContext.filesDir.absolutePath +
                    File.separator + "home" + File.separator
        )
        if (!directory.exists()) directory.mkdirs()
        directory
    }

    private val countryCode: String by lazy {
        getCurrentLocale().toUpperCase()
    }

    suspend fun getHome(): Result<HomeRS> {
        // 1 - get cached home if any
        val cachedHome = getCachedHome()

        // 2 - if there is cache and not expired ==> return it
        if (cachedHome != null && !homeExpired()) {
            return Result.Success(cachedHome)
        }

        // 3 - try to get home from api
        val remoteHome = loadHomeFromApi()

        // 4 - if remote success ==> cache it and return it
        if (remoteHome is Result.Success) {
            val homeRs = remoteHome.data
            writeHomeToCache(homeRs)
            return remoteHome
        }

        // 5 - if remote error ==> return cached or error
        return if (cachedHome != null) Result.Success(cachedHome) else remoteHome
    }

    private suspend fun getCachedHome(): HomeRS? = withContext(Dispatchers.IO) {
        val cacheFile = File(cacheDirectory, CACHE_FILE_NAME)
        val fileContent = Utils.fileContent(cacheFile)
        return@withContext gson.fromJson(fileContent, HomeRS::class.java)
    }

    private suspend fun writeHomeToCache(homeRS: HomeRS) = withContext(Dispatchers.IO) {
        val cacheFile = File(cacheDirectory, CACHE_FILE_NAME)
        Utils.writeToFile(gson.toJson(homeRS), cacheFile)
        preferences.setHomeResponseDate()
    }

    private fun homeExpired(): Boolean {
        val updateDate = preferences.getHomeResponseDate()
        val cacheDuration = (SystemClock.elapsedRealtime() - updateDate) / 1000
        return cacheDuration - appConfig.getHomeCacheDuration() * 60 * 60 >= 0
    }

    private suspend fun loadHomeFromApi(): Result<HomeRS> {
        val config = appConfig.homeApiConfig()
        val apiList = config.apiList()
        val maxApis = config.maxApi()

        var retries = 0
        var result: Result<HomeRS> = NO_RESULT
        do {
            retries++
            var apiIndex = 0
            var api = apiList.getOrEmpty(apiIndex)
            while (api.isNotEmpty() && !result.valid()) {
                result = retrofitRunner.executeNetworkCall { mousikiApi.getHome(api, countryCode) }
                apiIndex++
                api = if (apiIndex < maxApis) apiList.getOrEmpty(apiIndex) else ""
            }
        } while (!result.valid() && retries < config.retryCount())
        return result
    }

    private fun Result<HomeRS>.valid(): Boolean {
        if (this is Result.Success) {
            return data.compactPlaylists.isNotEmpty()
                    || data.simplePlaylists.isNotEmpty()
                    || data.videoLists.isNotEmpty()
        }
        return false
    }

    companion object {
        private const val CACHE_FILE_NAME = "home.json"
    }
}

fun formatDuration(totalSecs: Int): String {
    val hours = totalSecs / 3600;
    val minutes = (totalSecs % 3600) / 60;
    val seconds = totalSecs % 60;

    if (hours == 0) {
        return String.format("%02dmin:%02ds", minutes, seconds);
    }
    return String.format("%02dh:%02dmin:%02ds", hours, minutes, seconds);
}