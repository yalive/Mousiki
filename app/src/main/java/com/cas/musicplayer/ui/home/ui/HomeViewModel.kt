package com.cas.musicplayer.ui.home.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cas.musicplayer.base.BaseViewModel
import com.cas.musicplayer.base.common.Resource
import com.cas.musicplayer.base.common.hasItems
import com.cas.musicplayer.base.common.isLoading
import com.cas.musicplayer.base.common.loading
import com.cas.musicplayer.data.models.Artist
import com.cas.musicplayer.net.asResource
import com.cas.musicplayer.net.map
import com.cas.musicplayer.ui.home.domain.model.ChartModel
import com.cas.musicplayer.ui.home.domain.model.GenreMusic
import com.cas.musicplayer.ui.home.domain.usecase.GetChartsUseCase
import com.cas.musicplayer.ui.home.domain.usecase.GetGenresUseCase
import com.cas.musicplayer.ui.home.domain.usecase.GetNewReleasedSongsUseCase
import com.cas.musicplayer.ui.home.domain.usecase.GetTopArtistsUseCase
import com.cas.musicplayer.ui.home.ui.model.NewReleaseDisplayedItem
import com.cas.musicplayer.ui.home.ui.model.toDisplayedNewRelease
import com.cas.musicplayer.utils.getCurrentLocale
import com.cas.musicplayer.utils.uiCoroutine
import javax.inject.Inject

/**
 **********************************
 * Created by Abdelhadi on 4/13/19.
 **********************************
 */
class HomeViewModel @Inject constructor(
    private val getNewReleasedSongs: GetNewReleasedSongsUseCase,
    private val getTopArtists: GetTopArtistsUseCase,
    private val getCharts: GetChartsUseCase,
    private val getGenres: GetGenresUseCase
) : BaseViewModel() {

    private val _trendingTracks = MutableLiveData<Resource<List<NewReleaseDisplayedItem>>>()
    val trendingTracks: LiveData<Resource<List<NewReleaseDisplayedItem>>>
        get() = _trendingTracks

    private val _charts = MutableLiveData<Resource<List<ChartModel>>>()
    val charts: LiveData<Resource<List<ChartModel>>>
        get() = _charts

    private val _genres = MutableLiveData<Resource<List<GenreMusic>>>()
    val genres: LiveData<Resource<List<GenreMusic>>>
        get() = _genres

    private val _artists = MutableLiveData<Resource<List<Artist>>>()
    val artists: LiveData<Resource<List<Artist>>>
        get() = _artists

    init {
        loadTrending()
        loadArtists(getCurrentLocale())
        loadCharts()
        loadGenres()
    }


    private fun loadTrending() = uiCoroutine {
        if (_trendingTracks.hasItems() || _trendingTracks.isLoading()) {
            return@uiCoroutine
        }
        _trendingTracks.loading()
        val result = getNewReleasedSongs()
        _trendingTracks.value = result.map { tracks ->
            tracks.map { it.toDisplayedNewRelease() }
        }.asResource()
    }

    private fun loadCharts() = uiCoroutine {
        _charts.loading()
        val chartList = getCharts()
        _charts.value = Resource.Success(chartList)
    }

    private fun loadGenres() = uiCoroutine {
        _genres.loading()
        val chartList = getGenres()
        _genres.value = Resource.Success(chartList)
    }

    private fun loadArtists(countryCode: String) = uiCoroutine {
        if (!_artists.hasItems() && !_artists.isLoading()) {
            _artists.loading()
            val result = getTopArtists(countryCode)
            _artists.value = result.asResource()
        }
    }
}

