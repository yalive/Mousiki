package com.cas.musicplayer.ui.player.queue

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.player.services.PlaybackLiveData
import com.mousiki.shared.domain.models.DisplayedVideoItem
import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.domain.models.toDisplayedVideoItem
import com.mousiki.shared.player.PlaySongDelegate
import com.mousiki.shared.ui.event.Event
import com.mousiki.shared.ui.event.asEvent
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import kotlinx.coroutines.launch

class QueueViewModel(
    playSongDelegate: PlaySongDelegate
) : ViewModel(), PlaySongDelegate by playSongDelegate {

    private val _queue = MediatorLiveData<List<DisplayedVideoItem>>()
    val queue: LiveData<List<DisplayedVideoItem>> = _queue

    private val _onClickTrack = MediatorLiveData<Event<Track>>()
    val onClickTrack: LiveData<Event<Track>> = _onClickTrack

    private var currentQueue = listOf<DisplayedVideoItem>()

    init {
        _queue.addSource(PlayerQueue) {
            onQueueChanged()
        }
    }

    fun onClickTrack(track: Track) = viewModelScope.launch {
        if (PlayerQueue.isCurrentTrack(track) && (PlaybackLiveData.isPlaying() || PlaybackLiveData.isPause())) {
            PlayerQueue.togglePlayback()
        } else {
            val tracks = PlayerQueue.queue ?: listOf()
            playTrackFromQueue(track, tracks)
        }
    }

    fun onClickShowMoreOptions(track: Track) {
        _onClickTrack.value = track.asEvent()
    }

    fun updateQueue(state: PlayerConstants.PlayerState?) {
        var indexOfCurrent = Int.MAX_VALUE
        val updatedList = currentQueue.mapIndexed { index, item ->
            val isCurrent = PlayerQueue.isCurrentTrack(item.track)
            if (isCurrent) {
                indexOfCurrent = index
            }
            item.copy(
                isCurrent = isCurrent,
                isPlaying = isCurrent && (state == PlayerConstants.PlayerState.PLAYING || state == PlayerConstants.PlayerState.BUFFERING),
                beforeCurrent = !isCurrent && index < indexOfCurrent
            )
        }
        currentQueue = updatedList
        _queue.postValue(updatedList)
    }

    fun swapItems(from: Int, to: Int) {
        val indexOfCurrent = currentQueue.indexOfFirst {
            it.track.id == PlayerQueue.value?.id
        }
        if (from <= indexOfCurrent || to <= indexOfCurrent) return
        PlayerQueue.swapTracks(from, to)
        onQueueChanged()
    }

    fun removeTrackFromQueue(position: Int) {
        if (isCurrentTrack(position)) return
        PlayerQueue.removeTrack(currentQueue[position].track)
        onQueueChanged()
    }

    private fun onQueueChanged() {
        val tracks = PlayerQueue.queue ?: listOf()
        currentQueue = tracks.map { it.toDisplayedVideoItem() }
        updateQueue(PlaybackLiveData.value)
    }

    fun isCurrentTrack(position: Int): Boolean {
        val indexOfCurrent = currentQueue.indexOfFirst {
            it.track.id == PlayerQueue.value?.id
        }
        return indexOfCurrent == position
    }
}
