package com.cas.musicplayer.ui.newrelease

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cas.musicplayer.base.BaseViewModel
import com.cas.musicplayer.base.common.Resource
import com.cas.musicplayer.base.common.hasItems
import com.cas.musicplayer.base.common.isLoading
import com.cas.musicplayer.base.common.loading
import com.cas.musicplayer.data.enteties.MusicTrack
import com.cas.musicplayer.net.asResource
import com.cas.musicplayer.ui.home.domain.usecase.GetNewReleasedSongsUseCase
import com.cas.musicplayer.utils.uiCoroutine
import javax.inject.Inject

/**
 **********************************
 * Created by Abdelhadi on 4/13/19.
 **********************************
 */
class NewReleaseViewModel @Inject constructor(
    val getNewReleasedSongs: GetNewReleasedSongsUseCase
) : BaseViewModel() {

    private val _newReleases = MutableLiveData<Resource<List<MusicTrack>>>()
    val newReleases: LiveData<Resource<List<MusicTrack>>>
        get() = _newReleases

    init {
        loadTrending()
    }

    private fun loadTrending() = uiCoroutine {
        if (_newReleases.hasItems() || _newReleases.isLoading()) {
            return@uiCoroutine
        }
        _newReleases.loading()
        val result = getNewReleasedSongs(max = 50)
        _newReleases.value = result.asResource()
    }
}