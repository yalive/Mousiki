package com.cas.common.result

import com.cas.common.resource.Resource
import com.mousiki.shared.domain.result.Result
import com.mousiki.shared.utils.TextResource

fun <T> Result<T>.asResource(): Resource<T> {
    return when (this) {
        is Result.Success -> Resource.Success(data)
        is Result.Error -> Resource.Failure(message)
    }
}

