package com.cas.musicplayer.di

import android.content.Context
import android.content.SharedPreferences
import com.cas.common.connectivity.ConnectivityState
import com.cas.musicplayer.MousikiDb
import com.cas.musicplayer.MusicApp
import com.cas.musicplayer.data.local.database.MusicTrackRoomDatabase
import com.cas.musicplayer.data.local.database.dao.*
import com.cas.musicplayer.data.remote.retrofit.AddKeyInterceptor
import com.cas.musicplayer.data.remote.retrofit.MousikiSearchApi
import com.cas.musicplayer.data.remote.retrofit.YoutubeService
import com.cas.musicplayer.utils.Constants
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.mousiki.shared.data.repository.GenresRepository
import com.mousiki.shared.preference.PreferencesHelper
import com.mousiki.shared.preference.SettingsProvider
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

    @Singleton
    @JvmStatic
    @Provides
    fun providesDataBase(context: Context): MusicTrackRoomDatabase =
        MusicTrackRoomDatabase.getDatabase(context)

    @Singleton
    @JvmStatic
    @Provides
    fun providesTrendingSongsDao(db: MusicTrackRoomDatabase): TrendingSongsDao =
        db.trendingSongsDao()

    @Singleton
    @JvmStatic
    @Provides
    fun providesFavouriteSongsDao(db: MusicTrackRoomDatabase): FavouriteTracksDao =
        db.favouriteTracksDao()

    @Singleton
    @JvmStatic
    @Provides
    fun providesArtistDao(db: MusicTrackRoomDatabase): ArtistDao = db.artistsDao()

    @Singleton
    @JvmStatic
    @Provides
    fun providesChannelSongsDao(db: MusicTrackRoomDatabase): ChannelSongsDao = db.channelSongsDao()

    @Singleton
    @JvmStatic
    @Provides
    fun providesChannelPlaylistsDao(db: MusicTrackRoomDatabase): ChannelPlaylistsDao =
        db.channelPlaylistsDao()

    @Singleton
    @JvmStatic
    @Provides
    fun providesPlaylistSongsDao(db: MusicTrackRoomDatabase): PlaylistSongsDao =
        db.playlistSongsDao()

    @Singleton
    @JvmStatic
    @Provides
    fun providesSongTitleDao(db: MusicTrackRoomDatabase): LightSongDao = db.songTitleDao()

    @Singleton
    @JvmStatic
    @Provides
    fun providesRecentlyPlayedTracksDao(db: MusicTrackRoomDatabase): RecentlyPlayedTracksDao =
        db.recentlyPlayedTracksDao()

    @Singleton
    @JvmStatic
    @Provides
    fun providesHistoricTracksDao(db: MusicTrackRoomDatabase): HistoricTracksDao =
        db.historicTracksDao()

    @Singleton
    @JvmStatic
    @Provides
    fun providesSearchQueryDao(db: MusicTrackRoomDatabase): SearchQueryDao = db.searchQueryDao()

    @Singleton
    @JvmStatic
    @Provides
    fun providesSearchSongDao(db: MusicTrackRoomDatabase): SearchSongDao = db.searchSongDao()

    @Singleton
    @JvmStatic
    @Provides
    fun providesSearchPlaylistsDao(db: MusicTrackRoomDatabase): SearchPlaylistsDao =
        db.searchPlaylistsDao()

    @Singleton
    @JvmStatic
    @Provides
    fun providesSearchChannelDao(db: MusicTrackRoomDatabase): SearchChannelDao =
        db.searchSearchChannelDao()

    @Singleton
    @JvmStatic
    @Provides
    fun providesCustomPlaylistTrackDao(db: MusicTrackRoomDatabase): CustomPlaylistTrackDao =
        db.customPlaylistTrackDao()

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

}