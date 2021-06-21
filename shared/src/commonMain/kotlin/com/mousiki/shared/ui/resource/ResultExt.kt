package com.mousiki.shared.ui.resource

import com.mousiki.shared.domain.result.Result

fun <T> Result<T>.asResource(): Resource<T> {
    return when (this) {
        is Result.Success -> Resource.Success(data)
        is Result.Error -> Resource.Failure(message)
    }
}

