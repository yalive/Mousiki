package com.cas.common.resource

import androidx.lifecycle.MutableLiveData
import com.mousiki.shared.utils.TextResource

/**
 **********************************
 * Created by Abdelhadi on 2019-05-15.
 **********************************
 */
/**
 * Represent a network-bound resource and its states.
 */
sealed class Resource<out T> {

    object Loading : Resource<Nothing>()

    data class Success<out T>(val data: T) : Resource<T>()

    data class Failure(val message: TextResource) : Resource<Nothing>()
}

fun <T> MutableLiveData<Resource<List<T>>>.hasItems(): Boolean {
    return when (val currentValue = value ?: return false) {
        is Resource.Success -> currentValue.data.isNotEmpty()
        else -> false
    }
}

fun <T> MutableLiveData<Resource<T>>.isLoading(): Boolean {
    val currentValue = value ?: return false
    return currentValue is Resource.Loading
}

fun <T> MutableLiveData<Resource<T>>.isError(): Boolean {
    val currentValue = value ?: return false
    return currentValue is Resource.Failure
}

fun <T> MutableLiveData<Resource<T>>.loading() {
    value = Resource.Loading
}


fun <T> Resource<T>.doOnSuccess(block: (T) -> Unit) {
    when (this) {
        is Resource.Success -> block(data)
    }
}
