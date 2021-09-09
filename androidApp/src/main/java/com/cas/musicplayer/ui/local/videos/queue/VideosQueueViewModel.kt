package com.cas.musicplayer.ui.local.videos.queue

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cas.musicplayer.ui.local.repository.LocalVideosRepository
import com.cas.musicplayer.ui.local.repository.filterNotHidden
import com.mousiki.shared.domain.models.DisplayedVideoItem
import com.mousiki.shared.domain.models.LocalSong
import com.mousiki.shared.domain.models.toDisplayedVideoItem
import kotlinx.coroutines.launch

class VideosQueueViewModel(
    private val localVideosRepository: LocalVideosRepository,
) : ViewModel() {

    private val _localVideos = MutableLiveData<List<DisplayedVideoItem>>()
    val localVideos: LiveData<List<DisplayedVideoItem>> get() = _localVideos


    private var currentVideoId = 0L

    init {
        loadAllVideos()
    }

    private fun loadAllVideos() = viewModelScope.launch {
        val videos = localVideosRepository.videos().filterNotHidden()
        _localVideos.value = videos.map {
            LocalSong(it).toDisplayedVideoItem(isCurrent = currentVideoId == it.id)
        }
    }

    fun onVideoChanged(newVideoId: Long) {
        this.currentVideoId = newVideoId
        if (_localVideos.value == null) return
        _localVideos.value = _localVideos.value?.map {
            it.copy(
                isCurrent = it.track.id == newVideoId.toString()
            )
        }
    }
}
