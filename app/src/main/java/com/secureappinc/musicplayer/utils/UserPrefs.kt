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
    val KEY_LAUNCH_COUNT = "launch-count"
    val CLICK_TRACK_COUNT = "click-track-count"
    val KEY_RATED_APP = "has-rated-app"

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

    fun onLaunchApp() {
        val count = getLaunchCount()
        getPrefs().edit().putInt(KEY_LAUNCH_COUNT, count + 1).apply()
    }

    fun getLaunchCount(): Int {
        return getPrefs().getInt(KEY_LAUNCH_COUNT, 0)
    }

    fun hasRatedApp(): Boolean {
        return getPrefs().getBoolean(KEY_RATED_APP, false)
    }

    fun setRatedApp() {
        getPrefs().edit().putBoolean(KEY_RATED_APP, true).apply()
    }

    private fun getPrefs(): SharedPreferences {
        return MusicApp.get().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun resetNumberOfTrackClick() {
        val count = getClickTrackCount()
        getPrefs().edit().putInt(CLICK_TRACK_COUNT, 0).apply()
    }

    fun onClickTrack() {
        val count = getClickTrackCount()
        getPrefs().edit().putInt(CLICK_TRACK_COUNT, count + 1).apply()
    }

    fun getClickTrackCount(): Int {
        return getPrefs().getInt(CLICK_TRACK_COUNT, 0)
    }
}