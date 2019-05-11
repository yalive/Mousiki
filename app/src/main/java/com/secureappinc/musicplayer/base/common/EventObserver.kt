package com.secureappinc.musicplayer.base.common

import androidx.lifecycle.Observer

/**
 **********************************
 * Created by Abdelhadi on 4/8/19.
 **********************************
 */
/**
 * An [Observer] for [Event]s, simplifying the pattern of checking if the [Event]'s content has
 * already been handled.
 *
 * [onEventUnhandledContent] is *only* called if the [Event]'s contents has not been handled.
 *
 * [Source: https://gist.github.com/JoseAlcerreca/e0bba240d9b3cffa258777f12e5c0ae9]
 */
class EventObserver<T>(private val onEventUnhandledContent: (T) -> Unit) : Observer<Event<T>> {
    override fun onChanged(event: Event<T>?) {
        event?.getContentIfNotHandled()?.let { value ->
            onEventUnhandledContent(value)
        }
    }
}