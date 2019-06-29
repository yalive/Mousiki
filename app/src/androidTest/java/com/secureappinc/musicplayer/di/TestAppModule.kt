package com.secureappinc.musicplayer.di

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.readystatesoftware.chuck.ChuckInterceptor
import com.secureappinc.musicplayer.net.AddKeyInterceptor
import com.secureappinc.musicplayer.net.YoutubeService
import com.secureappinc.musicplayer.ui.charts.ChartsViewModel
import com.secureappinc.musicplayer.utils.Constants
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.mockito.Mockito
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-19.
 ***************************************
 */
@Module
object TestAppModule {

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
    fun providesOkHttp(appContext: Context): OkHttpClient {
        val client = OkHttpClient.Builder()
            .connectTimeout((5 * 60).toLong(), TimeUnit.SECONDS)
            .writeTimeout((5 * 60).toLong(), TimeUnit.SECONDS)
            .readTimeout((5 * 60).toLong(), TimeUnit.SECONDS)
            .addInterceptor(AddKeyInterceptor())

        if (Constants.Config.DEBUG_NETWORK) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            client.addInterceptor(logging)
            client.addInterceptor(ChuckInterceptor(appContext))
        }
        return client.build()
    }

    @Singleton
    @Provides
    fun providesSharedPref(context: Context): SharedPreferences =
        context.getSharedPreferences("music.app", Context.MODE_PRIVATE)


    @Singleton
    @Provides
    fun providesChartsViewModel(): ChartsViewModel {
        return Mockito.mock(ChartsViewModel::class.java)
    }
}