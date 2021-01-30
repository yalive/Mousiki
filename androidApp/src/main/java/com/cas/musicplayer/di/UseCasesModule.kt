package com.cas.musicplayer.di

import com.mousiki.shared.domain.usecase.artist.GetAllArtistsUseCase
import com.mousiki.shared.domain.usecase.artist.GetAllCountryArtistsUseCase
import com.mousiki.shared.domain.usecase.artist.GetArtistSongsUseCase
import com.mousiki.shared.domain.usecase.artist.GetCountryArtistsUseCase
import com.mousiki.shared.domain.usecase.channel.GetChannelPlaylistsUseCase
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
import com.mousiki.shared.data.repository.*
import dagger.Module
import dagger.Provides

@Module
object UseCasesModule {

    @JvmStatic
    @Provides
    fun providesGetAllArtistsUseCase(
        repository: ArtistsRepository
    ) = GetAllArtistsUseCase(repository)


    @JvmStatic
    @Provides
    fun providesGetAllCountryArtistsUseCase(
        repository: ArtistsRepository
    ) = GetAllCountryArtistsUseCase(repository)

    @JvmStatic
    @Provides
    fun providesGetArtistSongsUseCase(
        repository: ArtistsRepository
    ) = GetArtistSongsUseCase(repository)

    @JvmStatic
    @Provides
    fun providesGetCountryArtistsUseCase(
        repository: ArtistsRepository
    ) = GetCountryArtistsUseCase(repository)


    @JvmStatic
    @Provides
    fun providesGetChannelPlaylistsUseCase(
        repository: PlaylistRepository
    ) = GetChannelPlaylistsUseCase(repository)

    @JvmStatic
    @Provides
    fun providesGetUserRelevantChartsUseCase(
        repository: ChartsRepository
    ) = GetUserRelevantChartsUseCase(repository)

    @JvmStatic
    @Provides
    fun providesLoadChartLastThreeTracksUseCase(
        repository: ChartsRepository
    ) = LoadChartLastThreeTracksUseCase(repository)


    @JvmStatic
    @Provides
    fun providesAddTrackToCustomPlaylistUseCase(
        repository: CustomPlaylistsRepository
    ) = AddTrackToCustomPlaylistUseCase(repository)

    @JvmStatic
    @Provides
    fun providesDeleteTrackFromCustomPlaylistUseCase(
        repository: CustomPlaylistsRepository
    ) = DeleteTrackFromCustomPlaylistUseCase(repository)

    @JvmStatic
    @Provides
    fun providesGetCustomPlaylistsUseCase(
        repository: CustomPlaylistsRepository
    ) = GetCustomPlaylistsUseCase(repository)

    @JvmStatic
    @Provides
    fun providesGetCustomPlaylistTracksUseCase(
        repository: CustomPlaylistsRepository
    ) = GetCustomPlaylistTracksUseCase(repository)

    @JvmStatic
    @Provides
    fun providesRemoveCustomPlaylistUseCase(
        repository: CustomPlaylistsRepository
    ) = RemoveCustomPlaylistUseCase(repository)

    @JvmStatic
    @Provides
    fun providesGetGenresUseCase(
        repository: GenresRepository
    ) = GetGenresUseCase(repository)

    @JvmStatic
    @Provides
    fun providesAddSongToFavouriteUseCase(
        repository: SongsRepository
    ) = AddSongToFavouriteUseCase(repository)

    @JvmStatic
    @Provides
    fun providesGetFavouriteTracksFlowUseCase(
        repository: SongsRepository
    ) = GetFavouriteTracksFlowUseCase(repository)

    @JvmStatic
    @Provides
    fun providesGetFavouriteTracksUseCase(
        repository: SongsRepository
    ) = GetFavouriteTracksUseCase(repository)

    @JvmStatic
    @Provides
    fun providesGetHeavyTracksUseCase(
        repository: StatisticsRepository
    ) = GetHeavyTracksUseCase(repository)

    @JvmStatic
    @Provides
    fun providesRemoveSongFromFavouriteListUseCase(
        repository: SongsRepository
    ) = RemoveSongFromFavouriteListUseCase(repository)

    @JvmStatic
    @Provides
    fun providesAddTrackToRecentlyPlayedUseCase(
        repository: StatisticsRepository
    ) = AddTrackToRecentlyPlayedUseCase(repository)

    @JvmStatic
    @Provides
    fun providesGetRecentlyPlayedSongsFlowUseCase(
        repository: StatisticsRepository
    ) = GetRecentlyPlayedSongsFlowUseCase(repository)

    @JvmStatic
    @Provides
    fun providesGetRecentlyPlayedSongsUseCase(
        repository: StatisticsRepository
    ) = GetRecentlyPlayedSongsUseCase(repository)

    @JvmStatic
    @Provides
    fun providesGetGoogleSearchSuggestionsUseCase(
        repository: SearchRepository
    ) = GetGoogleSearchSuggestionsUseCase(repository)

    @JvmStatic
    @Provides
    fun providesGetRecentSearchQueriesUseCase(
        repository: SearchRepository
    ) = GetRecentSearchQueriesUseCase(repository)

    @JvmStatic
    @Provides
    fun providesSaveSearchQueryUseCase(
        repository: SearchRepository
    ) = SaveSearchQueryUseCase(repository)

    @JvmStatic
    @Provides
    fun providesSearchChannelsUseCase(
        repository: SearchRepository
    ) = SearchChannelsUseCase(repository)

    @JvmStatic
    @Provides
    fun providesSearchPlaylistsUseCase(
        repository: SearchRepository
    ) = SearchPlaylistsUseCase(repository)

    @JvmStatic
    @Provides
    fun providesSearchSongsUseCase(
        repository: SearchRepository
    ) = SearchSongsUseCase(repository)

    @JvmStatic
    @Provides
    fun providesGetFeaturedSongsUseCase(
    ) = GetFeaturedSongsUseCase()

    @JvmStatic
    @Provides
    fun providesGetPlaylistVideosUseCase(
        repository: PlaylistRepository
    ) = GetPlaylistVideosUseCase(repository)

    @JvmStatic
    @Provides
    fun providesGetPopularSongsUseCase(
        repository: SongsRepository
    ) = GetPopularSongsUseCase(repository)

}