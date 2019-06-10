package com.secureappinc.musicplayer.net

import com.secureappinc.musicplayer.data.mappers.Mapper
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
    suspend fun <T, E> executeNetworkCall(mapper: Mapper<T, E>, request: suspend () -> T): Result<E> = try {
        val response = request()
        Success(mapper.map(response))
    } catch (e: Exception) {
        ErrorResult(e)
    }
}

