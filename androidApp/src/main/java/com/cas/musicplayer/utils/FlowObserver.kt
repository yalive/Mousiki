package com.cas.musicplayer.utils

import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.asLiveData
import com.mousiki.shared.ui.event.Event
import kotlinx.coroutines.flow.Flow

fun <T> LifecycleOwner.observeEvent(flow: Flow<Event<T>?>, body: (T) -> Unit = {}) {
    val owner = if (this is Fragment) viewLifecycleOwner else this
    flow.asLiveData().observe(owner, EventObserver { it?.let { t -> body(t) } })
}