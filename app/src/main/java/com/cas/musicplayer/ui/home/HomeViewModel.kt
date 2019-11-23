package com.cas.musicplayer.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cas.common.resource.Resource
import com.cas.common.resource.hasItems
import com.cas.common.resource.isLoading
import com.cas.common.resource.loading
import com.cas.common.result.asResource
import com.cas.common.result.map
import com.cas.common.viewmodel.BaseViewModel
import com.cas.musicplayer.data.remote.models.Artist
import com.cas.musicplayer.domain.model.ChartModel
import com.cas.musicplayer.domain.model.GenreMusic
import com.cas.musicplayer.domain.usecase.artist.GetCountryArtistsUseCase
import com.cas.musicplayer.domain.usecase.chart.GetChartsUseCase
import com.cas.musicplayer.domain.usecase.genre.GetGenresUseCase
import com.cas.musicplayer.domain.usecase.song.GetPopularSongsUseCase
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem
import com.cas.musicplayer.ui.home.model.toDisplayedVideoItem
import com.cas.musicplayer.utils.getCurrentLocale
import com.cas.musicplayer.utils.uiCoroutine
import javax.inject.Inject

/**
 **********************************
 * Created by Abdelhadi on 4/13/19.
 **********************************
 */
class HomeViewModel @Inject constructor(
    private val getNewReleasedSongs: GetPopularSongsUseCase,
    private val getCountryArtists: GetCountryArtistsUseCase,
    private val getCharts: GetChartsUseCase,
    private val getGenres: GetGenresUseCase
) : BaseViewModel() {

    private val _newReleases = MutableLiveData<Resource<List<DisplayedVideoItem>>>()
    val newReleases: LiveData<Resource<List<DisplayedVideoItem>>>
        get() = _newReleases

    private val _charts = MutableLiveData<List<ChartModel>>()
    val charts: LiveData<List<ChartModel>>
        get() = _charts

    private val _genres = MutableLiveData<List<GenreMusic>>()
    val genres: LiveData<List<GenreMusic>>
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
        if (_newReleases.hasItems() || _newReleases.isLoading()) {
            return@uiCoroutine
        }
        _newReleases.loading()
        val result = getNewReleasedSongs(max = 25)
        _newReleases.value = result.map { tracks ->
            tracks.map { it.toDisplayedVideoItem() }
        }.asResource()
    }

    private fun loadCharts() = uiCoroutine {
        val chartList = getCharts(max = 6).shuffled()
        _charts.value = chartList
    }

    private fun loadGenres() = uiCoroutine {
        val chartList = getGenres().take(9)
        _genres.value = chartList
    }

    private fun loadArtists(countryCode: String) = uiCoroutine {
        if (!_artists.hasItems() && !_artists.isLoading()) {
            _artists.loading()
            val result = getCountryArtists(countryCode)
            _artists.value = result.asResource()
        }
    }
}

