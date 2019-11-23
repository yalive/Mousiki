package com.cas.musicplayer.data.preferences

import android.content.Context
import android.content.SharedPreferences
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
        return preferences().getString(EXTRAS_POPULAR_NEXT_PAGE, "") ?: ""
    }

    fun setMostPopularNextPageToken(nextPageToken: String) {
        preferences().edit().putString(EXTRAS_POPULAR_NEXT_PAGE, nextPageToken).apply()
    }

    private fun preferences(): SharedPreferences {
        return context.getSharedPreferences("mousiki", Context.MODE_PRIVATE)
    }

    companion object {
        private val EXTRAS_POPULAR_NEXT_PAGE = "most-popular-next-page"
    }
}