package com.secureappinc.musicplayer.utils

import android.content.Context
import android.content.SharedPreferences
import com.secureappinc.musicplayer.MusicApp
import com.secureappinc.musicplayer.player.PlaySort

/**
 **********************************
 * Created by Abdelhadi on 4/12/19.
 **********************************
 */
object UserPrefs {

    val PREF_NAME = "music-app-pref"
    val KEY_CURRENT_SORT = "current-sort"


    fun saveFav(videoId: String?, isAdd: Boolean) {
        val pref = getPrefs()
        pref.edit().putBoolean(videoId, isAdd).apply()
    }

    fun isFav(videoId: String?): Boolean {
        val pref = getPrefs()
        return pref.getBoolean(videoId, false)
    }


    fun saveSort(sort: PlaySort) {
        val pref = getPrefs()
        pref.edit().putString(KEY_CURRENT_SORT, sort.toString()).apply()
    }


    fun getSort(): PlaySort {
        val pref = getPrefs()
        val sort = pref.getString(KEY_CURRENT_SORT, PlaySort.SEQUENCE.toString())
        return PlaySort.toEnum(sort!!)
    }

    private fun getPrefs(): SharedPreferences {
        return MusicApp.get().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
}