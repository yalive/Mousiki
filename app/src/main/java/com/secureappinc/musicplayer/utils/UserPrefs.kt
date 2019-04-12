package com.secureappinc.musicplayer.utils

import android.content.Context
import android.content.SharedPreferences
import com.secureappinc.musicplayer.MusicApp

/**
 **********************************
 * Created by Abdelhadi on 4/12/19.
 **********************************
 */
object UserPrefs {

    val PREF_NAME = "music-app-pref"


    fun saveFav(videoId: String) {
        val pref = getPrefs()
        pref.edit().putBoolean(videoId, true).apply()
    }


    private fun getPrefs(): SharedPreferences {
        return MusicApp.get().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
}