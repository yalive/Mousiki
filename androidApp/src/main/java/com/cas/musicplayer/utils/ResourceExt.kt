package com.cas.musicplayer.tmp

import androidx.lifecycle.MutableLiveData
import com.mousiki.shared.ui.resource.Resource

/**
 **********************************
 * Created by Abdelhadi on 2019-05-15.
 **********************************
 */

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
