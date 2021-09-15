package com.mousiki.shared.ui.event

import kotlinx.coroutines.flow.MutableStateFlow

/**
 **********************************
 * Created by Abdelhadi on 4/8/19.
 **********************************
 */
/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 */
open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content
}

fun <T> T.asEvent(): Event<T> {
    return Event(this)
}

fun <T> MutableStateFlow<Event<T>>.trigger(eventContent: T) {
    value = eventContent.asEvent()
}

fun MutableStateFlow<Event<Unit>>.trigger() {
    value = kotlin.Unit.asEvent()
}