package com.cas.musicplayer.di

import com.cas.musicplayer.ui.MainViewModel
import com.cas.musicplayer.ui.artists.list.ArtistListViewModel
import com.cas.musicplayer.ui.bottomsheet.TrackOptionsViewModel
import com.cas.musicplayer.ui.common.ads.CommonAdsViewModel
import com.cas.musicplayer.ui.common.ads.RewardedAdDelegate
import com.cas.musicplayer.ui.genres.GenresViewModel
import com.cas.musicplayer.ui.local.albums.AlbumDetailsViewModel
import com.cas.musicplayer.ui.local.albums.LocalAlbumsViewModel
import com.cas.musicplayer.ui.local.artists.ArtistDetailsViewModel
import com.cas.musicplayer.ui.local.artists.LocalArtistsViewModel
import com.cas.musicplayer.ui.local.folders.FolderDetailsViewModel
import com.cas.musicplayer.ui.local.folders.FolderVideoDetailsViewModel
import com.cas.musicplayer.ui.local.folders.FoldersViewModel
import com.cas.musicplayer.ui.local.playlists.LocalPlaylistsViewModel
import com.cas.musicplayer.ui.local.repository.LocalSongsRepository
import com.cas.musicplayer.ui.local.repository.LocalVideosRepository
import com.cas.musicplayer.ui.local.songs.LocalSongsViewModel
import com.cas.musicplayer.ui.local.songs.settings.LocalSongsSettingsViewModel
import com.cas.musicplayer.ui.local.videos.LocalVideoViewModel
import com.cas.musicplayer.ui.local.videos.history.PlayedVideoViewModel
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
import com.mousiki.shared.domain.usecase.library.AddSongToFavouriteUseCase
import com.mousiki.shared.domain.usecase.library.RemoveSongFromFavouriteListUseCase
import com.mousiki.shared.domain.usecase.recent.AddTrackToRecentlyPlayedUseCase
import com.mousiki.shared.domain.usecase.recent.GetRecentlyPlayedSongsUseCase
import com.mousiki.shared.preference.PreferencesHelper
import com.mousiki.shared.ui.artist.songs.ArtistSongsViewModel
import com.mousiki.shared.ui.home.HomeViewModel
import com.mousiki.shared.ui.library.LibraryViewModel
import com.mousiki.shared.ui.playlist.PlaylistSongsViewModel
import com.mousiki.shared.utils.AnalyticsApi
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

object Injector : KoinComponent {

    val mainViewModel: MainViewModel
        get() = get()

    val adsViewModel: CommonAdsViewModel
        get() = get()

    val homeViewModel: HomeViewModel
        get() = get()

    val popularSongsViewModel: PopularSongsViewModel
        get() = get()

    val artistListViewModel: ArtistListViewModel
        get() = get()

    val artistVideosViewModel: ArtistSongsViewModel
        get() = get()

    val playlistVideosViewModel: PlaylistSongsViewModel
        get() = get()

    val searchYoutubeViewModel: SearchYoutubeViewModel
        get() = get()

    val genresViewModel: GenresViewModel
        get() = get()

    val libraryViewModel: LibraryViewModel
        get() = get()

    val trackOptionsViewModel: TrackOptionsViewModel
        get() = get()

    val playerViewModel: PlayerViewModel
        get() = get()

    val mainSearchViewModel: MainSearchViewModel
        get() = get()

    val emptyViewModel: EmptyViewModel
        get() = get()

    val settingsViewModel: SettingsViewModel
        get() = get()

    val addTrackToPlaylistViewModel: AddTrackToPlaylistViewModel
        get() = get()

    val customPlaylistSongsViewModel: CustomPlaylistSongsViewModel
        get() = get()

    val createPlaylistViewModel: CreatePlaylistViewModel
        get() = get()

    val removeSongFromFavouriteList: RemoveSongFromFavouriteListUseCase
        get() = get()

    val addSongToFavourite: AddSongToFavouriteUseCase
        get() = get()

    val getRecentlyPlayedSongs: GetRecentlyPlayedSongsUseCase
        get() = get()

    val queueViewModel: QueueViewModel
        get() = get()

    val addTrackToRecentlyPlayed: AddTrackToRecentlyPlayedUseCase
        get() = get()

    val preferencesHelper: PreferencesHelper
        get() = get()

    val analytics: AnalyticsApi
        get() = get()

    val rewardedAdDelegate: RewardedAdDelegate
        get() = get()

    val localSongsViewModel: LocalSongsViewModel
        get() = get()

    val localVideoViewModel: LocalVideoViewModel
        get() = get()

    val playedVideoViewModel: PlayedVideoViewModel
        get() = get()

    val localSongsSettingsViewModel: LocalSongsSettingsViewModel
        get() = get()

    val localVideosSettingsViewModel: LocalVideosSettingsViewModel
        get() = get()

    val localPlaylistViewModel: LocalPlaylistsViewModel
        get() = get()

    val localAlbumsViewModel: LocalAlbumsViewModel
        get() = get()

    val localArtistsViewModel: LocalArtistsViewModel
        get() = get()

    val foldersViewModel: FoldersViewModel
        get() = get()

    val albumDetailsViewModel: AlbumDetailsViewModel
        get() = get()

    val folderDetailsViewModel: FolderDetailsViewModel
        get() = get()

    val folderVideoDetailsViewModel: FolderVideoDetailsViewModel
        get() = get()

    val artistDetailsViewModel: ArtistDetailsViewModel
        get() = get()

    val localSongsRepository: LocalSongsRepository
        get() = get()

    val localVideosRepository: LocalVideosRepository
        get() = get()
}