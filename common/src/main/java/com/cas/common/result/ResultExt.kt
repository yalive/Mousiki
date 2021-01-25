package com.cas.common.result

import com.cas.common.R
import com.cas.common.resource.Resource
import com.mousiki.shared.domain.result.Result
import com.mousiki.shared.utils.TextResource

fun <T> Result<T>.asResource(): Resource<T> {
    return when (this) {
        is Result.Success -> Resource.Success(data)
        is Result.Error -> Resource.Failure(message)
    }
}

val NO_RESULT = Result.Error(TextResource.fromStringId(R.string.common_empty_state))