package com.mousiki.shared.di

import com.mousiki.shared.data.datasource.LocalSongsDataSource
import com.mousiki.shared.data.datasource.RemoteSongsDataSource
import com.mousiki.shared.data.datasource.channel.ChannelSongsLocalDataSource
import com.mousiki.shared.data.datasource.channel.ChannelSongsRemoteDataSource
import com.mousiki.shared.data.datasource.playlist.PlaylistSongsLocalDataSource
import com.mousiki.shared.data.datasource.playlist.PlaylistSongsRemoteDataSource
import com.mousiki.shared.data.datasource.search.SearchLocalDataSource
import com.mousiki.shared.data.datasource.search.SearchRemoteDataSource
import com.mousiki.shared.data.remote.api.MousikiApi
import com.mousiki.shared.data.remote.api.MousikiApiImpl
import com.mousiki.shared.data.remote.mapper.*
import com.mousiki.shared.data.remote.runner.NetworkRunner
import com.mousiki.shared.data.repository.*
import com.mousiki.shared.domain.usecase.artist.GetAllArtistsUseCase
import com.mousiki.shared.domain.usecase.artist.GetAllCountryArtistsUseCase
import com.mousiki.shared.domain.usecase.artist.GetArtistSongsUseCase
import com.mousiki.shared.domain.usecase.artist.GetCountryArtistsUseCase
import com.mousiki.shared.domain.usecase.chart.GetUserRelevantChartsUseCase
import com.mousiki.shared.domain.usecase.chart.LoadChartLastThreeTracksUseCase
import com.mousiki.shared.domain.usecase.customplaylist.*
import com.mousiki.shared.domain.usecase.genre.GetGenresUseCase
import com.mousiki.shared.domain.usecase.library.*
import com.mousiki.shared.domain.usecase.recent.AddTrackToRecentlyPlayedUseCase
import com.mousiki.shared.domain.usecase.recent.GetRecentlyPlayedSongsFlowUseCase
import com.mousiki.shared.domain.usecase.recent.GetRecentlyPlayedSongsUseCase
import com.mousiki.shared.domain.usecase.search.*
import com.mousiki.shared.domain.usecase.song.GetFeaturedSongsUseCase
import com.mousiki.shared.domain.usecase.song.GetPlaylistVideosUseCase
import com.mousiki.shared.domain.usecase.song.GetPopularSongsUseCase
import com.mousiki.shared.ui.artist.songs.ArtistSongsViewModel
import com.mousiki.shared.ui.home.HomeViewModel
import com.mousiki.shared.ui.library.LibraryViewModel
import com.mousiki.shared.ui.playlist.PlaylistSongsViewModel
import kotlinx.serialization.json.Json
import org.koin.dsl.module


val networkModule = module {
    single<MousikiApi> { MousikiApiImpl(get()) }
    single { NetworkRunner() }
    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }
}

val repositoriesModule = module {
    factory { GenresRepository() }
    factory { PlaylistsRepository(get()) }
    factory { StatisticsRepository(get()) }
    single { SongsRepository(get(), get(), get(), get()) }
    single { ArtistsRepository(get(), get(), get(), get(), get()) }
    factory { YtbPlaylistRepository(get(), get()) }
    single { SearchRepository(get(), get(), get(), get(), get()) }
    single { HomeRepository(get(), get(), get(), get(), get()) }
    factory { ChartsRepository() }
    factory { AudioRepository(get(), get(), get()) }
}

val mappersModule = module {
    factory { YTBChannelToArtist() }
    factory { YTBChannelToChannel() }
    factory { YTBPlaylistItemToTrack() }
    factory { YTBPlaylistItemToVideoId() }
    factory { YTBPlaylistToPlaylist() }
    factory { YTBSearchResultToChannelId() }
    factory { YTBSearchResultToPlaylistId() }
    factory { YTBSearchResultToVideoId() }
    factory { YTBVideoToTrack() }
}

val dataSourcesModule = module {
    factory { LocalSongsDataSource(get(), get()) }
    factory { SearchLocalDataSource(get()) }
    factory { PlaylistSongsLocalDataSource(get()) }
    factory { ChannelSongsLocalDataSource(get()) }

    // Remote
    factory {
        SearchRemoteDataSource(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    factory { RemoteSongsDataSource(get(), get(), get(), get(), get(), get(), get(), get()) }
    factory {
        PlaylistSongsRemoteDataSource(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    factory { ChannelSongsRemoteDataSource(get(), get(), get(), get(), get(), get(), get(), get()) }
}

val useCasesModule = module {
    factory { GetAllArtistsUseCase(get()) }
    factory { GetAllCountryArtistsUseCase(get()) }
    factory { GetArtistSongsUseCase(get()) }
    factory { GetCountryArtistsUseCase(get()) }
    factory { GetUserRelevantChartsUseCase(get()) }
    factory { LoadChartLastThreeTracksUseCase(get()) }
    factory { AddTrackToCustomPlaylistUseCase(get()) }
    factory { CreateCustomPlaylistUseCase(get()) }
    factory { CustomPlaylistFirstYtbTrackUseCase(get()) }
    factory { DeleteTrackFromCustomPlaylistUseCase(get()) }
    factory { GetCustomPlaylistTracksFlowUseCase(get()) }
    factory { RemoveSongFromRecentlyPlayedUseCase(get()) }
    factory { GetLocalPlaylistsUseCase(get()) }
    factory { GetLocalPlaylistItemCountUseCase(get()) }
    factory { GetLocalPlaylistsFlowUseCase(get()) }
    factory { GetCustomPlaylistTracksUseCase(get()) }
    factory { RemoveCustomPlaylistUseCase(get()) }
    factory { GetGenresUseCase(get()) }
    factory { AddSongToFavouriteUseCase(get()) }
    factory { GetFavouriteTracksFlowUseCase(get()) }
    factory { GetFavouriteTracksUseCase(get()) }
    factory { GetHeavyTracksFlowUseCase(get()) }
    factory { GetHeavyTracksUseCase(get()) }
    factory { RemoveSongFromFavouriteListUseCase(get()) }
    factory { AddTrackToRecentlyPlayedUseCase(get()) }
    factory { GetRecentlyPlayedSongsFlowUseCase(get()) }
    factory { GetRecentlyPlayedSongsUseCase(get()) }
    factory { GetGoogleSearchSuggestionsUseCase(get()) }
    factory { GetRecentSearchQueriesUseCase(get()) }
    factory { SaveSearchQueryUseCase(get()) }
    factory { RemoveSearchQueryUseCase(get()) }
    factory { ClearSearchHistoryUseCase(get()) }
    factory { SearchSongsUseCase(get()) }
    factory { GetFeaturedSongsUseCase() }
    factory { GetPlaylistVideosUseCase(get()) }
    factory { GetPopularSongsUseCase(get()) }
}

val kmmViewModelsModule = module {
    factory { PlaylistSongsViewModel(get(), get(), get()) }
    factory {
        HomeViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    factory { LibraryViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    factory { ArtistSongsViewModel(get(), get(), get()) }
}