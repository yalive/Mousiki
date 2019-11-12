package com.cas.musicplayer.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.cas.musicplayer.base.common.Event
import com.cas.musicplayer.base.common.EventObserver
import com.cas.musicplayer.base.common.Resource

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
