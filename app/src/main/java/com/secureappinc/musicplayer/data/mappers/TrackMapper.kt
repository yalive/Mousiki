package com.secureappinc.musicplayer.data.mappers

import com.secureappinc.musicplayer.data.enteties.MusicTrack
import com.secureappinc.musicplayer.data.models.YTTrendingMusicRS
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackMapper @Inject constructor() :
    Mapper<YTTrendingMusicRS, List<MusicTrack>> {
    override suspend fun map(from: YTTrendingMusicRS): List<MusicTrack> {
        return from.items.map {
            val track = MusicTrack(
                it.id,
                it.snippetTitle(),
                it.contentDetails.duration
            )
            it.snippet?.urlImageOrEmpty()?.let { url ->
                track.fullImageUrl = url
            }
            track
        }
    }
}