package com.cas.musicplayer.ui.local.repository

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.BaseColumns
import android.provider.MediaStore
import android.provider.MediaStore.Audio.AudioColumns
import android.provider.MediaStore.Audio.Media
import android.util.Log
import com.cas.musicplayer.MusicApp
import com.cas.musicplayer.utils.*
import com.mousiki.shared.domain.models.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

import android.content.ContentValues
import android.os.Build
import androidx.annotation.RequiresApi


/**
 * Created by Fayssel Yabahddou on 6/18/21.
 */
class LocalSongsRepository(private val context: Context) {

    suspend fun songs(): List<Song> = withContext(Dispatchers.IO) {
        return@withContext songs(makeSongCursor(null, null))
    }

    fun songs(cursor: Cursor?): List<Song> {
        Log.i("LocalSongsRepository", "fun called : songs, Cursor : ${cursor != null}")
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
        return@withContext song(
            makeSongCursor(
                selection = AudioColumns._ID + "=?",
                selectionValues = arrayOf(songId.toString()),
                withFilter = false
            )
        )
    }

    suspend fun updateSong(
        songId: Long,
        name: String,
        artist: String,
        album: String,
        composer: String
    ): Song = withContext(Dispatchers.IO) {
        return@withContext if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            updateSongQ(songId, name, artist, album, composer)
        } else {
            updateSongBQ(songId, name, artist, album, composer)
        }
    }

    private suspend fun updateSongBQ(
        songId: Long,
        name: String,
        artist: String,
        album: String,
        composer: String
    ): Song = withContext(Dispatchers.IO) {
        val cv = ContentValues(1)
        val uri = ContentUris.withAppendedId(mediaUri(), songId)
        cv.put(Media.TITLE, name)
        cv.put(Media.ARTIST, artist)
        cv.put(Media.ALBUM, album)
        cv.put(Media.COMPOSER, composer)
        context.contentResolver.update(uri, cv, null, null)
        return@withContext song(
            makeSongCursor(
                selection = AudioColumns._ID + "=?",
                selectionValues = arrayOf(songId.toString()),
                withFilter = false
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private suspend fun updateSongQ(
        songId: Long,
        name: String,
        artist: String,
        album: String,
        composer: String
    ): Song = withContext(Dispatchers.IO) {

        val uri = ContentUris.withAppendedId(mediaUri(), songId)

        val values1 = ContentValues().apply {
            put(Media.IS_PENDING, 1)
        }
        context.contentResolver.update(uri, values1, null, null)

        val values = ContentValues().apply {
            put(Media.TITLE, name)
            put(Media.DISPLAY_NAME, name)
            put(Media.ARTIST, artist)
            put(Media.ALBUM, album)
            put(Media.COMPOSER, composer)
            put(Media.IS_PENDING, 0)
        }

        context.contentResolver.update(uri, values, null, null)

        return@withContext song(
            makeSongCursor(
                selection = AudioColumns._ID + "=?",
                selectionValues = arrayOf(songId.toString()),
                withFilter = false
            )
        )
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
        val artistName = cursor.getStringOrNull(Media.ARTIST)
        val composer = cursor.getStringOrNull(AudioColumns.COMPOSER)
        val albumArtist = cursor.getStringOrNull("album_artist")
        val path = data.substringBeforeLast("/")
        val size = cursor.getLong(AudioColumns.SIZE)
        val mimeType = cursor.getString(AudioColumns.MIME_TYPE)
        return Song(
            id = id,
            title = title,
            trackNumber = trackNumber,
            year = year,
            duration = duration,
            data = data,
            dateModified = dateModified,
            albumId = albumId,
            albumName = albumName ?: "",
            artistId = artistId,
            artistName = artistName ?: "",
            composer = composer ?: "",
            albumArtist = albumArtist ?: "",
            path = path,
            size = size,
            mimeType = mimeType
        )
    }

    @JvmOverloads
    fun makeSongCursor(
        selection: String?,
        selectionValues: Array<String>?,
        sortOrder: String = PreferenceUtil.songSortOrder,
        withFilter: Boolean = true // util when making selection by id
    ): Cursor? {
        var selectionFinal = selection
        var selectionValuesFinal = selectionValues
        selectionFinal = if (selection != null && selection.trim { it <= ' ' } != "") {
            "(${AudioColumns.IS_MUSIC} OR ${AudioColumns.IS_NOTIFICATION} OR ${AudioColumns.IS_PODCAST} OR ${AudioColumns.IS_RINGTONE} OR ${AudioColumns.IS_ALARM}) AND $selectionFinal"
        } else {
            "(${AudioColumns.IS_MUSIC} OR ${AudioColumns.IS_NOTIFICATION} OR ${AudioColumns.IS_PODCAST} OR ${AudioColumns.IS_RINGTONE} OR ${AudioColumns.IS_ALARM}) "
        }

        if (withFilter) {

            val minDuration =
                if (PreferenceUtil.filterAudioLessThanDuration) PreferenceUtil.filterLength * 1000 else DEF_MIN_DURATION * 1000
            selectionFinal = selectionFinal + " AND " + Media.DURATION + ">= " + minDuration
            if (PreferenceUtil.filterAudioLessThanSize) {
                selectionFinal =
                    selectionFinal + " AND " + Media.SIZE + ">= " + (PreferenceUtil.filterSizeKb * 1000)
            }
        }

        return try {
            context.contentResolver.query(
                mediaUri(),
                baseProjection,
                selectionFinal,
                selectionValuesFinal,
                sortOrder
            )
        } catch (ex: SecurityException) {
            return null
        }
    }

    private fun mediaUri(): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            Media.EXTERNAL_CONTENT_URI
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

    companion object {
        private const val DEF_MIN_DURATION = 2
    }
}

fun List<Song>.filterNotHidden(): List<Song> {
    return filter {
        !PreferenceUtil.isFolderHidden(File(it.path).fixedPath(MusicApp.get()))
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
    AudioColumns.SIZE, // 11
    AudioColumns.MIME_TYPE, // 11
    "album_artist" // 12
)