package com.cas.musicplayer.ui.popular

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cas.musicplayer.base.BaseViewModel
import com.cas.musicplayer.base.common.Resource
import com.cas.musicplayer.base.common.hasItems
import com.cas.musicplayer.base.common.isLoading
import com.cas.musicplayer.base.common.loading
import com.cas.musicplayer.net.asResource
import com.cas.musicplayer.net.map
import com.cas.musicplayer.ui.home.domain.usecase.GetPopularSongsUseCase
import com.cas.musicplayer.ui.home.ui.model.DisplayedVideoItem
import com.cas.musicplayer.ui.home.ui.model.toDisplayedVideoItem
import com.cas.musicplayer.utils.uiCoroutine
import javax.inject.Inject

/**
 **********************************
 * Created by Abdelhadi on 4/13/19.
 **********************************
 */
class PopularSongsViewModel @Inject constructor(
    val getPopularSongs: GetPopularSongsUseCase
) : BaseViewModel() {

    private val _newReleases = MutableLiveData<Resource<List<DisplayedVideoItem>>>()
    val newReleases: LiveData<Resource<List<DisplayedVideoItem>>>
        get() = _newReleases

    init {
        loadTrending()
    }

    private fun loadTrending() = uiCoroutine {
        if (_newReleases.hasItems() || _newReleases.isLoading()) {
            return@uiCoroutine
        }
        _newReleases.loading()
        val result = getPopularSongs(max = 50)
        _newReleases.value = result.map { tracks ->
            tracks.map { it.toDisplayedVideoItem() }
        }.asResource()
    }
}