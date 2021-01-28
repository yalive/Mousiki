package com.mousiki.shared.data.remote.runner

import com.mousiki.shared.data.config.SearchConfig
import com.mousiki.shared.data.config.apiList
import com.mousiki.shared.data.config.maxApi
import com.mousiki.shared.data.config.retryCount
import com.mousiki.shared.data.remote.mapper.Mapper
import com.mousiki.shared.domain.result.Result
import com.mousiki.shared.utils.TextResource
import com.mousiki.shared.utils.getOrEmpty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-10.
 ***************************************
 */
class NetworkRunner {

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
        val mappedResponse = withContext(Dispatchers.Default) {
            mapper.map(response)
        }
        Result.Success(mappedResponse)
    } catch (e: Exception) {
        //FirebaseCrashlytics.getInstance().recordException(e)
        Result.Error(TextResource.fromText("Something was wrong"))
    }

    suspend fun <T> executeNetworkCall(
        request: suspend () -> T
    ): Result<T> = try {
        val response = request()
        Result.Success(response)
    } catch (e: Exception) {
        //FirebaseCrashlytics.getInstance().recordException(e)
        Result.Error(TextResource.fromText("Something was wrong"))
    }

    suspend fun <T> loadWithRetry(
        config: SearchConfig,
        apiCall: suspend (String) -> T
    ): Result<T> {
        val apiList = config.apiList()
        val maxApis = config.maxApi()

        var retries = 0
        var result: Result<T> = Result.Error(TextResource.fromText("Something was wrong"))
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

