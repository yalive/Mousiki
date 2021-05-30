package com.mousiki.shared.ui.home

import com.mousiki.shared.ads.FacebookAdsDelegate
import com.mousiki.shared.data.config.RemoteAppConfig
import com.mousiki.shared.data.models.Artist
import com.mousiki.shared.data.models.toTrack
import com.mousiki.shared.data.repository.HomeRepository
import com.mousiki.shared.domain.models.*
import com.mousiki.shared.domain.result.Result
import com.mousiki.shared.domain.result.map
import com.mousiki.shared.domain.usecase.artist.GetCountryArtistsUseCase
import com.mousiki.shared.domain.usecase.chart.GetUserRelevantChartsUseCase
import com.mousiki.shared.domain.usecase.genre.GetGenresUseCase
import com.mousiki.shared.domain.usecase.song.GetPopularSongsUseCase
import com.mousiki.shared.player.PlaySongDelegate
import com.mousiki.shared.preference.PreferencesHelper
import com.mousiki.shared.ui.base.BaseViewModel
import com.mousiki.shared.ui.home.model.HeaderItem
import com.mousiki.shared.ui.home.model.HomeItem
import com.mousiki.shared.ui.resource.*
import com.mousiki.shared.utils.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    playSongDelegate: PlaySongDelegate,
    facebookAdsDelegate: FacebookAdsDelegate,
) : BaseViewModel(), PlaySongDelegate by playSongDelegate,
    FacebookAdsDelegate by facebookAdsDelegate {

    private val _newReleases =
        MutableStateFlow<Resource<List<DisplayedVideoItem>>?>(null)
    val newReleases: StateFlow<Resource<List<DisplayedVideoItem>>?> = _newReleases

    private val _genres = MutableStateFlow<List<GenreMusic>?>(null)
    val genres: StateFlow<List<GenreMusic>?> = _genres

    private val _artists = MutableStateFlow<Resource<List<Artist>>?>(null)
    val artists: StateFlow<Resource<List<Artist>>?> = _artists

    private val _homeItems = MutableStateFlow<List<HomeItem>?>(null)
    val homeItems: StateFlow<List<HomeItem>?> = _homeItems

    init {
        getHome()
    }

    private fun getHome() = scope.launch {
        appConfig.awaitActivation()
        if (appConfig.newHomeEnabled()) loadNewHome() else showOldHome()
    }

    private suspend fun loadNewHome() {
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
                prepareAds()
            }
            is Result.Error -> showOldHome()
        }
    }

    fun onClickTrack(track: MusicTrack, queue: List<MusicTrack>) = scope.launch {
        playTrackFromQueue(track, queue)
    }

    fun onClickRetryNewRelease() {
        loadTrending()
    }

    private fun loadTrending() = scope.launch {
        if (_newReleases.hasItems() || _newReleases.isLoading()) {
            return@launch
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

    private fun loadGenres() = scope.launch {
        val chartList = getGenres().take(8)
        _genres.value = chartList
    }

    private fun loadArtists() = scope.launch {
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

    private suspend fun prepareAds() {
        // 1 - Load Facebook native ads (3 ads)
        val ads = getHomeFacebookNativeAds(3)
        if (ads.isEmpty()) return

        // 2 - Insert ads in specific positions
        val homeListItems = _homeItems.value?.toMutableList() ?: return
        homeListItems.add(1, ads[0])
        if (ads.size > 1) {
            // Above genres
            val index = homeListItems.indexOfFirst { it is HeaderItem.GenresHeader }
            if (index != -1) {
                homeListItems.add(index - 1, ads[1])
            }
        }

        if (ads.size > 2) {
            // Above Artists
            val index = homeListItems.size - 2
            homeListItems.add(index, ads[2])
        }

        // 3 - Notify Observer

        // Get current state of: Artists, new release and genres (Next: Use single observable object)
        val popularItem = _homeItems.value?.filterIsInstance<HomeItem.PopularsItem>()?.firstOrNull()
        val newRelease = popularItem?.copy(resource = _newReleases.value!!)
        val indexNewRelease = homeListItems.indexOfFirst { it is HomeItem.PopularsItem }
        homeListItems[indexNewRelease] = newRelease!!

        // Genres
        val genreItem = _homeItems.value?.filterIsInstance<HomeItem.GenreItem>()?.firstOrNull()
        val updatedGenre = genreItem?.copy(genres = _genres.value!!)
        val indexGenres = homeListItems.indexOfFirst { it is HomeItem.GenreItem }
        homeListItems[indexGenres] = updatedGenre!!

        // Artists
        val currentArtistsList = (_artists.value as? Resource.Success<List<Artist>>)?.data.orEmpty()
        val artistsItem = _homeItems.value?.filterIsInstance<HomeItem.ArtistItem>()?.firstOrNull()
        val updatedArtists = artistsItem?.copy(artists = currentArtistsList)
        val indexArtists = homeListItems.indexOfFirst { it is HomeItem.ArtistItem }
        homeListItems[indexArtists] = updatedArtists!!

        _homeItems.value = homeListItems
    }

    // For iOS
    val newReleasesFlow: CommonFlow<Resource<List<DisplayedVideoItem>>?>
        get() = newReleases.asCommonFlow()

    val genresFlow: CommonFlow<List<GenreMusic>?>
        get() = genres.asCommonFlow()

    val artistsFlow: CommonFlow<Resource<List<Artist>>?>
        get() = artists.asCommonFlow()

    val homeItemsFlow: CommonFlow<List<HomeItem>?>
        get() = homeItems.asCommonFlow()
}

