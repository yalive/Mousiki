package com.cas.musicplayer.ui.popular

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cas.common.resource.Resource
import com.cas.common.resource.hasItems
import com.cas.common.resource.isLoading
import com.cas.common.resource.loading
import com.cas.common.result.Result
import com.cas.common.result.asResource
import com.cas.common.result.map
import com.cas.common.viewmodel.BaseViewModel
import com.cas.delegatedadapter.DisplayableItem
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.domain.usecase.song.GetPopularSongsUseCase
import com.cas.musicplayer.ui.common.PlaySongDelegate
import com.cas.musicplayer.ui.home.model.toDisplayedVideoItem
import com.cas.musicplayer.ui.popular.model.SongsHeaderItem
import com.cas.musicplayer.utils.uiCoroutine
import javax.inject.Inject

/**
 **********************************
 * Created by Abdelhadi on 4/13/19.
 **********************************
 */
class PopularSongsViewModel @Inject constructor(
    private val getPopularSongs: GetPopularSongsUseCase,
    delegate: PlaySongDelegate
) : BaseViewModel(), PlaySongDelegate by delegate {

    private val allSongs = mutableListOf<MusicTrack>()

    private val _newReleases = MutableLiveData<Resource<List<DisplayableItem>>>()
    val newReleases: LiveData<Resource<List<DisplayableItem>>>
        get() = _newReleases

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
            allSongs.addAll(tracks)
            val songs: MutableList<DisplayableItem> = tracks.map { it.toDisplayedVideoItem() }.toMutableList()
            if (tracks.isNotEmpty()) {
                songs.apply {
                    add(0, SongsHeaderItem(tracks[0]))
                }
            }
            songs
        }.asResource()
    }

    fun loadMoreSongs() = uiCoroutine {
        if (_loadMore.isLoading()) return@uiCoroutine
        if (allSongs.isNotEmpty() && allSongs.size < MAX_VIDEOS) {
            _loadMore.value = Resource.Loading
            val result = getPopularSongs.invoke(25, allSongs.lastOrNull())
            _loadMore.value = Resource.Success(Unit)
            if (result is Result.Success) {
                val newSongs = result.data
                allSongs.addAll(newSongs)
                val newPageMapped = newSongs.map { it.toDisplayedVideoItem() }
                _newReleases.value = Resource.Success(newPageMapped)
            }
        }
    }

    fun onClickTrack(track: MusicTrack) {
        uiCoroutine {
            playTrackFromQueue(track, allSongs)
        }
    }

    companion object {
        private const val MAX_VIDEOS = 200
    }
}