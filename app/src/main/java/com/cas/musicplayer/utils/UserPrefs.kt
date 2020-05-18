package com.cas.musicplayer.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.cas.musicplayer.MusicApp
import com.cas.musicplayer.player.PlaySort

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
    val KEY_SLEEP_TIMER_VALUE = "sleep-timer-value"
    val KEY_THEME = "key_theme_mode"
    val KEY_OUT_VIDEO_SIZE = "key_out_video_size"
    val KEY_TOOL_TIP_BATTERY_SAVER = "has_seen_tool_tip_battery_saver"

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
        val sort = pref.getString(KEY_CURRENT_SORT, PlaySort.LOOP_ALL.toString())
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

    fun getPrefs(): SharedPreferences {
        return MusicApp.get().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun resetNumberOfTrackClick() {
        getPrefs().edit().putInt(CLICK_TRACK_COUNT, 0).apply()
    }

    fun onClickTrack() {
        val count = getClickTrackCount()
        getPrefs().edit().putInt(CLICK_TRACK_COUNT, count + 1).apply()
    }

    fun getClickTrackCount(): Int {
        return getPrefs().getInt(CLICK_TRACK_COUNT, 0)
    }

    fun setSleepTimerValue(duration: Int) {
        getPrefs().edit().putInt(KEY_SLEEP_TIMER_VALUE, duration).apply()
    }

    fun getSleepTimerValue(): Int {
        return getPrefs().getInt(KEY_SLEEP_TIMER_VALUE, 10)
    }

    fun setThemeModeValue(mode: Int) {
        getPrefs().edit().putInt(KEY_THEME, mode).apply()
    }

    fun getThemeModeValue(): Int {
        return getPrefs().getInt(KEY_THEME, THEME_DARK)
    }

    fun outVideoSize(): OutVideoSize {
        return when (getPrefs().getInt(KEY_OUT_VIDEO_SIZE, 1)) {
            0 -> OutVideoSize.SMALL
            1 -> OutVideoSize.NORMAL
            2 -> OutVideoSize.LARGE
            else -> OutVideoSize.NORMAL
        }
    }

    fun setOutVideoSize(size: OutVideoSize) {
        val newSize = when (size) {
            OutVideoSize.SMALL -> 0
            OutVideoSize.NORMAL -> 1
            OutVideoSize.LARGE -> 2
        }
        getPrefs().edit {
            putInt(KEY_OUT_VIDEO_SIZE, newSize)
        }
    }

    fun getOutVideoSizeValue(): Int {
        return getPrefs().getInt(KEY_OUT_VIDEO_SIZE, 1)
    }

    fun hasSeenToolTipBatterySaver(): Boolean {
        val pref = getPrefs()
        return pref.getBoolean(KEY_TOOL_TIP_BATTERY_SAVER, false)
    }

    fun setSeenToolTipBatterySaver() {
        getPrefs().edit {
            putBoolean(KEY_TOOL_TIP_BATTERY_SAVER, true)
        }
    }

    const val THEME_AUTOMATIC = 0
    const val THEME_LIGHT = 1
    const val THEME_DARK = 2

    enum class OutVideoSize {
        SMALL, NORMAL, LARGE
    }
}