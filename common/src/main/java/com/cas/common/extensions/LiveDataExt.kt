package com.cas.common.extensions

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.cas.common.event.Event
import com.cas.common.event.EventObserver
import com.cas.common.resource.Resource

/**
 **********************************
 * Created by Abdelhadi on 2019-05-31.
 **********************************
 */

fun <T> LiveData<Resource<T>>.valueOrNull(): T? {
    val currentValue = value
    if (currentValue is Resource.Success) return currentValue.data
    return null
}

fun <T> LifecycleOwner.observe(liveData: LiveData<T>, body: (T) -> Unit = {}) {
    liveData.observe(this, Observer { it?.let { t -> body(t) } })
}

fun <T> LifecycleOwner.observeEvent(liveData: LiveData<Event<T>>, body: (T) -> Unit = {}) {
    liveData.observe(this, EventObserver { it?.let { t -> body(t) } })
}

fun <T> MutableLiveData<T>.toImmutableLiveData() = this as LiveData<T>