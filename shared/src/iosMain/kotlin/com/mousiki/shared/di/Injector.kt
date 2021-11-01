package com.mousiki.shared.di

import com.mousiki.shared.data.config.RemoteAppConfig
import com.mousiki.shared.data.repository.AudioRepository
import com.mousiki.shared.data.repository.HomeRepository
import com.mousiki.shared.domain.usecase.artist.GetCountryArtistsUseCase
import com.mousiki.shared.domain.usecase.customplaylist.*
import com.mousiki.shared.domain.usecase.genre.GetGenresUseCase
import com.mousiki.shared.domain.usecase.library.*
import com.mousiki.shared.domain.usecase.recent.AddTrackToRecentlyPlayedUseCase
import com.mousiki.shared.domain.usecase.recent.GetRecentlyPlayedSongsFlowUseCase
import com.mousiki.shared.domain.usecase.recent.GetRecentlyPlayedSongsUseCase
import com.mousiki.shared.domain.usecase.song.GetPopularSongsUseCase
import com.mousiki.shared.preference.PreferencesHelper
import com.mousiki.shared.ui.artist.songs.ArtistSongsViewModel
import com.mousiki.shared.ui.home.HomeViewModel
import com.mousiki.shared.ui.library.LibraryViewModel
import com.mousiki.shared.ui.playlist.PlaylistSongsViewModel
import com.mousiki.shared.ui.search.MainSearchViewModel
import com.mousiki.shared.ui.search.SearchYoutubeViewModel
import com.mousiki.shared.utils.Strings
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

object Injector : KoinComponent {

    val homeRepository: HomeRepository get() = get()

    /*######################## View Models  ########################*/
    val homeViewModel: HomeViewModel get() = get()
    val libraryViewModel: LibraryViewModel get() = get()
    val playlistSongsViewModel: PlaylistSongsViewModel get() = get()
    val artistSongsViewModel: ArtistSongsViewModel get() = get()
    val searchViewModel: SearchYoutubeViewModel get() = get()
    val mainSearchViewModel: MainSearchViewModel get() = get()

    /*######################## Helpers  ########################*/
    val strings: Strings get() = get()
    val audioRepository: AudioRepository get() = get()
    val appConfig: RemoteAppConfig get() = get()
    val preferencesHelper: PreferencesHelper get() = get()

    /*######################## Use cases  ########################*/
    val getCountryArtists: GetCountryArtistsUseCase get() = get()
    val getNewReleasedSongs: GetPopularSongsUseCase get() = get()
    val getGenres: GetGenresUseCase get() = get()
    val addSongToFavourite: AddSongToFavouriteUseCase get() = get()
    val removeSongFromFavourite: RemoveSongFromFavouriteListUseCase get() = get()
    val removeSongFromRecentlyPlayed: RemoveSongFromRecentlyPlayedUseCase get() = get()
    val addTrackToRecentlyPlayed: AddTrackToRecentlyPlayedUseCase get() = get()

    val getFavouriteTracks: GetFavouriteTracksUseCase get() = get()
    val getRecentlyPlayedSongs: GetRecentlyPlayedSongsUseCase get() = get()
    val getHeavyTracks: GetHeavyTracksUseCase get() = get()

    val getFavouriteTracksFlow: GetFavouriteTracksFlowUseCase get() = get()
    val getRecentlyPlayedSongsFlow: GetRecentlyPlayedSongsFlowUseCase get() = get()
    val getHeavyTracksFlow: GetHeavyTracksFlowUseCase get() = get()

    val addTrackToCustomPlaylist: AddTrackToCustomPlaylistUseCase get() = get()
    val createCustomPlaylist: CreateCustomPlaylistUseCase get() = get()
    val getCustomPlaylistFirstYtbTrack: CustomPlaylistFirstYtbTrackUseCase get() = get()
    val deleteTrackFromCustomPlaylist: DeleteTrackFromCustomPlaylistUseCase get() = get()
    val getCustomPlaylistTracks: GetCustomPlaylistTracksUseCase get() = get()
    val getLocalPlaylists: GetLocalPlaylistsUseCase get() = get()
    val removeCustomPlaylist: RemoveCustomPlaylistUseCase get() = get()
    val getCustomPlaylistTracksFlow: GetCustomPlaylistTracksFlowUseCase get() = get()
    val getLocalPlaylistItemCountFlow: GetLocalPlaylistItemCountFlowUseCase get() = get()
    val getLocalPlaylistsFlow: GetLocalPlaylistsFlowUseCase get() = get()
}