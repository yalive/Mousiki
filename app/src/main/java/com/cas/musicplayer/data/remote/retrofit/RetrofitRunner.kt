package com.cas.musicplayer.data.remote.retrofit

import com.cas.common.result.AppMessage
import com.cas.common.result.Result
import com.cas.musicplayer.R
import com.cas.musicplayer.data.remote.mappers.Mapper
import com.cas.musicplayer.utils.bgContext
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
        Result.Error(AppMessage.ResourceMessage(R.string.common_technical_issue))
    }
}

