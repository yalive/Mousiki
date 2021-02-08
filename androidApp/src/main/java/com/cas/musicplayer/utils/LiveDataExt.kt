package com.cas.musicplayer.tmp

import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.cas.musicplayer.utils.EventObserver
import com.mousiki.shared.ui.event.Event
import com.mousiki.shared.ui.resource.Resource

/**
 **********************************
 * Created by Abdelhadi on 2019-05-31.
 **********************************
 */

fun <T> LiveData<Resource<T>>.valueOrNull(): T? {
    return (value as? Resource.Success)?.data
}

fun <T> LifecycleOwner.observe(liveData: LiveData<T>, body: (T) -> Unit = {}) {
    val owner = if (this is Fragment) viewLifecycleOwner else this
    liveData.observe(owner, Observer { it?.let { t -> body(t) } })
}

fun <T> LifecycleOwner.observeEvent(liveData: LiveData<Event<T>>, body: (T) -> Unit = {}) {
    val owner = if (this is Fragment) viewLifecycleOwner else this
    liveData.observe(owner, EventObserver { it?.let { t -> body(t) } })
}

fun <T> MutableLiveData<T>.toImmutableLiveData() = this as LiveData<T>