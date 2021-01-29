package com.cas.musicplayer.di

import android.content.Context
import com.cas.musicplayer.domain.usecase.library.AddSongToFavouriteUseCase
import com.cas.musicplayer.domain.usecase.library.RemoveSongFromFavouriteListUseCase
import com.cas.musicplayer.domain.usecase.recent.AddTrackToRecentlyPlayedUseCase
import com.cas.musicplayer.domain.usecase.recent.GetRecentlyPlayedSongsUseCase
import com.cas.musicplayer.ui.MainViewModel
import com.cas.musicplayer.ui.artists.list.ArtistListViewModel
import com.cas.musicplayer.ui.artists.songs.ArtistSongsViewModel
import com.cas.musicplayer.ui.bottomsheet.TrackOptionsViewModel
import com.cas.musicplayer.ui.common.PlaySongDelegateModule
import com.cas.musicplayer.ui.common.ads.AdsDelegateModule
import com.cas.musicplayer.ui.common.ads.CommonAdsViewModel
import com.cas.musicplayer.ui.common.ads.RewardedAdDelegate
import com.cas.musicplayer.ui.favourite.FavouriteSongsViewModel
import com.cas.musicplayer.ui.genres.GenresViewModel
import com.cas.musicplayer.ui.home.HomeViewModel
import com.cas.musicplayer.ui.library.LibraryViewModel
import com.cas.musicplayer.ui.player.PlayerViewModel
import com.cas.musicplayer.ui.player.queue.QueueViewModel
import com.cas.musicplayer.ui.playlist.create.AddTrackToPlaylistViewModel
import com.cas.musicplayer.ui.playlist.create.CreatePlaylistViewModel
import com.cas.musicplayer.ui.playlist.custom.CustomPlaylistSongsViewModel
import com.cas.musicplayer.ui.playlist.songs.PlaylistSongsViewModel
import com.cas.musicplayer.ui.popular.PopularSongsViewModel
import com.cas.musicplayer.ui.searchyoutube.MainSearchViewModel
import com.cas.musicplayer.ui.searchyoutube.SearchYoutubeViewModel
import com.cas.musicplayer.ui.settings.SettingsViewModel
import com.cas.musicplayer.utils.EmptyViewModel
import com.mousiki.shared.data.config.RemoteAppConfig
import com.mousiki.shared.utils.AnalyticsApi
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-09.
 ***************************************
 */

@Singleton
@Component(
    modules = [
        AppModule::class,
        PlaySongDelegateModule::class,
        AdsDelegateModule::class,
        FirebaseModule::class
    ]
)
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): AppComponent
    }

    val mainViewModel: MainViewModel

    val adsViewModel: CommonAdsViewModel

    val homeViewModel: HomeViewModel

    val popularSongsViewModel: PopularSongsViewModel

    val artistListViewModel: ArtistListViewModel

    val artistVideosViewModelFactory: ArtistSongsViewModel.Factory

    val playlistVideosViewModelFactory: PlaylistSongsViewModel.Factory

    val searchYoutubeViewModel: SearchYoutubeViewModel

    val genresViewModel: GenresViewModel

    val libraryViewModel: LibraryViewModel

    val favouriteTracksViewModel: FavouriteSongsViewModel

    val trackOptionsViewModel: TrackOptionsViewModel

    val playerViewModel: PlayerViewModel

    val mainSearchViewModel: MainSearchViewModel

    val emptyViewModel: EmptyViewModel

    val settingsViewModel: SettingsViewModel

    val addTrackToPlaylistViewModelFactory: AddTrackToPlaylistViewModel.Factory

    val customPlaylistSongsViewModelFactory: CustomPlaylistSongsViewModel.Factory

    val createPlaylistViewModel: CreatePlaylistViewModel

    val removeSongFromFavouriteList: RemoveSongFromFavouriteListUseCase

    val addSongToFavourite: AddSongToFavouriteUseCase

    val getRecentlyPlayedSongs: GetRecentlyPlayedSongsUseCase

    val analytics: AnalyticsApi

    val rewardedAdDelegate: RewardedAdDelegate

    val addTrackToRecentlyPlayed: AddTrackToRecentlyPlayedUseCase

    val appConfig: RemoteAppConfig

    val queueViewModel: QueueViewModel
}