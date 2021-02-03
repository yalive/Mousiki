package com.cas.musicplayer.di

import com.cas.musicplayer.ui.MainViewModel
import com.cas.musicplayer.ui.artists.list.ArtistListViewModel
import com.cas.musicplayer.ui.artists.songs.ArtistSongsViewModel
import com.cas.musicplayer.ui.bottomsheet.TrackOptionsViewModel
import com.cas.musicplayer.ui.common.ads.CommonAdsViewModel
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
import org.koin.dsl.module

val viewModelsModule = module {
    factory { MainViewModel(get(), get(), get()) }
    factory { CommonAdsViewModel(get()) }
    factory { HomeViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    factory { PopularSongsViewModel(get(), get(), get()) }
    factory { ArtistListViewModel(get()) }
    factory { ArtistSongsViewModel(get(), get(), get()) }
    factory { PlaylistSongsViewModel(get(), get(), get()) }
    factory { SearchYoutubeViewModel(get(), get(), get(), get(), get(), get()) }
    factory { GenresViewModel(get()) }
    factory { LibraryViewModel(get(), get(), get(), get(), get(), get(), get()) }
    factory { FavouriteSongsViewModel(get(), get()) }
    factory { TrackOptionsViewModel(get(), get(), get()) }
    factory { PlayerViewModel(get(), get(), get(), get()) }
    factory { MainSearchViewModel(get()) }
    factory { EmptyViewModel() }
    factory { SettingsViewModel() }
    factory { AddTrackToPlaylistViewModel(get(), get(), get(), get()) }
    factory { CustomPlaylistSongsViewModel(get(), get(), get()) }
    factory { CreatePlaylistViewModel(get(), get()) }
    factory { QueueViewModel(get()) }
}