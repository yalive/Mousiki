package com.cas.musicplayer.di

import android.content.Context
import android.content.SharedPreferences
import com.cas.common.connectivity.ConnectivityState
import com.cas.musicplayer.MousikiDb
import com.cas.musicplayer.MusicApp
import com.cas.musicplayer.data.datasource.RemoteSongsDataSource
import com.cas.musicplayer.data.remote.retrofit.AddKeyInterceptor
import com.cas.musicplayer.data.remote.retrofit.MousikiSearchApi
import com.cas.musicplayer.data.remote.retrofit.YoutubeService
import com.cas.musicplayer.data.repositories.SongsRepository
import com.cas.musicplayer.data.repositories.StatisticsRepository
import com.cas.musicplayer.utils.Constants
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.mousiki.shared.data.datasource.ArtistsLocalDataSource
import com.mousiki.shared.data.datasource.LocalSongsDataSource
import com.mousiki.shared.data.datasource.channel.ChannelPlaylistsLocalDataSource
import com.mousiki.shared.data.datasource.channel.ChannelSongsLocalDataSource
import com.mousiki.shared.data.datasource.playlist.PlaylistSongsLocalDataSource
import com.mousiki.shared.data.datasource.search.SearchLocalDataSource
import com.mousiki.shared.data.repository.CustomPlaylistsRepository
import com.mousiki.shared.data.repository.GenresRepository
import com.mousiki.shared.preference.PreferencesHelper
import com.mousiki.shared.preference.SettingsProvider
import com.mousiki.shared.utils.NetworkUtils
import com.readystatesoftware.chuck.ChuckInterceptor
import com.squareup.inject.assisted.dagger2.AssistedModule
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-09.
 ***************************************
 */

@AssistedModule
@Module(includes = [AssistedInject_AppModule::class])
object AppModule {

    @ExperimentalSerializationApi
    @Singleton
    @JvmStatic
    @Provides
    fun providesRetrofit(json: Json, client: OkHttpClient): Retrofit {
        val contentType = MediaType.get("application/json")
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Singleton
    @JvmStatic
    @Provides
    fun providesYoutubeService(retrofit: Retrofit): YoutubeService {
        return retrofit.create(YoutubeService::class.java)
    }

    @Singleton
    @JvmStatic
    @Provides
    fun providesMousikiSearchApi(retrofit: Retrofit): MousikiSearchApi {
        return retrofit.create(MousikiSearchApi::class.java)
    }

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
    fun providesOkHttp(addKeyInterceptor: AddKeyInterceptor): OkHttpClient {
        val client = OkHttpClient.Builder()
            .connectTimeout((5 * 60).toLong(), TimeUnit.SECONDS)
            .writeTimeout((5 * 60).toLong(), TimeUnit.SECONDS)
            .readTimeout((5 * 60).toLong(), TimeUnit.SECONDS)
            .addInterceptor(addKeyInterceptor)

        if (Constants.Config.DEBUG_NETWORK) {
            client.addInterceptor(ChuckInterceptor(MusicApp.get()))
        }
        return client.build()
    }


    @Singleton
    @JvmStatic
    @Provides
    fun providesSharedPref(context: Context): SharedPreferences =
        context.getSharedPreferences("music.app", Context.MODE_PRIVATE)

    @Singleton
    @JvmStatic
    @Provides
    fun providesConnectivityState(context: Context): ConnectivityState =
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


}