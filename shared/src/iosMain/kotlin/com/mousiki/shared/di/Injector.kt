package com.mousiki.shared.di

import com.mousiki.shared.data.config.RemoteAppConfig
import com.mousiki.shared.data.repository.HomeRepository
import com.mousiki.shared.domain.usecase.artist.GetCountryArtistsUseCase
import com.mousiki.shared.domain.usecase.genre.GetGenresUseCase
import com.mousiki.shared.domain.usecase.song.GetPopularSongsUseCase
import org.koin.core.KoinComponent
import org.koin.core.get

object Injector : KoinComponent {
    val homeRepository: HomeRepository
        get() = get()

    val appConfig: RemoteAppConfig
        get() = get()

    val getCountryArtists: GetCountryArtistsUseCase
        get() = get()

    val getNewReleasedSongs: GetPopularSongsUseCase
        get() = get()

    val getGenres: GetGenresUseCase
        get() = get()
}