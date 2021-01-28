package com.mousiki.shared.domain.result

import com.mousiki.shared.utils.TextResource

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-10.
 ***************************************
 */
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: TextResource, val code: String = "") : Result<Nothing>()
}

suspend fun <I, O> Result<I>.map(mapSuccess: suspend (I) -> O): Result<O> {
    return when (this) {
        is Result.Success -> Result.Success(mapSuccess(data))
        is Result.Error -> this
    }
}

suspend fun <T> Result<T>.alsoWhenSuccess(doOnSuccess: suspend (T) -> Unit): Result<T> {
    if (this is Result.Success) doOnSuccess(data)
    return this
}

fun <T> Result<List<T>>.hasData(): Boolean {
    return (this is Result.Success && data.isNotEmpty())
}