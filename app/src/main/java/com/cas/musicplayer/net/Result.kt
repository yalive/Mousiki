package com.cas.musicplayer.net

import androidx.annotation.StringRes
import com.cas.musicplayer.R
import com.cas.musicplayer.base.common.Resource

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-10.
 ***************************************
 */
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: AppMessage, val code: String = "") : Result<Nothing>()
}

sealed class AppMessage {
    data class ResourceMessage(
        @StringRes val resMessage: Int,
        val arguments: List<String> = emptyList()
    ) : AppMessage()

    data class StringMessage(val message: String) : AppMessage()
}

fun <T> Result<T>.asResource(): Resource<T> {
    return when (this) {
        is Result.Success -> Resource.Success(data)
        is Result.Error -> Resource.Failure(message)
    }
}

suspend fun <I, O> Result<I>.map(mapSuccess: suspend (I) -> O): Result<O> {
    return when (this) {
        is Result.Success -> Result.Success(mapSuccess(data))
        is Result.Error -> this
    }
}

val TECHNICAL_ISSUE_RESULT =
    Result.Error(AppMessage.ResourceMessage(R.string.common_technical_issue))
val NO_RESULT = Result.Error(AppMessage.ResourceMessage(R.string.common_empty_state))