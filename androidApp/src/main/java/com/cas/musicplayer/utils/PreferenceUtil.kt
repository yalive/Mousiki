package com.cas.musicplayer.utils

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.cas.musicplayer.MusicApp
import com.mousiki.shared.utils.Constants.FILTER_SONG
import com.mousiki.shared.utils.Constants.INITIALIZED_BLACKLIST
import com.mousiki.shared.utils.Constants.SONG_SORT_ORDER


/**
 * Created by Fayssel Yabahddou on 6/18/21.
 */
object PreferenceUtil {
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MusicApp.get())

    var songSortOrder
        get() = sharedPreferences.getStringOrDefault(
            SONG_SORT_ORDER,
            SortOrder.SongSortOrder.SONG_A_Z
        )
        set(value) = sharedPreferences.edit {
            putString(SONG_SORT_ORDER, value)
        }

    val filterLength get() = sharedPreferences.getInt(FILTER_SONG, 20)

    var isInitializedBlacklist
        get() = sharedPreferences.getBoolean(
            INITIALIZED_BLACKLIST, false
        )
        set(value) = sharedPreferences.edit {
            putBoolean(INITIALIZED_BLACKLIST, value)
        }

}

fun SharedPreferences.getStringOrDefault(key: String, default: String): String {
    return getString(key, default) ?: default
}