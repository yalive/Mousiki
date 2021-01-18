package com.cas.musicplayer.data.remote.retrofit

import com.cas.common.result.AppMessage
import com.cas.common.result.NO_RESULT
import com.cas.common.result.Result
import com.cas.musicplayer.R
import com.cas.musicplayer.data.config.SearchConfig
import com.cas.musicplayer.data.config.apiList
import com.cas.musicplayer.data.config.maxApi
import com.cas.musicplayer.data.config.retryCount
import com.cas.musicplayer.data.datasource.search.getOrEmpty
import com.cas.musicplayer.data.remote.mappers.Mapper
import com.cas.musicplayer.utils.bgContext
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-10.
 ***************************************
 */
@Singleton
class RetrofitRunner @Inject constructor() {

    /**
     * Executes webservice call and map the response to an app domain model.
     * [T]: The return type of the webservice call.
     * [E]: Mapped to model
     */
    suspend fun <T, E> executeNetworkCall(
        mapper: Mapper<T, E>,
        request: suspend () -> T
    ): Result<E> = try {
        val response = request()
        val mappedResponse = withContext(bgContext) {
            mapper.map(response)
        }
        Result.Success(mappedResponse)
    } catch (e: Exception) {
        FirebaseCrashlytics.getInstance().recordException(e)
        Result.Error(AppMessage.ResourceMessage(R.string.common_technical_issue))
    }

    suspend fun <T> executeNetworkCall(
        request: suspend () -> T
    ): Result<T> = try {
        val response = request()
        Result.Success(response)
    } catch (e: Exception) {
        FirebaseCrashlytics.getInstance().recordException(e)
        Result.Error(AppMessage.ResourceMessage(R.string.common_technical_issue))
    }

    suspend fun <T> loadWithRetry(
        config: SearchConfig,
        apiCall: suspend (String) -> T
    ): Result<T> {
        val apiList = config.apiList()
        val maxApis = config.maxApi()

        var retries = 0
        var result: Result<T> = NO_RESULT
        do {
            retries++
            var apiIndex = 0
            var api = apiList.getOrEmpty(apiIndex)
            while (api.isNotEmpty() && result !is Result.Success) {
                result = executeNetworkCall { apiCall(api) }
                apiIndex++
                api = if (apiIndex < maxApis) apiList.getOrEmpty(apiIndex) else ""
            }
        } while (result !is Result.Success && retries < config.retryCount())
        return result
    }
}

