package com.cas.musicplayer.ui.home.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cas.musicplayer.base.BaseViewModel
import com.cas.musicplayer.base.common.*
import com.cas.musicplayer.data.models.Artist
import com.cas.musicplayer.net.asOldResource
import com.cas.musicplayer.net.asResource
import com.cas.musicplayer.net.map
import com.cas.musicplayer.ui.home.domain.usecase.GetNewReleasedSongsUseCase
import com.cas.musicplayer.ui.home.domain.usecase.GetTopArtistsUseCase
import com.cas.musicplayer.ui.home.ui.model.NewReleaseDisplayedItem
import com.cas.musicplayer.ui.home.ui.model.toDisplayedNewRelease
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

val bgContext = Dispatchers.IO
val uiContext = Dispatchers.Main

val uiScope = CoroutineScope(uiContext)

/**
 **********************************
 * Created by Abdelhadi on 4/13/19.
 **********************************
 */
class HomeViewModel @Inject constructor(
    private val getNewReleasedSongs: GetNewReleasedSongsUseCase,
    private val getTopArtists: GetTopArtistsUseCase
) : BaseViewModel() {

    private val _trendingTracks = MutableLiveData<Resource<List<NewReleaseDisplayedItem>>>()
    val trendingTracks: LiveData<Resource<List<NewReleaseDisplayedItem>>>
        get() = _trendingTracks

    var sixArtists = MutableLiveData<ResourceOld<List<Artist>>>()

    fun loadTrendingMusic() = uiScope.launch(coroutineContext) {
        if (_trendingTracks.hasItems() || _trendingTracks.isLoading()) {
            return@launch
        }
        _trendingTracks.loading()
        val result = getNewReleasedSongs()
        _trendingTracks.value = result.map { tracks ->
            tracks.map { it.toDisplayedNewRelease() }
        }.asResource()
    }

    fun loadArtists(countryCode: String) = uiScope.launch(coroutineContext) {
        if (!sixArtists.hasItems() && !sixArtists.isLoading()) {
            sixArtists.loading()
            val sixArtistResult = getTopArtists(countryCode)
            sixArtists.value = sixArtistResult.asOldResource()
        }
    }
}

