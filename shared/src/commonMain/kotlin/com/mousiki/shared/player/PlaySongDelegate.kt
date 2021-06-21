package com.mousiki.shared.player

import com.mousiki.shared.domain.models.MusicTrack

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-08.
 ***************************************
 */
interface PlaySongDelegate {
    val currentSong: MusicTrack?
    suspend fun playTrackFromQueue(track: MusicTrack, queue: List<MusicTrack>)
    fun isPlayingASong(): Boolean
}