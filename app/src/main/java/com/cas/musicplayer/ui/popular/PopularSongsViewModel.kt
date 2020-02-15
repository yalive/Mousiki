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
import com.cas.common.viewmodel.BaseViewModel
import com.cas.delegatedadapter.DisplayableItem
import com.cas.delegatedadapter.LoadingItem
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.domain.usecase.song.GetPopularSongsUseCase
import com.cas.musicplayer.ui.common.PlaySongDelegate
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
    delegate: PlaySongDelegate
) : BaseViewModel(), PlaySongDelegate by delegate {

    private val allSongs = mutableListOf<MusicTrack>()

    private val _newReleases = MutableLiveData<Resource<List<DisplayableItem>>>()
    val newReleases: LiveData<Resource<List<DisplayableItem>>>
        get() = _newReleases

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
            tracks.map { it.toDisplayedVideoItem() }.toMutableList()
        }.asResource()
    }

    var loadingMore = false
    fun loadMoreSongs() = uiCoroutine {
        if (loadingMore) return@uiCoroutine
        if (allSongs.isNotEmpty() && allSongs.size < MAX_VIDEOS) {
            loadingMore = true
            _newReleases.appendItems(listOf(LoadingItem))
            val result = getPopularSongs(25, allSongs.lastOrNull())
            loadingMore = false
            if (result is Result.Success) {
                val newSongs = result.data
                allSongs.addAll(newSongs)
                val newPageMapped = newSongs.map { it.toDisplayedVideoItem() }
                _newReleases.appendItemsAndRemoveLoading(newPageMapped)
            } else {
                _newReleases.removeLoading()
            }
        }
    }

    fun onClickTrack(track: MusicTrack) {
        uiCoroutine {
            playTrackFromQueue(track, allSongs)
        }
    }

    fun onClickTrackPlayAll() {
        uiCoroutine {
            if (allSongs.isEmpty()) return@uiCoroutine
            playTrackFromQueue(allSongs.first(), allSongs)
        }
    }

    companion object {
        private const val MAX_VIDEOS = 200
    }
}

fun MutableLiveData<Resource<List<DisplayableItem>>>.removeLoading() {
    val oldList = (valueOrNull() ?: emptyList()).toMutableList()
    oldList.remove(LoadingItem)
    value = Resource.Success(oldList)
}

fun MutableLiveData<Resource<List<DisplayableItem>>>.appendItems(newItems: List<DisplayableItem>) {
    val oldList = (valueOrNull() ?: emptyList()).toMutableList().apply {
        addAll(newItems)
    }
    value = Resource.Success(oldList)
}

fun MutableLiveData<Resource<List<DisplayableItem>>>.appendItemsAndRemoveLoading(newItems: List<DisplayableItem>) {
    val oldList = (valueOrNull() ?: emptyList()).toMutableList().apply {
        addAll(newItems)
    }
    oldList.remove(LoadingItem)
    value = Resource.Success(oldList)
}

private fun <T> List<T>.swapped(position1: Int, position2: Int): List<T> {
    if (position1 > size || position2 >= size || position1 < 0 || position2 < 0) return this
    val mutableList = toMutableList()
    val item1 = mutableList[position1]
    val item2 = mutableList[position2]
    mutableList[position1] = item2
    mutableList[position2] = item1
    return mutableList
}