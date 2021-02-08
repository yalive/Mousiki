package com.mousiki.shared.ui.resource

import com.mousiki.shared.utils.TextResource

/**
 * Represent a network-bound resource and its states.
 */
sealed class Resource<out T> {

    object Loading : Resource<Nothing>()

    data class Success<out T>(val data: T) : Resource<T>()

    data class Failure(val message: TextResource) : Resource<Nothing>()
}