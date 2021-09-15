package com.cas.musicplayer.ui.local.videos.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cas.musicplayer.ui.local.repository.LocalVideosRepository
import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.domain.models.LocalSong
import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.domain.models.toDisplayedVideoItem
import com.mousiki.shared.domain.usecase.recent.AddVideoToRecentlyPlayedUseCase
import com.mousiki.shared.domain.usecase.recent.GetRecentlyPlayedVideosFlowUseCase
import com.mousiki.shared.ui.base.BaseViewModel
import com.mousiki.shared.ui.resource.Resource
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class PlayedVideoViewModel(
    private val localVideosRepository: LocalVideosRepository,
    private val getRecentlyPlayedVideosFlow: GetRecentlyPlayedVideosFlowUseCase,
    private val addVideoToRecentlyPlayed: AddVideoToRecentlyPlayedUseCase,
) : BaseViewModel() {

    private val _playedVideos = MutableLiveData<Resource<List<DisplayableItem>>>()
    val playedVideos: LiveData<Resource<List<DisplayableItem>>>
        get() = _playedVideos

    init {
        getAllPlayedVideos()
    }

    fun getAllPlayedVideos() = viewModelScope.launch {
        getRecentlyPlayedVideosFlow(300)
            .onStart { _playedVideos.value = Resource.Loading }
            .collect { videos ->
                val videoTracks =
                    videos.map { LocalSong(localVideosRepository.video(it.id.toLong())).toDisplayedVideoItem() }

                _playedVideos.value = Resource.Success(videoTracks)
            }
    }

    fun onPlayVideo(track: Track) = viewModelScope.launch {
        addVideoToRecentlyPlayed(track)
    }
}