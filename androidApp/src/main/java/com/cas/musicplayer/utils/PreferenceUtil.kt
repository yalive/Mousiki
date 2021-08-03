package com.cas.musicplayer.utils

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.cas.musicplayer.MusicApp
import com.mousiki.shared.utils.Constants.ALBUM_ARTISTS_ONLY
import com.mousiki.shared.utils.Constants.ALBUM_DETAIL_SONG_SORT_ORDER
import com.mousiki.shared.utils.Constants.ALBUM_SONG_SORT_ORDER
import com.mousiki.shared.utils.Constants.ALBUM_SORT_ORDER
import com.mousiki.shared.utils.Constants.ARTIST_ALBUM_SORT_ORDER
import com.mousiki.shared.utils.Constants.ARTIST_SONG_SORT_ORDER
import com.mousiki.shared.utils.Constants.ARTIST_SORT_ORDER
import com.mousiki.shared.utils.Constants.FILTER_SONG
import com.mousiki.shared.utils.Constants.INITIALIZED_BLACKLIST
import com.mousiki.shared.utils.Constants.MUSIC_SEEN
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

    var albumDetailSongSortOrder
        get() = sharedPreferences.getStringOrDefault(
            ALBUM_DETAIL_SONG_SORT_ORDER,
            SortOrder.AlbumSongSortOrder.SONG_TRACK_LIST
        )
        set(value) = sharedPreferences.edit { putString(ALBUM_DETAIL_SONG_SORT_ORDER, value) }

    var albumSortOrder
        get() = sharedPreferences.getStringOrDefault(
            ALBUM_SORT_ORDER,
            SortOrder.AlbumSortOrder.ALBUM_A_Z
        )
        set(value) = sharedPreferences.edit {
            putString(ALBUM_SORT_ORDER, value)
        }

    val albumSongSortOrder
        get() = sharedPreferences.getStringOrDefault(
            ALBUM_SONG_SORT_ORDER,
            SortOrder.AlbumSongSortOrder.SONG_TRACK_LIST
        )

    var albumArtistsOnly
        get() = sharedPreferences.getBoolean(
            ALBUM_ARTISTS_ONLY,
            false
        )
        set(value) = sharedPreferences.edit { putBoolean(ALBUM_ARTISTS_ONLY, value) }

    var artistSortOrder
        get() = sharedPreferences.getStringOrDefault(
            ARTIST_SORT_ORDER,
            SortOrder.ArtistSortOrder.ARTIST_A_Z
        )
        set(value) = sharedPreferences.edit {
            putString(ARTIST_SORT_ORDER, value)
        }

    val artistAlbumSortOrder
        get() = sharedPreferences.getStringOrDefault(
            ARTIST_ALBUM_SORT_ORDER,
            SortOrder.ArtistAlbumSortOrder.ALBUM_A_Z
        )

    val artistSongSortOrder
        get() = sharedPreferences.getStringOrDefault(
            ARTIST_SONG_SORT_ORDER,
            SortOrder.AlbumSongSortOrder.SONG_TRACK_LIST
        )

    var musicSeen
        get() = sharedPreferences.getBoolean(
            MUSIC_SEEN,
            false
        )
        set(value) = sharedPreferences.edit { putBoolean(MUSIC_SEEN, value) }
}

fun SharedPreferences.getStringOrDefault(key: String, default: String): String {
    return getString(key, default) ?: default
}