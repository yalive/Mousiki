package com.cas.musicplayer.tmp

import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.cas.musicplayer.utils.EventObserver
import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.domain.models.DisplayedVideoItem
import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.ui.event.Event
import com.mousiki.shared.ui.event.asEvent
import com.mousiki.shared.ui.resource.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

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

fun Fragment.launchWhenViewResumed(
    block: suspend CoroutineScope.() -> Unit
): Job = lifecycleScope.launchWhenResumed {
    viewLifecycleOwner.lifecycleScope.launchWhenResumed(block)
}

val LiveData<List<DisplayableItem>>.tracks: List<Track>
    get() = value?.filterIsInstance<DisplayedVideoItem>()
        ?.map { it.track }.orEmpty()

fun <T> MutableLiveData<Event<T>>.trigger(eventContent: T) {
    value = eventContent.asEvent()
}

fun MutableLiveData<Event<Unit>>.trigger() {
    value = Unit.asEvent()
}