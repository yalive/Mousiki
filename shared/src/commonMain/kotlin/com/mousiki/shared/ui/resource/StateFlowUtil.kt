package com.mousiki.shared.ui.resource

import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.domain.models.LoadingItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

fun <T> StateFlow<Resource<T>?>.valueOrNull(): T? {
    return (value as? Resource.Success)?.data
}

fun <T> MutableStateFlow<Resource<List<T>>?>.hasItems(): Boolean {
    return when (val currentValue = value ?: return false) {
        is Resource.Success -> currentValue.data.isNotEmpty()
        else -> false
    }
}

fun <T> MutableStateFlow<Resource<T>?>.isLoading(): Boolean {
    val currentValue = value ?: return false
    return currentValue is Resource.Loading
}

fun <T> MutableStateFlow<Resource<T>?>.isError(): Boolean {
    val currentValue = value ?: return false
    return currentValue is Resource.Failure
}

fun <T> MutableStateFlow<Resource<T>?>.loading() {
    value = Resource.Loading
}


fun <T> Resource<T>.doOnSuccess(block: (T) -> Unit) {
    when (this) {
        is Resource.Success -> block(data)
    }
}

fun MutableStateFlow<Resource<List<DisplayableItem>>?>.removeLoading() {
    val oldList = (valueOrNull() ?: emptyList()).toMutableList()
    oldList.remove(LoadingItem)
    value = Resource.Success(oldList)
}

fun MutableStateFlow<Resource<List<DisplayableItem>>?>.appendItems(
    newItems: List<DisplayableItem>,
    removeLoading: Boolean
) {
    val oldList = (valueOrNull() ?: emptyList()).toMutableList().apply {
        addAll(newItems)
    }
    if (removeLoading) {
        oldList.remove(LoadingItem)
    }
    value = Resource.Success(oldList)
}