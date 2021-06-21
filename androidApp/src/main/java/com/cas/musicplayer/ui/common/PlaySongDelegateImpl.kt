package com.cas.musicplayer.ui.common

import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.player.services.PlaybackLiveData
import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.domain.usecase.recent.AddTrackToRecentlyPlayedUseCase
import com.mousiki.shared.player.PlaySongDelegate
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants

class PlaySongDelegateImpl(
    private val addTrackToRecentlyPlayed: AddTrackToRecentlyPlayedUseCase
) : PlaySongDelegate {

    override val currentSong: Track?
        get() = PlayerQueue.value

    override suspend fun playTrackFromQueue(track: Track, queue: List<Track>) {
        PlayerQueue.playTrack(track, queue)
    }

    override fun isPlayingASong(): Boolean {
        val state = PlaybackLiveData.value ?: return false
        return state == PlayerConstants.PlayerState.PLAYING || state == PlayerConstants.PlayerState.BUFFERING
    }
}