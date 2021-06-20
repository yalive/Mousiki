package com.cas.musicplayer.ui.common

import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.player.services.PlaybackLiveData
import com.mousiki.shared.domain.models.LocalSong
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
        if (queue.none { it.id == "local_123" }) {
            val localTrack = LocalSong(
                id = "local_123",
                title = "💪💪 Hamid el kasri - Ya moulana 💪💪",
                duration = "482978",
                artistName = "Hamid El Kasri",
                path = "/storage/emulated/0/Download/Misc/hamid.mp3"
            )
            val mQueue = mutableListOf<Track>()
            mQueue.add(localTrack)
            mQueue.addAll(queue)
            PlayerQueue.playTrack(track, mQueue)
        } else {
            PlayerQueue.playTrack(track, queue)
        }
    }

    override fun isPlayingASong(): Boolean {
        val state = PlaybackLiveData.value ?: return false
        return state == PlayerConstants.PlayerState.PLAYING || state == PlayerConstants.PlayerState.BUFFERING
    }
}