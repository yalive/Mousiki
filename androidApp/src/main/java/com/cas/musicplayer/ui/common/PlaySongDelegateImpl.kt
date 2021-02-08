package com.cas.musicplayer.ui.common

import com.cas.musicplayer.player.PlayerQueue
import com.mousiki.shared.domain.models.MusicTrack
import com.mousiki.shared.domain.usecase.recent.AddTrackToRecentlyPlayedUseCase
import com.mousiki.shared.player.PlaySongDelegate

class PlaySongDelegateImpl(
    private val addTrackToRecentlyPlayed: AddTrackToRecentlyPlayedUseCase
) : PlaySongDelegate {

    override suspend fun playTrackFromQueue(track: MusicTrack, queue: List<MusicTrack>) {
        PlayerQueue.playTrack(track, queue)
    }
}