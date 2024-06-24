package com.mousiki.shared.data.repository

import com.mousiki.shared.domain.models.AiTrack
import com.mousiki.shared.domain.models.LocalSong
import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.domain.models.YtbTrack

class LocalTrackMapper(private val songProvider: LocalSongProvider) {

    suspend fun mapTracks(tracks: List<Track>): List<Track> {
        return tracks.map {
            when (it) {
                is LocalSong -> LocalSong(songProvider.getSongById(it.song.id))
                is YtbTrack -> it
                is AiTrack -> it
            }
        }
    }
}

