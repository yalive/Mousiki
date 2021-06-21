package com.mousiki.shared.di

import com.mousiki.shared.data.config.RemoteAppConfig
import com.mousiki.shared.data.repository.AudioRepository
import com.mousiki.shared.data.repository.HomeRepository
import com.mousiki.shared.domain.usecase.artist.GetCountryArtistsUseCase
import com.mousiki.shared.domain.usecase.genre.GetGenresUseCase
import com.mousiki.shared.domain.usecase.song.GetPopularSongsUseCase
import com.mousiki.shared.ui.artist.songs.ArtistSongsViewModel
import com.mousiki.shared.ui.home.HomeViewModel
import com.mousiki.shared.ui.library.LibraryViewModel
import com.mousiki.shared.ui.playlist.PlaylistSongsViewModel
import com.mousiki.shared.utils.Strings
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

object Injector : KoinComponent {
    val homeRepository: HomeRepository
        get() = get()

    val getCountryArtists: GetCountryArtistsUseCase
        get() = get()

    val getNewReleasedSongs: GetPopularSongsUseCase
        get() = get()

    val getGenres: GetGenresUseCase
        get() = get()

    val appConfig: RemoteAppConfig
        get() = get()

    // View Models
    val homeViewModel: HomeViewModel
        get() = get()

    val libraryViewModel: LibraryViewModel
        get() = get()

    val playlistSongsViewModel: PlaylistSongsViewModel
        get() = get()

    val artistSongsViewModel: ArtistSongsViewModel
        get() = get()

    val strings: Strings
        get() = get()

    val audioRepository: AudioRepository
        get() = get()
}