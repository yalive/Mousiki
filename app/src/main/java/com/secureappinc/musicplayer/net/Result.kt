package com.secureappinc.musicplayer.net

import com.secureappinc.musicplayer.data.models.Resource

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-10.
 ***************************************
 */
sealed class Result<T> {
    open fun get(): T? = null
}

data class Success<T>(val data: T) : Result<T>() {
    override fun get(): T = data
}

data class ErrorResult<T>(val exception: Exception) : Result<T>()


fun <T> Result<T>.toResource(): Resource<T> {
    return if (this is Success) Resource.success(this.data) else Resource.error("error")
}