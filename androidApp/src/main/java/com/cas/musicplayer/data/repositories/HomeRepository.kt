package com.cas.musicplayer.data.repositories

import android.content.Context
import android.os.SystemClock
import com.cas.common.result.NO_RESULT
import com.cas.musicplayer.data.config.RemoteAppConfig
import com.cas.musicplayer.utils.Utils
import com.cas.musicplayer.utils.getCurrentLocale
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mousiki.shared.data.config.apiList
import com.mousiki.shared.data.config.maxApi
import com.mousiki.shared.data.config.retryCount
import com.mousiki.shared.data.models.HomeRS
import com.mousiki.shared.data.remote.api.MousikiApi
import com.mousiki.shared.data.remote.runner.NetworkRunner
import com.mousiki.shared.domain.result.Result
import com.mousiki.shared.preference.PreferencesHelper
import com.mousiki.shared.utils.getOrEmpty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject

/**
 ************************************
 * Created by Abdelhadi on 11/22/20.
 * Copyright © 2020 Mousiki
 ************************************
 */

class HomeRepository @Inject constructor(
    private val networkRunner: NetworkRunner,
    private val preferences: PreferencesHelper,
    private val appContext: Context,
    private val json: Json,
    private val appConfig: RemoteAppConfig,
    private val mousikiApi: MousikiApi
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
        return@withContext try {
            json.decodeFromString<HomeRS>(fileContent)
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun writeHomeToCache(homeRS: HomeRS) = withContext(Dispatchers.IO) {
        try {
            val cacheFile = File(cacheDirectory, CACHE_FILE_NAME)
            Utils.writeToFile(json.encodeToString(homeRS), cacheFile)
            preferences.setHomeResponseDate()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance()
                .recordException(Exception("Unable to encode home response", e))
        }
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
                result =
                    networkRunner.executeNetworkCall { mousikiApi.getHome(api, countryCode) }
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