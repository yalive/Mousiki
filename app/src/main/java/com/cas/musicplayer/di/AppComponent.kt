package com.cas.musicplayer.di

import android.content.Context
import com.google.gson.Gson
import com.cas.musicplayer.ui.MainViewModel
import com.cas.musicplayer.ui.artists.artistdetail.detailplaylist.PlaylistVideosViewModel
import com.cas.musicplayer.ui.artists.artistdetail.playlists.ArtistPlaylistsViewModel
import com.cas.musicplayer.ui.artists.artistdetail.videos.ArtistVideosViewModel
import com.cas.musicplayer.ui.artists.list.ArtistListViewModel
import com.cas.musicplayer.ui.charts.ChartsViewModel
import com.cas.musicplayer.ui.genres.detailgenre.DetailGenreViewModel
import com.cas.musicplayer.ui.genres.detailgenre.playlists.GenrePlaylistsViewModel
import com.cas.musicplayer.ui.genres.detailgenre.videos.GenreVideosViewModel
import com.cas.musicplayer.ui.genres.list.GenresViewModel
import com.cas.musicplayer.ui.home.HomeViewModel
import com.cas.musicplayer.ui.newrelease.NewReleaseViewModel
import com.cas.musicplayer.ui.searchyoutube.SearchYoutubeViewModel
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