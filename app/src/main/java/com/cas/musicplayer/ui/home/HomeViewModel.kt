package com.cas.musicplayer.ui.home

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cas.common.connectivity.ConnectivityState
import com.cas.common.resource.Resource
import com.cas.common.resource.hasItems
import com.cas.common.resource.isLoading
import com.cas.common.resource.loading
import com.cas.common.result.Result
import com.cas.common.result.asResource
import com.cas.common.result.map
import com.cas.common.viewmodel.BaseViewModel
import com.cas.musicplayer.data.config.RemoteAppConfig
import com.cas.musicplayer.data.remote.models.Artist
import com.cas.musicplayer.data.remote.models.mousiki.toTrack
import com.cas.musicplayer.data.repositories.HomeRepository
import com.cas.musicplayer.domain.model.GenreMusic
import com.cas.musicplayer.domain.model.HeaderItem
import com.cas.musicplayer.domain.model.HomeItem
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.domain.usecase.artist.GetCountryArtistsUseCase
import com.cas.musicplayer.domain.usecase.chart.GetUserRelevantChartsUseCase
import com.cas.musicplayer.domain.usecase.genre.GetGenresUseCase
import com.cas.musicplayer.domain.usecase.song.GetPopularSongsUseCase
import com.cas.musicplayer.ui.common.PlaySongDelegate
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem
import com.cas.musicplayer.ui.home.model.toDisplayedVideoItem
import com.cas.musicplayer.utils.getCurrentLocale
import com.cas.musicplayer.utils.uiCoroutine
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 **********************************
 * Created by Abdelhadi on 4/13/19.
 **********************************
 */
class HomeViewModel @Inject constructor(
    private val getNewReleasedSongs: GetPopularSongsUseCase,
    private val getCountryArtists: GetCountryArtistsUseCase,
    private val getUserRelevantCharts: GetUserRelevantChartsUseCase,
    private val getGenres: GetGenresUseCase,
    private val analytics: FirebaseAnalytics,
    private val connectivityState: ConnectivityState,
    private val homeRepository: HomeRepository,
    private val appConfig: RemoteAppConfig,
    delegate: PlaySongDelegate
) : BaseViewModel(), PlaySongDelegate by delegate {

    private val _newReleases = MutableLiveData<Resource<List<DisplayedVideoItem>>>()
    val newReleases: LiveData<Resource<List<DisplayedVideoItem>>> = _newReleases

    private val _genres = MutableLiveData<List<GenreMusic>>()
    val genres: LiveData<List<GenreMusic>> = _genres

    private val _artists = MutableLiveData<Resource<List<Artist>>>()
    val artists: LiveData<Resource<List<Artist>>> = _artists

    private val _homeItems = MutableLiveData<List<HomeItem>>()
    val homeItems: LiveData<List<HomeItem>> = _homeItems

    init {
        getHome()
    }

    private fun getHome() = viewModelScope.launch {
        appConfig.awaitActivation()
        if (appConfig.newHomeEnabled()) {
            when (val result = homeRepository.getHome()) {
                is Result.Success -> {

                    val homeRS = result.data
                    val items = mutableListOf<HomeItem>()

                    // Create compact playlists
                    val compactPlaylists = homeRS.compactPlaylists.filter {
                        it.playlists.isNotEmpty()
                    }.map {
                        HomeItem.CompactPlaylists(it.title, it.playlists)
                    }

                    // Create simple playlists
                    val simplePlaylists = homeRS.simplePlaylists.filter {
                        it.playlists.isNotEmpty()
                    }.map {
                        HomeItem.SimplePlaylists(it.title, it.playlists)
                    }

                    // Create videos lists
                    val videoLists = homeRS.videoLists.filter {
                        it.videos.isNotEmpty()
                    }.map {
                        HomeItem.VideoList(
                            it.title,
                            it.videos.map { it.video.toTrack().toDisplayedVideoItem() })
                    }

                    // Create promos
                    val promos = HomeItem.VideoList(
                        "Trending videos",
                        items = homeRS.promos.map { it.video.toTrack().toDisplayedVideoItem() }
                    )

                    // Add items: this is the order in UI
                    if (promos.items.isNotEmpty()) {
                        items.add(promos)
                    }

                    items.add(HeaderItem.PopularsHeader(false))
                    items.add(HomeItem.PopularsItem(Resource.Loading))
                    items.addAll(compactPlaylists)
                    items.add(HeaderItem.GenresHeader)
                    items.add(HomeItem.GenreItem(emptyList()))
                    items.addAll(simplePlaylists)
                    items.addAll(videoLists)
                    items.add(HeaderItem.ArtistsHeader)
                    items.add(HomeItem.ArtistItem(emptyList()))
                    _homeItems.value = items
                    loadTrending()
                    loadGenres()
                    loadArtists(getCurrentLocale())
                }
                is Result.Error -> showOldHome()
            }
        } else {
            showOldHome()
        }
    }

    fun onClickTrack(track: MusicTrack, queue: List<MusicTrack>) = uiCoroutine {
        playTrackFromQueue(track, queue)
    }

    fun onClickRetryNewRelease() {
        loadTrending()
    }

    private fun loadTrending() = uiCoroutine {
        if (_newReleases.hasItems() || _newReleases.isLoading()) {
            return@uiCoroutine
        }
        val connectedBefore = connectivityState.isConnected()
        _newReleases.loading()
        val result = getNewReleasedSongs(max = 10)
        _newReleases.value = result.map { tracks ->
            tracks.map { it.toDisplayedVideoItem() }
        }.asResource()
        if (result is Result.Error) {
            val bundle = Bundle()
            bundle.putBoolean("isConnected", connectivityState.isConnected())
            bundle.putBoolean("isConnectedBeforeCall", connectedBefore)
            bundle.putString("local", getCurrentLocale())
            analytics.logEvent("cannot_load_trending_tracks", bundle)
        }
    }

    private fun loadGenres() = uiCoroutine {
        val chartList = getGenres().take(8)
        _genres.value = chartList
    }

    private fun loadArtists(countryCode: String) = uiCoroutine {
        if (!_artists.hasItems() && !_artists.isLoading()) {
            _artists.loading()
            val result = getCountryArtists(countryCode)
            _artists.value = result.asResource()
        }
    }

    private fun showOldHome() {
        val items = mutableListOf<HomeItem>()
        items.add(HeaderItem.PopularsHeader(false))
        items.add(HomeItem.PopularsItem(Resource.Loading))
        items.add(HeaderItem.GenresHeader)
        items.add(HomeItem.GenreItem(emptyList()))
        items.add(HeaderItem.ArtistsHeader)
        items.add(HomeItem.ArtistItem(emptyList()))

        _homeItems.value = items
        loadTrending()
        loadGenres()
        loadArtists(getCurrentLocale())
    }

}

