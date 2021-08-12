package com.cas.musicplayer.ui.local.repository

import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns
import android.provider.MediaStore
import android.provider.MediaStore.Audio.AudioColumns
import android.provider.MediaStore.Audio.AudioColumns.IS_MUSIC
import android.provider.MediaStore.Audio.Media
import com.cas.musicplayer.utils.*
import com.mousiki.shared.domain.models.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by Fayssel Yabahddou on 6/18/21.
 */
class LocalSongsRepository(private val context: Context) {

    suspend fun songs(): List<Song> = withContext(Dispatchers.IO) {
        return@withContext songs(makeSongCursor(null, null))
    }

    fun songs(cursor: Cursor?): List<Song> {
        val songs = arrayListOf<Song>()
        if (cursor != null && cursor.moveToFirst()) {
            do {
                songs.add(getSongFromCursorImpl(cursor))
            } while (cursor.moveToNext())
        }
        cursor?.close()
        return songs
    }

    fun song(cursor: Cursor?): Song {
        val song: Song = if (cursor != null && cursor.moveToFirst()) {
            getSongFromCursorImpl(cursor)
        } else {
            Song.emptySong
        }
        cursor?.close()
        return song
    }

    fun songs(query: String): List<Song> {
        return songs(makeSongCursor(AudioColumns.TITLE + " LIKE ?", arrayOf("%$query%")))
    }

    suspend fun song(songId: Long): Song = withContext(Dispatchers.IO) {
        return@withContext song(makeSongCursor(AudioColumns._ID + "=?", arrayOf(songId.toString())))
    }

    fun songsByFilePath(filePath: String): List<Song> {
        return songs(
            makeSongCursor(
                AudioColumns.DATA + "=?",
                arrayOf(filePath)
            )
        )
    }

    private fun getSongFromCursorImpl(
        cursor: Cursor
    ): Song {
        val id = cursor.getLong(AudioColumns._ID)
        val title = cursor.getString(AudioColumns.TITLE)
        val trackNumber = cursor.getInt(AudioColumns.TRACK)
        val year = cursor.getInt(AudioColumns.YEAR)
        val duration = cursor.getLong(AudioColumns.DURATION)
        val data = cursor.getString(AudioColumns.DATA)
        val dateModified = cursor.getLong(AudioColumns.DATE_MODIFIED)
        val albumId = cursor.getLong(AudioColumns.ALBUM_ID)
        val albumName = cursor.getStringOrNull(AudioColumns.ALBUM)
        val artistId = cursor.getLong(AudioColumns.ARTIST_ID)
        val artistName = cursor.getStringOrNull(AudioColumns.ARTIST)
        val composer = cursor.getStringOrNull(AudioColumns.COMPOSER)
        val albumArtist = cursor.getStringOrNull("album_artist")
        val path = data.substringBeforeLast("/")
        return Song(
            id,
            title,
            trackNumber,
            year,
            duration,
            data,
            dateModified,
            albumId,
            albumName ?: "",
            artistId,
            artistName ?: "",
            composer ?: "",
            albumArtist ?: "",
            path
        )
    }

    @JvmOverloads
    fun makeSongCursor(

        selection: String?,
        selectionValues: Array<String>?,
        sortOrder: String = PreferenceUtil.songSortOrder
    ): Cursor? {
        var selectionFinal = selection
        var selectionValuesFinal = selectionValues
        selectionFinal = if (selection != null && selection.trim { it <= ' ' } != "") {
            "$IS_MUSIC AND $selectionFinal"
        } else {
            IS_MUSIC
        }

        // Blacklist
        val paths = BlacklistStore.getInstance(context).paths
        if (paths.isNotEmpty()) {
            selectionFinal = generateBlacklistSelection(selectionFinal, paths.size)
            selectionValuesFinal = addBlacklistSelectionValues(selectionValuesFinal, paths)
        }
        selectionFinal =
            selectionFinal + " AND " + Media.DURATION + ">= " + (PreferenceUtil.filterLength * 1000)

        val uri = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            Media.EXTERNAL_CONTENT_URI
        }
        return try {
            context.contentResolver.query(
                uri,
                baseProjection,
                selectionFinal,
                selectionValuesFinal,
                sortOrder
            )
        } catch (ex: SecurityException) {
            return null
        }
    }

    private fun generateBlacklistSelection(
        selection: String?,
        pathCount: Int
    ): String {
        val newSelection = StringBuilder(
            if (selection != null && selection.trim { it <= ' ' } != "") "$selection AND " else "")
        newSelection.append(AudioColumns.DATA + " NOT LIKE ?")
        for (i in 0 until pathCount - 1) {
            newSelection.append(" AND " + AudioColumns.DATA + " NOT LIKE ?")
        }
        return newSelection.toString()
    }

    private fun addBlacklistSelectionValues(
        selectionValues: Array<String>?,
        paths: ArrayList<String>
    ): Array<String>? {
        var selectionValuesFinal = selectionValues
        if (selectionValuesFinal == null) {
            selectionValuesFinal = emptyArray()
        }
        val newSelectionValues = Array(selectionValuesFinal.size + paths.size) {
            "n = $it"
        }
        System.arraycopy(selectionValuesFinal, 0, newSelectionValues, 0, selectionValuesFinal.size)
        for (i in selectionValuesFinal.size until newSelectionValues.size) {
            newSelectionValues[i] = paths[i - selectionValuesFinal.size] + "%"
        }
        return newSelectionValues
    }
}

val baseProjection = arrayOf(
    BaseColumns._ID, // 0
    AudioColumns.TITLE, // 1
    AudioColumns.TRACK, // 2
    AudioColumns.YEAR, // 3
    AudioColumns.DURATION, // 4
    AudioColumns.DATA, // 5
    AudioColumns.DATE_MODIFIED, // 6
    AudioColumns.ALBUM_ID, // 7
    AudioColumns.ALBUM, // 8
    AudioColumns.ARTIST_ID, // 9
    AudioColumns.ARTIST, // 10
    AudioColumns.COMPOSER, // 11
    "album_artist" // 12
)