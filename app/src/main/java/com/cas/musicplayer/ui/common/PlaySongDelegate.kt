package com.cas.musicplayer.ui.common

import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.domain.usecase.recent.AddTrackToRecentlyPlayedUseCase
import com.cas.musicplayer.player.PlayerQueue
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-08.
 ***************************************
 */
interface PlaySongDelegate {
    suspend fun playTrackFromQueue(track: MusicTrack, queue: List<MusicTrack>)
}

class PlaySongDelegateImpl @Inject constructor(
    private val addTrackToRecentlyPlayed: AddTrackToRecentlyPlayedUseCase
) : PlaySongDelegate {

    override suspend fun playTrackFromQueue(track: MusicTrack, queue: List<MusicTrack>) {
        PlayerQueue.playTrack(track, queue)
    }
}