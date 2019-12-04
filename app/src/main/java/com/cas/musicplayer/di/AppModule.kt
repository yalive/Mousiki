package com.cas.musicplayer.di

import android.content.Context
import android.content.SharedPreferences
import com.cas.musicplayer.MusicApp
import com.cas.musicplayer.data.local.database.MusicTrackRoomDatabase
import com.cas.musicplayer.data.local.database.dao.*
import com.cas.musicplayer.data.remote.retrofit.AddKeyInterceptor
import com.cas.musicplayer.data.remote.retrofit.YoutubeService
import com.cas.musicplayer.utils.Constants
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.readystatesoftware.chuck.ChuckInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-09.
 ***************************************
 */
@Module
object AppModule {

    @Singleton
    @JvmStatic
    @Provides
    fun providesYoutubeService(gson: Gson, client: OkHttpClient): YoutubeService {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
        return retrofit.create(YoutubeService::class.java)
    }

    @Singleton
    @JvmStatic
    @Provides
    fun providesGson(): Gson = GsonBuilder().setLenient().create()

    @Singleton
    @JvmStatic
    @Provides
    fun providesOkHttp(): OkHttpClient {
        val client = OkHttpClient.Builder()
            .connectTimeout((5 * 60).toLong(), TimeUnit.SECONDS)
            .writeTimeout((5 * 60).toLong(), TimeUnit.SECONDS)
            .readTimeout((5 * 60).toLong(), TimeUnit.SECONDS)
            .addInterceptor(AddKeyInterceptor())

        if (Constants.Config.DEBUG_NETWORK) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            client.addInterceptor(logging)
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
    fun providesDataBase(context: Context): MusicTrackRoomDatabase = MusicTrackRoomDatabase.getDatabase(context)

    @Singleton
    @JvmStatic
    @Provides
    fun providesTrendingSongsDao(db: MusicTrackRoomDatabase): TrendingSongsDao = db.trendingSongsDao()

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
    fun providesChannelPlaylistsDao(db: MusicTrackRoomDatabase): ChannelPlaylistsDao = db.channelPlaylistsDao()

    @Singleton
    @JvmStatic
    @Provides
    fun providesPlaylistSongsDao(db: MusicTrackRoomDatabase): PlaylistSongsDao = db.playlistSongsDao()

    @Singleton
    @JvmStatic
    @Provides
    fun providesSongTitleDao(db: MusicTrackRoomDatabase): SongTitleDao = db.songTitleDao()

    @Singleton
    @JvmStatic
    @Provides
    fun providesRecentlyPlayedTracksDao(db: MusicTrackRoomDatabase): RecentlyPlayedTracksDao = db.recentlyPlayedTracksDao()

    @Singleton
    @JvmStatic
    @Provides
    fun providesHistoricTracksDao(db: MusicTrackRoomDatabase): HistoricTracksDao = db.historicTracksDao()
}