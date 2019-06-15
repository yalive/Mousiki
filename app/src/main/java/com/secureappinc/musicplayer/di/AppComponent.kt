package com.secureappinc.musicplayer.di

import android.content.Context
import com.google.gson.Gson
import com.secureappinc.musicplayer.ui.MainViewModel
import com.secureappinc.musicplayer.ui.artists.artistdetail.detailplaylist.PlaylistVideosViewModel
import com.secureappinc.musicplayer.ui.artists.artistdetail.playlists.ArtistPlaylistsViewModel
import com.secureappinc.musicplayer.ui.artists.artistdetail.videos.ArtistVideosViewModel
import com.secureappinc.musicplayer.ui.artists.list.ArtistListViewModel
import com.secureappinc.musicplayer.ui.charts.ChartsViewModel
import com.secureappinc.musicplayer.ui.genres.detailgenre.DetailGenreViewModel
import com.secureappinc.musicplayer.ui.genres.detailgenre.playlists.GenrePlaylistsViewModel
import com.secureappinc.musicplayer.ui.genres.detailgenre.videos.GenreVideosViewModel
import com.secureappinc.musicplayer.ui.genres.list.GenresViewModel
import com.secureappinc.musicplayer.ui.home.HomeViewModel
import com.secureappinc.musicplayer.ui.newrelease.NewReleaseViewModel
import com.secureappinc.musicplayer.ui.searchyoutube.SearchYoutubeViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-09.
 ***************************************
 */

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): AppComponent
    }

    val mainViewModel: MainViewModel

    val homeViewModel: HomeViewModel

    val newReleaseViewModel: NewReleaseViewModel

    val chartsViewModel: ChartsViewModel

    val detailGenreViewModel: DetailGenreViewModel

    val genreVideosViewModel: GenreVideosViewModel

    val genrePlaylistsViewModel: GenrePlaylistsViewModel

    val artistListViewModel: ArtistListViewModel

    val artistVideosViewModel: ArtistVideosViewModel

    val artistPlaylistsViewModel: ArtistPlaylistsViewModel

    val playlistVideosViewModel: PlaylistVideosViewModel

    val searchYoutubeViewModel: SearchYoutubeViewModel

    val genresViewModel: GenresViewModel

    val gson: Gson
}