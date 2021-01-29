package com.cas.musicplayer.di

import android.content.Context
import android.content.SharedPreferences
import com.cas.musicplayer.MousikiDb
import com.cas.musicplayer.data.datasource.RemoteSongsDataSource
import com.cas.musicplayer.data.repositories.SongsRepository
import com.cas.musicplayer.utils.ConnectivityState
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.mousiki.shared.data.config.RemoteAppConfig
import com.mousiki.shared.data.datasource.ArtistsLocalDataSource
import com.mousiki.shared.data.datasource.ArtistsRemoteDataSource
import com.mousiki.shared.data.datasource.LocalSongsDataSource
import com.mousiki.shared.data.datasource.channel.ChannelPlaylistsLocalDataSource
import com.mousiki.shared.data.datasource.channel.ChannelPlaylistsRemoteDataSource
import com.mousiki.shared.data.datasource.channel.ChannelSongsLocalDataSource
import com.mousiki.shared.data.datasource.playlist.PlaylistSongsLocalDataSource
import com.mousiki.shared.data.datasource.search.SearchLocalDataSource
import com.mousiki.shared.data.datasource.search.SearchRemoteDataSource
import com.mousiki.shared.data.remote.api.MousikiApi
import com.mousiki.shared.data.remote.api.MousikiApiImpl
import com.mousiki.shared.data.remote.mapper.*
import com.mousiki.shared.data.remote.runner.NetworkRunner
import com.mousiki.shared.data.repository.CustomPlaylistsRepository
import com.mousiki.shared.data.repository.GenresRepository
import com.mousiki.shared.data.repository.StatisticsRepository
import com.mousiki.shared.preference.PreferencesHelper
import com.mousiki.shared.preference.SettingsProvider
import com.mousiki.shared.utils.AnalyticsApi
import com.mousiki.shared.utils.ConnectivityChecker
import com.mousiki.shared.utils.NetworkUtils
import com.squareup.inject.assisted.dagger2.AssistedModule
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json
import javax.inject.Singleton

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-09.
 ***************************************
 */

@AssistedModule
@Module(includes = [AssistedInject_AppModule::class])
object AppModule {

    @Singleton
    @JvmStatic
    @Provides
    fun providesKotlinXJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Singleton
    @JvmStatic
    @Provides
    fun providesSharedPref(context: Context): SharedPreferences =
        context.getSharedPreferences("music.app", Context.MODE_PRIVATE)

    @Singleton
    @JvmStatic
    @Provides
    fun providesConnectivityState(context: Context): ConnectivityChecker =
        ConnectivityState(context)

    // Region: To ease migration ==> will migrate to koin
    @Singleton
    @JvmStatic
    @Provides
    fun providesGenreRepository(): GenresRepository = GenresRepository()

    @Singleton
    @JvmStatic
    @Provides
    fun providesSqlDb(appContext: Context): MousikiDb {
        val driver = AndroidSqliteDriver(
            MousikiDb.Schema,
            appContext,
            "music_track_database"
        )
        return MousikiDb(driver)
    }

    @Singleton
    @JvmStatic
    @Provides
    fun providesKMMPreferenceHelper(
        context: Context
    ): PreferencesHelper = PreferencesHelper(SettingsProvider(context))

    @JvmStatic
    @Provides
    fun providesLocalSongsDataSource(
        preferences: PreferencesHelper,
        db: MousikiDb
    ): LocalSongsDataSource = LocalSongsDataSource(preferences, db)

    @JvmStatic
    @Provides
    fun providesArtistsLocalDataSource(
        db: MousikiDb
    ): ArtistsLocalDataSource = ArtistsLocalDataSource(db)

    @JvmStatic
    @Provides
    fun providesSearchLocalDataSource(
        db: MousikiDb
    ): SearchLocalDataSource = SearchLocalDataSource(db)

    @JvmStatic
    @Provides
    fun providesPlaylistSongsLocalDataSource(
        db: MousikiDb
    ): PlaylistSongsLocalDataSource = PlaylistSongsLocalDataSource(db)

    @JvmStatic
    @Provides
    fun providesChannelPlaylistsLocalDataSource(
        db: MousikiDb
    ): ChannelPlaylistsLocalDataSource = ChannelPlaylistsLocalDataSource(db)

    @JvmStatic
    @Provides
    fun providesChannelSongsLocalDataSource(
        db: MousikiDb
    ): ChannelSongsLocalDataSource = ChannelSongsLocalDataSource(db)


    @Singleton
    @JvmStatic
    @Provides
    fun providesCustomPlaylistsRepository(
        db: MousikiDb
    ): CustomPlaylistsRepository = CustomPlaylistsRepository(db)


    @Singleton
    @JvmStatic
    @Provides
    fun providesNetworkUtils(
        appContext: Context
    ): NetworkUtils = NetworkUtils(appContext)


    @Singleton
    @JvmStatic
    @Provides
    fun providesSongsRepository(
        db: MousikiDb,
        remoteDataSource: RemoteSongsDataSource,
        localDataSource: LocalSongsDataSource,
        networkUtils: NetworkUtils
    ): SongsRepository = SongsRepository(db, remoteDataSource, localDataSource, networkUtils)


    @Singleton
    @JvmStatic
    @Provides
    fun providesStatisticsRepository(
        db: MousikiDb,
    ): StatisticsRepository = StatisticsRepository(db)


    @Singleton
    @JvmStatic
    @Provides
    fun providesMousikiApi(
        preferences: PreferencesHelper
    ): MousikiApi = MousikiApiImpl(preferences)


    @Singleton
    @JvmStatic
    @Provides
    fun providesNetworkRunner(): NetworkRunner = NetworkRunner()


    // Mappers
    @Singleton
    @JvmStatic
    @Provides
    fun providesYTBChannelToArtist(): YTBChannelToArtist = YTBChannelToArtist()

    @Singleton
    @JvmStatic
    @Provides
    fun providesYTBChannelToChannel(): YTBChannelToChannel = YTBChannelToChannel()


    @Singleton
    @JvmStatic
    @Provides
    fun providesYTBPlaylistItemToTrack(): YTBPlaylistItemToTrack = YTBPlaylistItemToTrack()


    @Singleton
    @JvmStatic
    @Provides
    fun providesYTBPlaylistItemToVideoId(): YTBPlaylistItemToVideoId = YTBPlaylistItemToVideoId()


    @Singleton
    @JvmStatic
    @Provides
    fun providesYTBPlaylistToPlaylist(): YTBPlaylistToPlaylist = YTBPlaylistToPlaylist()


    @Singleton
    @JvmStatic
    @Provides
    fun providesYTBSearchResultToChannelId(): YTBSearchResultToChannelId =
        YTBSearchResultToChannelId()


    @Singleton
    @JvmStatic
    @Provides
    fun providesYTBSearchResultToPlaylistId(): YTBSearchResultToPlaylistId =
        YTBSearchResultToPlaylistId()

    @Singleton
    @JvmStatic
    @Provides
    fun providesYTBSearchResultToVideoId(): YTBSearchResultToVideoId = YTBSearchResultToVideoId()

    @Singleton
    @JvmStatic
    @Provides
    fun providesYTBVideoToTrack(): YTBVideoToTrack = YTBVideoToTrack()

    @JvmStatic
    @Provides
    fun providesChannelPlaylistsRemoteDataSource(
        mousikiApi: MousikiApi,
        networkRunner: NetworkRunner,
        playlistMapper: YTBPlaylistToPlaylist
    ): ChannelPlaylistsRemoteDataSource =
        ChannelPlaylistsRemoteDataSource(mousikiApi, networkRunner, playlistMapper)

    @Singleton
    @JvmStatic
    @Provides
    fun providesRemoteAppConfig(
        firebaseRemoteConfig: FirebaseRemoteConfig,
        json: Json,
        context: Context,
        preferencesHelper: PreferencesHelper
    ) = RemoteAppConfig(
        firebaseRemoteConfig,
        json,
        context,
        preferencesHelper
    )

    @JvmStatic
    @Provides
    fun providesSearchRemoteDataSource(
        networkRunner: NetworkRunner,
        trackMapper: YTBVideoToTrack,
        playlistMapper: YTBPlaylistToPlaylist,
        channelMapper: YTBChannelToChannel,
        videoIdMapper: YTBSearchResultToVideoId,
        channelIdMapper: YTBSearchResultToChannelId,
        playlistIdMapper: YTBSearchResultToPlaylistId,
        analytics: AnalyticsApi,
        remoteConfig: RemoteAppConfig,
        mousikiApi: MousikiApi
    ) = SearchRemoteDataSource(
        networkRunner,
        trackMapper,
        playlistMapper,
        channelMapper,
        videoIdMapper,
        channelIdMapper,
        playlistIdMapper,
        analytics,
        remoteConfig,
        mousikiApi
    )

    @JvmStatic
    @Provides
    fun providesArtistsRemoteDataSource(
        mousikiApi: MousikiApi,
        networkRunner: NetworkRunner,
        artistMapper: YTBChannelToArtist
    ) = ArtistsRemoteDataSource(
        mousikiApi,
        networkRunner,
        artistMapper
    )
}