package com.cas.musicplayer.data.remote.mappers

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-12.
 ***************************************
 */
class ListMapper<F, T>(private val itemMapper: Mapper<F, T>) : Mapper<List<F>, List<T>> {
    override suspend fun map(from: List<F>): List<T> {
        return from.map { itemMapper.map(it) }
    }
}