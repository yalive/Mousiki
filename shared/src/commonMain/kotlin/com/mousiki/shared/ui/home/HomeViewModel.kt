package com.mousiki.shared.ui.home

import com.mousiki.shared.ads.FacebookAdsDelegate
import com.mousiki.shared.ads.GetListAdsDelegate
import com.mousiki.shared.data.config.RemoteAppConfig
import com.mousiki.shared.data.models.toTrack
import com.mousiki.shared.data.repository.HomeRepository
import com.mousiki.shared.data.repository.LocalTrackMapper
import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.domain.models.DisplayedVideoItem
import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.domain.models.toDisplayedVideoItem
import com.mousiki.shared.domain.result.Result
import com.mousiki.shared.domain.result.map
import com.mousiki.shared.domain.usecase.artist.GetCountryArtistsUseCase
import com.mousiki.shared.domain.usecase.genre.GetGenresUseCase
import com.mousiki.shared.domain.usecase.library.GetFavouriteTracksFlowUseCase
import com.mousiki.shared.domain.usecase.recent.GetRecentlyPlayedSongsFlowUseCase
import com.mousiki.shared.domain.usecase.song.GetPopularSongsUseCase
import com.mousiki.shared.player.PlaySongDelegate
import com.mousiki.shared.player.updateCurrentPlaying
import com.mousiki.shared.ui.base.BaseViewModel
import com.mousiki.shared.ui.home.model.HeaderItem
import com.mousiki.shared.ui.home.model.HomeItem
import com.mousiki.shared.ui.home.model.MousikiTopBarItem
import com.mousiki.shared.ui.resource.Resource
import com.mousiki.shared.ui.resource.asResource
import com.mousiki.shared.utils.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 **********************************
 * Created by Abdelhadi on 4/13/19.
 **********************************
 */
class HomeViewModel(
    private val getNewReleasedSongs: GetPopularSongsUseCase,
    private val getCountryArtists: GetCountryArtistsUseCase,
    private val localTrackMapper: LocalTrackMapper,
    private val getGenres: GetGenresUseCase,
    private val analytics: AnalyticsApi,
    private val connectivityState: ConnectivityChecker,
    private val homeRepository: HomeRepository,
    private val appConfig: RemoteAppConfig,
    private val getRecentlyPlayedSongs: GetRecentlyPlayedSongsFlowUseCase,
    private val getFavouriteTracks: GetFavouriteTracksFlowUseCase,
    playSongDelegate: PlaySongDelegate,
    facebookAdsDelegate: FacebookAdsDelegate,
    getListAdsDelegate: GetListAdsDelegate
) : BaseViewModel(), PlaySongDelegate by playSongDelegate,
    FacebookAdsDelegate by facebookAdsDelegate,
    GetListAdsDelegate by getListAdsDelegate {

    private val _homeItems = MutableStateFlow<List<DisplayableItem>?>(null)
    val homeItems: StateFlow<List<DisplayableItem>?> = _homeItems

    init {
        getHome()
    }

    fun sayHey() {
        println("Hey from KMM")
    }

    private fun getHome() = scope.launch {
        appConfig.awaitActivation()
        if (appConfig.newHomeEnabled()) loadNewHome() else showOldHome()
    }

    private suspend fun loadNewHome() {
        when (val result = homeRepository.getHome()) {
            is Result.Success -> {
                val homeRS = result.data
                val items = mutableListOf<DisplayableItem>()

                items.add(MousikiTopBarItem)
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
                        it.videos.orEmpty()
                            .map { it.video.toTrack(it.owner).toDisplayedVideoItem() })
                }

                // Create promos
                val promos = HomeItem.VideoList(
                    "Trending videos",
                    items = homeRS.promos.map { it.video.toTrack(it.owner).toDisplayedVideoItem() }
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
                observeRecent()
                observeFavourites()
                prepareAds()
            }
            is Result.Error -> showOldHome()
        }
    }

    fun onClickTrack(track: Track, queue: List<Track>) = scope.launch {
        playTrackFromQueue(track, queue)
    }

    fun onClickRetryNewRelease() {
        loadTrending()
    }

    private fun observeRecent() = scope.launch {
        getRecentlyPlayedSongs(300).collect { tracks ->
            if (tracks.isEmpty()) {
                removeItem { it is HomeItem.Recent }
                return@collect
            }
            val recentTracks = localTrackMapper.mapTracks(tracks)
                .map { it.toDisplayedVideoItem(this@HomeViewModel) }
            updateOrAddItem(HomeItem.Recent(recentTracks), 1, where = { it is HomeItem.Recent })
        }
    }

    private fun observeFavourites() = scope.launch {
        getFavouriteTracks(100).collect { tracks ->
            if (tracks.isEmpty()) {
                removeItem { it is HomeItem.Favourite }
                return@collect
            }
            val recentTracks = localTrackMapper.mapTracks(tracks)
                .map { it.toDisplayedVideoItem(this@HomeViewModel) }

            val recentPosition = _homeItems.value?.indexOfFirst { it is HomeItem.Recent } ?: -1

            updateOrAddItem(
                item = HomeItem.Favourite(recentTracks),
                addAt = if (recentPosition == -1) 1 else (recentPosition + 1),
                where = { it is HomeItem.Favourite }
            )
        }
    }

    private fun loadTrending() = scope.launch {
        val connectedBefore = connectivityState.isConnected()
        updateItem(HomeItem.PopularsItem(Resource.Loading), where = { it is HomeItem.PopularsItem })
        val result = getNewReleasedSongs(max = 15)
        val resource = result.map { tracks ->
            tracks.map { it.toDisplayedVideoItem() }
        }.asResource()
        updateItem(HomeItem.PopularsItem(resource), where = { it is HomeItem.PopularsItem })
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
        val genreList = getGenres().take(8)
        val genreItem = HomeItem.GenreItem(genreList)
        updateItem(genreItem, where = { it is HomeItem.GenreItem })
    }

    private fun loadArtists() = scope.launch {
        val result = getCountryArtists()
        if (result is Result.Success) {
            val artistsItem = HomeItem.ArtistItem(result.data)
            updateItem(artistsItem, where = { it is HomeItem.ArtistItem })
        }
    }

    private fun showOldHome() {
        val items = mutableListOf<DisplayableItem>()
        items.add(MousikiTopBarItem)
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
        observeRecent()
        observeFavourites()
    }

    private suspend fun prepareAds() {
        if (!appConfig.homeNativeAdsEnabled()) return
        awaitLoadAds()
        // 1 - Load Facebook native ads (3 ads)
        val ads = getNativeAds(3)
        if (ads.isEmpty()) return

        // 2 - Insert ads in specific positions
        val homeListItems = _homeItems.value?.toMutableList() ?: return
        homeListItems.add(2, ads[0])
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
        _homeItems.value = homeListItems
    }

    private fun updateOrAddItem(
        item: DisplayableItem,
        addAt: Int,
        where: (DisplayableItem) -> Boolean
    ) {
        val homeListItems = _homeItems.value?.toMutableList() ?: return
        val index = homeListItems.indexOfFirst { where(it) }
        if (index == -1) {
            homeListItems.add(addAt, item)
        } else {
            homeListItems[index] = item
        }
        _homeItems.value = homeListItems
    }

    private fun removeItem(where: (DisplayableItem) -> Boolean) {
        val homeListItems = _homeItems.value?.toMutableList() ?: return
        if (homeListItems.removeAll(where)) {
            _homeItems.value = homeListItems
        }
    }

    private fun updateItem(item: DisplayableItem, where: (DisplayableItem) -> Boolean) {
        val homeListItems = _homeItems.value?.toMutableList() ?: return
        val index = homeListItems.indexOfFirst { where(it) }
        homeListItems[index] = item
        _homeItems.value = homeListItems
    }

    fun onPlaybackStateChanged() {

        // Update recent
        scope.launch {
            val tracks = _homeItems.value?.filterIsInstance<HomeItem.Recent>()
                ?.firstOrNull()?.tracks ?: return@launch
            val recentTracks = updateCurrentPlaying(tracks).filterIsInstance<DisplayedVideoItem>()
            updateItem(HomeItem.Recent(recentTracks), where = { it is HomeItem.Recent })
        }

        // Update favourites
        scope.launch {
            val tracks = _homeItems.value?.filterIsInstance<HomeItem.Favourite>()
                ?.firstOrNull()?.tracks ?: return@launch
            val favTracks = updateCurrentPlaying(tracks).filterIsInstance<DisplayedVideoItem>()
            updateItem(HomeItem.Favourite(favTracks), where = { it is HomeItem.Favourite })
        }

        // Update new release
        scope.launch {
            val resource = _homeItems.value?.filterIsInstance<HomeItem.PopularsItem>()
                ?.firstOrNull()?.resource ?: return@launch
            if (resource !is Resource.Success) return@launch
            val tracks = resource.data
            val updatedTracks = updateCurrentPlaying(tracks).filterIsInstance<DisplayedVideoItem>()
            val updatedResource = Resource.Success(updatedTracks)
            updateItem(
                HomeItem.PopularsItem(updatedResource),
                where = { it is HomeItem.PopularsItem })
        }

        // Update video list
        scope.launch {
            _homeItems.value = _homeItems.value?.map {
                if (it is HomeItem.VideoList) {
                    it.copy(items = updateCurrentPlaying(it.items).filterIsInstance<DisplayedVideoItem>())
                } else it
            }
        }
    }

    // For iOS
    val homeItemsFlow: CommonFlow<List<DisplayableItem>?>
        get() = homeItems.asCommonFlow()
}

