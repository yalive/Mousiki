package com.cas.musicplayer.data.preferences

import android.content.Context
import android.content.SharedPreferences
import android.os.SystemClock
import dagger.Reusable
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-23.
 ***************************************
 */
@Reusable
class PreferencesHelper @Inject constructor(
    private val context: Context
) {

    fun mostPopularNextPageToken(): String {
        return preferences().getString(EXTRAS_POPULAR_NEXT_PAGE, "").orEmpty()
    }

    fun setMostPopularNextPageToken(nextPageToken: String) {
        preferences().edit().putString(EXTRAS_POPULAR_NEXT_PAGE, nextPageToken).apply()
    }

    fun setMostPopularSongsUpdateDate() {
        val elapsedRealtime = SystemClock.elapsedRealtime()
        preferences().edit().putLong(EXTRAS_POPULAR_UPDATE_DATE, elapsedRealtime).apply()
    }

    fun getMostPopularSongsUpdateDate(): Long {
        return preferences().getLong(EXTRAS_POPULAR_UPDATE_DATE, -1)
    }

    fun setChartsUpdateDate() {
        val elapsedRealtime = SystemClock.elapsedRealtime()
        preferences().edit().putLong(EXTRAS_CHARTS_UPDATE_DATE, elapsedRealtime).apply()
    }

    fun getChartsUpdateDate(): Long {
        return preferences().getLong(EXTRAS_CHARTS_UPDATE_DATE, -1)
    }

    private fun preferences(): SharedPreferences {
        return context.getSharedPreferences("mousiki", Context.MODE_PRIVATE)
    }

    companion object {
        private const val EXTRAS_POPULAR_NEXT_PAGE = "most-popular-next-page"
        private const val EXTRAS_POPULAR_UPDATE_DATE = "most-popular-songs-update-date"
        private const val EXTRAS_CHARTS_UPDATE_DATE = "charts-update-date"
    }
}