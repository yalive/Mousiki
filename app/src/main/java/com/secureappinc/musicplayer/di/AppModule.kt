package com.secureappinc.musicplayer.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.readystatesoftware.chuck.ChuckInterceptor
import com.secureappinc.musicplayer.MusicApp
import com.secureappinc.musicplayer.net.AddKeyInterceptor
import com.secureappinc.musicplayer.net.YoutubeService
import com.secureappinc.musicplayer.repository.HomeRepository
import com.secureappinc.musicplayer.ui.home.HomeViewModelFactory
import com.secureappinc.musicplayer.utils.Constants
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
class AppModule {

    @Singleton
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
    @Provides
    fun providesGson(): Gson = GsonBuilder().setLenient().create()

    @Singleton
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
    @Provides
    fun providesViewModelFactory(repository: HomeRepository): HomeViewModelFactory = HomeViewModelFactory(repository)
}