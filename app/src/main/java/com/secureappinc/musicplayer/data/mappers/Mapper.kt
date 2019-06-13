package com.secureappinc.musicplayer.data.mappers

/**
 * [F]: The type to convert
 */
interface Mapper<F, T> {
    suspend fun map(from: F): T
}

fun <F, T> Mapper<F, T>.toListMapper(): Mapper<List<F>, List<T>> = ListMapper(this)