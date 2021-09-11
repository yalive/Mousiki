package com.cas.musicplayer.di

import com.cas.musicplayer.ui.MainViewModel
import com.cas.musicplayer.ui.artists.list.ArtistListViewModel
import com.cas.musicplayer.ui.bottomsheet.TrackOptionsViewModel
import com.cas.musicplayer.ui.common.ads.CommonAdsViewModel
import com.cas.musicplayer.ui.genres.GenresViewModel
import com.cas.musicplayer.ui.local.albums.AlbumDetailsViewModel
import com.cas.musicplayer.ui.local.albums.LocalAlbumsViewModel
import com.cas.musicplayer.ui.local.artists.ArtistDetailsViewModel
import com.cas.musicplayer.ui.local.artists.LocalArtistsViewModel
import com.cas.musicplayer.ui.local.folders.FolderDetailsViewModel
import com.cas.musicplayer.ui.local.folders.FolderVideoDetailsViewModel
import com.cas.musicplayer.ui.local.folders.FoldersViewModel
import com.cas.musicplayer.ui.local.playlists.LocalPlaylistsViewModel
import com.cas.musicplayer.ui.local.songs.LocalSongsViewModel
import com.cas.musicplayer.ui.local.songs.settings.LocalSongsSettingsViewModel
import com.cas.musicplayer.ui.local.videos.LocalVideoViewModel
import com.cas.musicplayer.ui.local.videos.history.PlayedVideoViewModel
import com.cas.musicplayer.ui.local.videos.player.VideoPlayerViewModel
import com.cas.musicplayer.ui.local.videos.queue.VideosQueueViewModel
import com.cas.musicplayer.ui.local.videos.settings.LocalVideosSettingsViewModel
import com.cas.musicplayer.ui.player.PlayerViewModel
import com.cas.musicplayer.ui.player.queue.QueueViewModel
import com.cas.musicplayer.ui.playlist.create.CreatePlaylistViewModel
import com.cas.musicplayer.ui.playlist.custom.CustomPlaylistSongsViewModel
import com.cas.musicplayer.ui.playlist.select.AddTrackToPlaylistViewModel
import com.cas.musicplayer.ui.popular.PopularSongsViewModel
import com.cas.musicplayer.ui.searchyoutube.MainSearchViewModel
import com.cas.musicplayer.ui.searchyoutube.SearchYoutubeViewModel
import com.cas.musicplayer.ui.settings.SettingsViewModel
import com.cas.musicplayer.utils.EmptyViewModel
import org.koin.dsl.module

val viewModelsModule = module {
    factory { MainViewModel(get(), get(), get()) }
    factory { CommonAdsViewModel(get()) }
    factory { PopularSongsViewModel(get(), get(), get()) }
    factory { ArtistListViewModel(get()) }
    factory { SearchYoutubeViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
    factory { GenresViewModel(get()) }
    factory { TrackOptionsViewModel(get(), get(), get(), get()) }
    factory { PlayerViewModel(get(), get(), get(), get(), get()) }
    factory { MainSearchViewModel(get()) }
    factory { EmptyViewModel() }
    factory { SettingsViewModel() }
    factory { AddTrackToPlaylistViewModel(get(), get(), get(), get()) }
    factory { CustomPlaylistSongsViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
    factory { CreatePlaylistViewModel(get(), get()) }
    factory { QueueViewModel(get()) }
    factory { LocalSongsViewModel(get(), get()) }
    factory { LocalVideoViewModel(get(), get(), get()) }
    factory { PlayedVideoViewModel(get(), get(), get()) }
    factory { LocalSongsSettingsViewModel(get(), get()) }
    factory { LocalVideosSettingsViewModel(get()) }
    factory { LocalPlaylistsViewModel(get(), get(), get(), get(), get()) }
    factory { LocalAlbumsViewModel(get()) }
    factory { LocalArtistsViewModel(get()) }
    factory { FoldersViewModel(get()) }
    factory { AlbumDetailsViewModel(get(), get()) }
    factory { FolderDetailsViewModel(get(), get()) }
    factory { FolderVideoDetailsViewModel(get(), get(),get()) }
    factory { ArtistDetailsViewModel(get(), get()) }
    factory { VideosQueueViewModel(get()) }
    factory { VideoPlayerViewModel(get()) }
}