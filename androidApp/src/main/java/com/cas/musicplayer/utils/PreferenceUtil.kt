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
import com.mousiki.shared.utils.Constants.FILTER_SONGS_LESS_THAN_100k
import com.mousiki.shared.utils.Constants.FILTER_SONGS_LESS_THAN_60S
import com.mousiki.shared.utils.Constants.FILTER_VIDEOS_LESS_THAN_100k
import com.mousiki.shared.utils.Constants.FILTER_VIDEOS_LESS_THAN_60S
import com.mousiki.shared.utils.Constants.FILTER_VIDEO_DURATION
import com.mousiki.shared.utils.Constants.FILTER_VIDEO_SIZE
import com.mousiki.shared.utils.Constants.INITIALIZED_BLACKLIST
import com.mousiki.shared.utils.Constants.MUSIC_SEEN
import com.mousiki.shared.utils.Constants.SONG_SORT_ORDER
import com.mousiki.shared.utils.Constants.VIDEO_SORT_ORDER


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

    var videoSortOrder
        get() = sharedPreferences.getStringOrDefault(
            VIDEO_SORT_ORDER,
            SortOrder.VideoSortOrder.SONG_A_Z
        )
        set(value) = sharedPreferences.edit {
            putString(VIDEO_SORT_ORDER, value)
        }

    val filterLength get() = sharedPreferences.getInt(FILTER_SONG, 20)
    val filterSizeKb get() = sharedPreferences.getInt(FILTER_SONG, 100)

    val filterVideoLength get() = sharedPreferences.getInt(FILTER_VIDEO_DURATION, 20)
    val filterVideoSizeKb get() = sharedPreferences.getInt(FILTER_VIDEO_SIZE, 100)

    var isInitializedBlacklist
        get() = sharedPreferences.getBoolean(
            INITIALIZED_BLACKLIST, false
        )
        set(value) = sharedPreferences.edit {
            putBoolean(INITIALIZED_BLACKLIST, value)
        }

    var filterAudioLessThanDuration
        get() = sharedPreferences.getBoolean(
            FILTER_SONGS_LESS_THAN_60S, false
        )
        set(value) = sharedPreferences.edit {
            putBoolean(FILTER_SONGS_LESS_THAN_60S, value)
        }

    var filterAudioLessThanSize
        get() = sharedPreferences.getBoolean(
            FILTER_SONGS_LESS_THAN_100k, false
        )
        set(value) = sharedPreferences.edit {
            putBoolean(FILTER_SONGS_LESS_THAN_100k, value)
        }

    var filterVideoLessThanDuration
        get() = sharedPreferences.getBoolean(
            FILTER_VIDEOS_LESS_THAN_60S, false
        )
        set(value) = sharedPreferences.edit {
            putBoolean(FILTER_VIDEOS_LESS_THAN_60S, value)
        }

    var filterVideoLessThanSize
        get() = sharedPreferences.getBoolean(
            FILTER_VIDEOS_LESS_THAN_100k, false
        )
        set(value) = sharedPreferences.edit {
            putBoolean(FILTER_VIDEOS_LESS_THAN_100k, value)
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

    fun toggleFolderVisibility(path: String) {
        val hidden = sharedPreferences.getBoolean(path, false)
        sharedPreferences.edit { putBoolean(path, !hidden) }
    }

    fun isFolderHidden(path: String): Boolean {
        return sharedPreferences.getBoolean(path, false)
    }

    fun toggleVideosFolderVisibility(path: String) {
        val hidden = sharedPreferences.getBoolean(path, false)
        sharedPreferences.edit { putBoolean(path, !hidden) }
    }

    fun isVideoFolderHidden(path: String): Boolean {
        return sharedPreferences.getBoolean(path, false)
    }
}

fun SharedPreferences.getStringOrDefault(key: String, default: String): String {
    return getString(key, default) ?: default
}