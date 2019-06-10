package com.secureappinc.musicplayer.net

import com.secureappinc.musicplayer.models.YTTrendingMusicRS
import com.secureappinc.musicplayer.models.enteties.MusicTrack
import javax.inject.Inject
import javax.inject.Singleton

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-10.
 ***************************************
 */
@Singleton
class RetrofitRunner @Inject constructor() {
    suspend fun <T, E> execute(mapper: Mapper<T, E>, request: suspend () -> T): Result<E> = try {
        val response = request()
        Success(mapper.map(response))
    } catch (e: Exception) {
        ErrorResult(e)
    }
}

interface Mapper<F, T> {
    suspend fun map(from: F): T
}

@Singleton
class TrackMapper @Inject constructor() : Mapper<YTTrendingMusicRS, List<MusicTrack>> {
    override suspend fun map(from: YTTrendingMusicRS): List<MusicTrack> {
        return from.items.map {
            val track = MusicTrack(it.id, it.snippetTitle(), it.contentDetails.duration)
            it.snippet?.urlImageOrEmpty()?.let { url ->
                track.fullImageUrl = url
            }
            track
        }
    }
}