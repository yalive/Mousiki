package com.cas.musicplayer.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cas.common.resource.*
import com.cas.common.viewmodel.BaseViewModel
import com.cas.musicplayer.ui.common.PlaySongDelegate
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem
import com.cas.musicplayer.ui.home.model.HeaderItem
import com.cas.musicplayer.ui.home.model.HomeItem
import com.cas.musicplayer.ui.home.model.toDisplayedVideoItem
import com.cas.musicplayer.utils.uiCoroutine
import com.mousiki.shared.data.config.RemoteAppConfig
import com.mousiki.shared.data.models.Artist
import com.mousiki.shared.data.models.toTrack
import com.mousiki.shared.data.repository.HomeRepository
import com.mousiki.shared.domain.models.GenreMusic
import com.mousiki.shared.domain.models.MusicTrack
import com.mousiki.shared.domain.result.Result
import com.mousiki.shared.domain.result.map
import com.mousiki.shared.domain.usecase.artist.GetCountryArtistsUseCase
import com.mousiki.shared.domain.usecase.chart.GetUserRelevantChartsUseCase
import com.mousiki.shared.domain.usecase.genre.GetGenresUseCase
import com.mousiki.shared.domain.usecase.song.GetPopularSongsUseCase
import com.mousiki.shared.preference.PreferencesHelper
import com.mousiki.shared.utils.AnalyticsApi
import com.mousiki.shared.utils.ConnectivityChecker
import com.mousiki.shared.utils.getCurrentLocale
import com.mousiki.shared.utils.logEvent
import kotlinx.coroutines.launch

/**
 **********************************
 * Created by Abdelhadi on 4/13/19.
 **********************************
 */
class HomeViewModel(
    private val getNewReleasedSongs: GetPopularSongsUseCase,
    private val getCountryArtists: GetCountryArtistsUseCase,
    private val getUserRelevantCharts: GetUserRelevantChartsUseCase,
    private val getGenres: GetGenresUseCase,
    private val analytics: AnalyticsApi,
    private val connectivityState: ConnectivityChecker,
    private val homeRepository: HomeRepository,
    private val appConfig: RemoteAppConfig,
    private val preferencesHelper: PreferencesHelper,
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
                        it.playlists.orEmpty().isNotEmpty()
                    }.map {
                        HomeItem.CompactPlaylists(it.title.orEmpty(), it.playlists.orEmpty())
                    }

                    // Create simple playlists
                    val simplePlaylists = homeRS.simplePlaylists.filter {
                        it.playlists.orEmpty().isNotEmpty()
                    }.map {
                        HomeItem.SimplePlaylists(it.title.orEmpty(), it.playlists.orEmpty())
                    }

                    // Create videos lists
                    val videoLists = homeRS.videoLists.filter {
                        it.videos.orEmpty().isNotEmpty()
                    }.map {
                        HomeItem.VideoList(
                            it.title.orEmpty(),
                            it.videos.orEmpty().map { it.video.toTrack().toDisplayedVideoItem() })
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
                    loadArtists()
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
            analytics.logEvent(
                "cannot_load_trending_tracks",
                "isConnected" to connectivityState.isConnected(),
                "isConnectedBeforeCall" to connectedBefore,
                "local" to getCurrentLocale()
            )
        }
    }

    private fun loadGenres() = uiCoroutine {
        val chartList = getGenres().take(8)
        _genres.value = chartList
    }

    private fun loadArtists() = uiCoroutine {
        if (!_artists.hasItems() && !_artists.isLoading()) {
            _artists.loading()
            val result = getCountryArtists()
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
        loadArtists()
    }
}

