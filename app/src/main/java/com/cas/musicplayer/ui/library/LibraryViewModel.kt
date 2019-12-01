package com.cas.musicplayer.ui.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cas.common.viewmodel.BaseViewModel
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
    private val getRecentlyPlayedSongs: GetRecentlyPlayedSongsUseCase
) : BaseViewModel() {

    private val _recentSongs = MutableLiveData<List<DisplayedVideoItem>>()
    val recentSongs: LiveData<List<DisplayedVideoItem>>
        get() = _recentSongs

    init {
        loadRecentlyPlayedSongs()
    }

    private fun loadRecentlyPlayedSongs() = uiCoroutine {
        val songs = getRecentlyPlayedSongs()
        _recentSongs.value = songs.map { it.toDisplayedVideoItem() }
    }
}