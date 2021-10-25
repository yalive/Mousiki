package com.mousiki.shared.di

import com.mousiki.shared.data.config.RemoteAppConfig
import com.mousiki.shared.data.repository.AudioRepository
import com.mousiki.shared.data.repository.HomeRepository
import com.mousiki.shared.domain.usecase.artist.GetCountryArtistsUseCase
import com.mousiki.shared.domain.usecase.genre.GetGenresUseCase
import com.mousiki.shared.domain.usecase.library.AddSongToFavouriteUseCase
import com.mousiki.shared.domain.usecase.library.RemoveSongFromFavouriteListUseCase
import com.mousiki.shared.domain.usecase.library.RemoveSongFromRecentlyPlayedUseCase
import com.mousiki.shared.domain.usecase.recent.AddTrackToRecentlyPlayedUseCase
import com.mousiki.shared.domain.usecase.song.GetPopularSongsUseCase
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

    /*######################## Use cases  ########################*/
    val getCountryArtists: GetCountryArtistsUseCase get() = get()
    val getNewReleasedSongs: GetPopularSongsUseCase get() = get()
    val getGenres: GetGenresUseCase get() = get()
    val addSongToFavourite: AddSongToFavouriteUseCase get() = get()
    val removeSongFromFavourite: RemoveSongFromFavouriteListUseCase get() = get()
    val removeSongFromRecentlyPlayed: RemoveSongFromRecentlyPlayedUseCase get() = get()
    val addTrackToRecentlyPlayed: AddTrackToRecentlyPlayedUseCase get() = get()
}