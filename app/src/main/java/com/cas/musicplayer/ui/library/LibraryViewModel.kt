package com.cas.musicplayer.ui.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cas.common.viewmodel.BaseViewModel
import com.cas.musicplayer.domain.usecase.library.GetHeavyTracksUseCase
import com.cas.musicplayer.domain.usecase.recent.GetRecentlyPlayedSongsUseCase
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem
import com.cas.musicplayer.ui.home.model.toDisplayedVideoItem
import com.cas.musicplayer.utils.uiCoroutine
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-28.
 ***************************************
 */
class LibraryViewModel @Inject constructor(
    private val getRecentlyPlayedSongs: GetRecentlyPlayedSongsUseCase,
    private val getHeavyTracks: GetHeavyTracksUseCase
) : BaseViewModel() {

    private val _recentSongs = MutableLiveData<List<DisplayedVideoItem>>()
    val recentSongs: LiveData<List<DisplayedVideoItem>>
        get() = _recentSongs

    private val _heavySongs = MutableLiveData<List<DisplayedVideoItem>>()
    val heavySongs: LiveData<List<DisplayedVideoItem>>
        get() = _heavySongs

    init {
        loadRecentlyPlayedSongs()
        loadHeavyTrackList()
    }

    private fun loadRecentlyPlayedSongs() = uiCoroutine {
        val songs = getRecentlyPlayedSongs()
        _recentSongs.value = songs.map { it.toDisplayedVideoItem() }
    }

    private fun loadHeavyTrackList() = uiCoroutine {
        val songs = getHeavyTracks()
        _heavySongs.value = songs.map { it.toDisplayedVideoItem() }
    }
}