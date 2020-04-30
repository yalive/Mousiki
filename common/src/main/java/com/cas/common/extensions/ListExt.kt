package com.cas.common.extensions

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/29/20.
 ***************************************
 */

fun <T> Collection<T>.randomOrNull(): T? {
    return if (isEmpty()) null else random()
}