package com.mousiki.shared.preference

import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.domain.models.imgUrl
import com.mousiki.shared.player.PlaySort
import com.russhwolf.settings.Settings

/**
 **********************************
 * Created by Abdelhadi on 4/12/19.
 **********************************
 */
object UserPrefs {

    private lateinit var settings: Settings

    private val KEY_CURRENT_SORT = "current-sort"
    private val KEY_LAUNCH_COUNT = "launch-count"
    val CLICK_TRACK_COUNT = "click-track-count"
    private val KEY_RATED_APP = "has-rated-app"
    private val KEY_SLEEP_TIMER_VALUE = "sleep-timer-value"
    private val KEY_THEME = "key_theme_mode"
    private val KEY_OUT_VIDEO_SIZE = "key_out_video_size"
    private val KEY_TOOL_TIP_BATTERY_SAVER = "has_seen_tool_tip_battery_saver"

    fun init(settingsProvider: SettingsProvider) {
        settings = settingsProvider.providesOldSettings()
    }

    fun saveFav(videoId: String?, isAdd: Boolean) {
        settings.putBoolean(videoId.orEmpty(), isAdd)
    }

    fun isFav(videoId: String?): Boolean {
        return settings.getBoolean(videoId.orEmpty(), false)
    }


    fun saveSort(sort: PlaySort) {
        settings.putString(KEY_CURRENT_SORT, sort.toString())
    }


    fun getCurrentPlaybackSort(): PlaySort {
        val sort = settings.getString(KEY_CURRENT_SORT, PlaySort.LOOP_ALL.toString())
        return PlaySort.toEnum(sort)
    }

    fun onLaunchApp() {
        val count = getLaunchCount()
        settings.putInt(KEY_LAUNCH_COUNT, count + 1)
    }

    fun getLaunchCount(): Int {
        return settings.getInt(KEY_LAUNCH_COUNT, 0)
    }

    fun hasRatedApp(): Boolean {
        return settings.getBoolean(KEY_RATED_APP, false)
    }

    fun setRatedApp() {
        settings.putBoolean(KEY_RATED_APP, true)
    }

    fun resetNumberOfTrackClick() {
        settings.putInt(CLICK_TRACK_COUNT, 0)
    }

    fun onClickTrack() {
        val count = getClickTrackCount()
        settings.putInt(CLICK_TRACK_COUNT, count + 1)
    }

    fun getClickTrackCount(): Int {
        return settings.getInt(CLICK_TRACK_COUNT, 0)
    }

    fun setSleepTimerValue(duration: Int) {
        settings.putInt(KEY_SLEEP_TIMER_VALUE, duration)
    }

    fun getSleepTimerValue(): Int {
        return settings.getInt(KEY_SLEEP_TIMER_VALUE, 10)
    }

    fun setThemeModeValue(mode: Int) {
        settings.putInt(KEY_THEME, mode)
    }

    fun getThemeModeValue(): Int {
        return settings.getInt(KEY_THEME, THEME_DARK)
    }

    fun outVideoSize(): OutVideoSize {
        return when (getOutVideoSizeValue()) {
            0 -> OutVideoSize.SMALL
            1 -> OutVideoSize.NORMAL
            2 -> OutVideoSize.LARGE
            3 -> OutVideoSize.CIRCULAR
            else -> OutVideoSize.CIRCULAR
        }
    }

    fun setOutVideoSize(size: OutVideoSize) {
        val newSize = when (size) {
            OutVideoSize.SMALL -> 0
            OutVideoSize.NORMAL -> 1
            OutVideoSize.LARGE -> 2
            OutVideoSize.CIRCULAR -> 3
        }
        settings.putInt(KEY_OUT_VIDEO_SIZE, newSize)
    }

    fun getOutVideoSizeValue(): Int {
        return settings.getInt(KEY_OUT_VIDEO_SIZE, 3)
    }

    fun hasSeenToolTipBatterySaver(): Boolean {
        return settings.getBoolean(KEY_TOOL_TIP_BATTERY_SAVER, false)
    }

    fun setSeenToolTipBatterySaver() {
        settings.putBoolean(KEY_TOOL_TIP_BATTERY_SAVER, true)
    }

    fun getTrackImageUrl(track: Track): String {
        val url = settings.getString("${track.id}_preferred_url", track.imgUrl)
        return if (url.isNotEmpty()) url else track.imgUrl
    }

    fun setTrackImageUrl(track: Track, url: String) {
        settings.putString("${track.id}_preferred_url", url)
    }

    const val THEME_AUTOMATIC = 0
    const val THEME_LIGHT = 1
    const val THEME_DARK = 2

    enum class OutVideoSize {
        SMALL, NORMAL, LARGE, CIRCULAR
    }
}