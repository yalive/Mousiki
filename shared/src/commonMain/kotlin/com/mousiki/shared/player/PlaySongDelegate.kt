package com.mousiki.shared.player

import com.mousiki.shared.domain.models.Track

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-08.
 ***************************************
 */
interface PlaySongDelegate {
    val currentSong: Track?
    suspend fun playTrackFromQueue(track: Track, queue: List<Track>)
    fun isPlayingASong(): Boolean
}