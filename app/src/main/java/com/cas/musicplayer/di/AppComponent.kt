package com.cas.musicplayer.di

import android.content.Context
import com.cas.musicplayer.ui.MainViewModel
import com.cas.musicplayer.ui.artists.list.ArtistListViewModel
import com.cas.musicplayer.ui.artists.songs.ArtistSongsViewModel
import com.cas.musicplayer.ui.bottompanel.BottomPanelViewModel
import com.cas.musicplayer.ui.bottompanel.SlideUpPlaylistViewModel
import com.cas.musicplayer.ui.bottomsheet.FavBottomSheetViewModel
import com.cas.musicplayer.ui.common.PlaySongDelegateModule
import com.cas.musicplayer.ui.favourite.FavouriteSongsViewModel
import com.cas.musicplayer.ui.genres.GenresViewModel
import com.cas.musicplayer.ui.home.HomeViewModel
import com.cas.musicplayer.ui.library.LibraryViewModel
import com.cas.musicplayer.ui.playlist.create.AddTrackToPlaylistViewModel
import com.cas.musicplayer.ui.playlist.create.CreatePlaylistViewModel
import com.cas.musicplayer.ui.playlist.songs.PlaylistSongsViewModel
import com.cas.musicplayer.ui.popular.PopularSongsViewModel
import com.cas.musicplayer.ui.searchyoutube.MainSearchViewModel
import com.cas.musicplayer.ui.searchyoutube.SearchYoutubeViewModel
import com.cas.musicplayer.ui.settings.SettingsViewModel
import com.cas.musicplayer.utils.EmptyViewModel
import com.google.gson.Gson
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
        PlaySongDelegateModule::class
    ]
)
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): AppComponent
    }

    val mainViewModel: MainViewModel

    val homeViewModel: HomeViewModel

    val popularSongsViewModel: PopularSongsViewModel

    val artistListViewModel: ArtistListViewModel

    val artistVideosViewModel: ArtistSongsViewModel

    val playlistVideosViewModelFactory: PlaylistSongsViewModel.Factory

    val searchYoutubeViewModel: SearchYoutubeViewModel

    val genresViewModel: GenresViewModel

    val libraryViewModel: LibraryViewModel

    val favouriteTracksViewModel: FavouriteSongsViewModel

    val favBottomSheetViewModel: FavBottomSheetViewModel

    val bottomPanelViewModel: BottomPanelViewModel

    val slideUpPlaylistViewModel: SlideUpPlaylistViewModel

    val mainSearchViewModel: MainSearchViewModel

    val emptyViewModel: EmptyViewModel

    val settingsViewModel: SettingsViewModel

    val addTrackToPlaylistViewModelFactory: AddTrackToPlaylistViewModel.Factory

    val createPlaylistViewModel: CreatePlaylistViewModel

    val gson: Gson
}