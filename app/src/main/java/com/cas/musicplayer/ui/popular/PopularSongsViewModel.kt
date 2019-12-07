package com.cas.musicplayer.ui.popular

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cas.common.extensions.valueOrNull
import com.cas.common.resource.Resource
import com.cas.common.resource.hasItems
import com.cas.common.resource.isLoading
import com.cas.common.resource.loading
import com.cas.common.result.Result
import com.cas.common.result.asResource
import com.cas.common.result.map
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.domain.usecase.recent.AddTrackToRecentlyPlayedUseCase
import com.cas.musicplayer.domain.usecase.song.GetPopularSongsUseCase
import com.cas.musicplayer.ui.BaseSongsViewModel
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem
import com.cas.musicplayer.ui.home.model.toDisplayedVideoItem
import com.cas.musicplayer.utils.uiCoroutine
import javax.inject.Inject

/**
 **********************************
 * Created by Abdelhadi on 4/13/19.
 **********************************
 */
class PopularSongsViewModel @Inject constructor(
    private val getPopularSongs: GetPopularSongsUseCase,
    addTrackToRecentlyPlayed: AddTrackToRecentlyPlayedUseCase
) : BaseSongsViewModel(addTrackToRecentlyPlayed) {

    private val _newReleases = MutableLiveData<Resource<List<DisplayedVideoItem>>>()
    val newReleases: LiveData<Resource<List<DisplayedVideoItem>>>
        get() = _newReleases

    private val _hepMessage = MutableLiveData<String>()
    val hepMessage: LiveData<String>
        get() = _hepMessage

    private val _loadMore = MutableLiveData<Resource<Unit>>()
    val loadMore: LiveData<Resource<Unit>>
        get() = _loadMore

    init {
        loadTrending()
    }

    private fun loadTrending() = uiCoroutine {
        if (_newReleases.hasItems() || _newReleases.isLoading()) {
            return@uiCoroutine
        }
        _newReleases.loading()
        val result = getPopularSongs(25)
        _newReleases.value = result.map { tracks ->
            tracks.map { it.toDisplayedVideoItem() }
        }.asResource()
    }

    fun loadMoreSongs() = uiCoroutine {
        val previousList = _newReleases.valueOrNull()
        if (previousList != null && previousList.isNotEmpty() && previousList.size < MAX_VIDEOS) {
            _loadMore.value = Resource.Loading
            val result = getPopularSongs.invoke(25, previousList.lastOrNull()?.track)
            if (result is Result.Success) {
                _newReleases.value = result.map { tracks ->
                    val newPageMapped = tracks.map { it.toDisplayedVideoItem() }
                    previousList.toMutableList().apply {
                        addAll(newPageMapped)
                    }
                }.asResource()
            }
            _loadMore.value = Resource.Success(Unit)
        }

        if (previousList != null && previousList.size >= MAX_VIDEOS) {
            _hepMessage.value = "Max reached"
        }
    }

    fun onClickTrack(track: MusicTrack) {
        val tracks = (_newReleases.value as? Resource.Success)?.data?.map { it.track } ?: emptyList()
        playTrackFromQueue(track, tracks)
    }

    companion object {
        private const val MAX_VIDEOS = 200
    }
}