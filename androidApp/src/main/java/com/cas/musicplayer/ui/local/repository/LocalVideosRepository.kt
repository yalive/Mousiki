package com.cas.musicplayer.ui.local.repository

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.cas.musicplayer.utils.*
import com.mousiki.shared.domain.models.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class LocalVideosRepository(private val context: Context) {

    suspend fun videos(): List<Song> = withContext(Dispatchers.IO) {
        return@withContext videos(makeVideoCursor(null, null))
    }

    fun videos(cursor: Cursor?): List<Song> {
        Log.i("LocalSongsRepository", "fun called : songs, Cursor : ${cursor != null}")
        val videos = arrayListOf<Song>()
        if (cursor != null && cursor.moveToFirst()) {
            do {
                videos.add(getVideoFromCursorImpl(cursor))
            } while (cursor.moveToNext())
        }
        cursor?.close()
        return videos
    }

    fun video(cursor: Cursor?): Song {
        val song: Song = if (cursor != null && cursor.moveToFirst()) {
            getVideoFromCursorImpl(cursor)
        } else {
            Song.emptySong
        }
        cursor?.close()
        return song
    }

    fun videos(query: String): List<Song> {
        return videos(
            makeVideoCursor(
                MediaStore.Video.VideoColumns.TITLE + " LIKE ?",
                arrayOf("%$query%")
            )
        )
    }

    suspend fun video(songId: Long): Song = withContext(Dispatchers.IO) {
        return@withContext video(
            makeVideoCursor(
                selection = MediaStore.Video.VideoColumns._ID + "=?",
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
    ): Song? = withContext(Dispatchers.IO) {

        val cv = ContentValues(1)
        val uri = ContentUris.withAppendedId(mediaUri(), songId)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues(1)
            values.put(MediaStore.Video.Media.IS_PENDING, 1)
            val updatedRows: Int = context.contentResolver.update(uri, values, null, null)
            if (updatedRows == 0)
                return@withContext null
        }
        Log.d("UpdateSong","name : $name artist : $artist")
        cv.put(MediaStore.Video.Media.TITLE, name)
        cv.put(MediaStore.Video.VideoColumns.ARTIST, artist)
        cv.put(MediaStore.Video.VideoColumns.ALBUM, album)
        //cv.put(Media.COMPOSER, composer)

        val rowsUpdated: Int = context.contentResolver.update(uri, cv, null, null)

        if (rowsUpdated > 0) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues(1)
                values.put(MediaStore.Video.Media.IS_PENDING, 0)
                context.contentResolver.update(uri, values, null, null)
            }
            return@withContext video(
                makeVideoCursor(
                    selection = MediaStore.Video.VideoColumns._ID + "=?",
                    selectionValues = arrayOf(songId.toString()),
                    withFilter = false
                )
            )
        } else
            return@withContext null

    }

    fun songsByFilePath(filePath: String): List<Song> {
        return videos(
            makeVideoCursor(
                MediaStore.Video.VideoColumns.DATA + "=?",
                arrayOf(filePath)
            )
        )
    }

    private fun getVideoFromCursorImpl(
        cursor: Cursor
    ): Song {

        val id = cursor.getLong(MediaStore.Video.Media._ID)
        val title = cursor.getString(MediaStore.Video.Media.TITLE)
        val duration = cursor.getLong(MediaStore.Video.VideoColumns.DURATION)
        val data = cursor.getString(MediaStore.Video.VideoColumns.DATA)
        val albumName = cursor.getStringOrNull(MediaStore.Video.VideoColumns.ALBUM)
        val artistName = cursor.getStringOrNull(MediaStore.Video.VideoColumns.ARTIST)
        val path = data.substringBeforeLast("/")
        val size = cursor.getLong(MediaStore.Video.VideoColumns.SIZE)
        val resolution = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.RESOLUTION))
        return Song(
            id = id,
            title = title,
            trackNumber = 1,
            year = 2000,
            duration = duration,
            data = data,
            dateModified = 1000,
            albumId = 0,
            albumName = albumName ?: "",
            artistId = 0,
            artistName = artistName ?: "",
            composer = "",
            albumArtist = "",
            path = path,
            size = size,
            resolution = resolution ?: ""
        )
    }

    @JvmOverloads
    fun makeVideoCursor(
        selection: String?,
        selectionValues: Array<String>?,
        sortOrder: String = PreferenceUtil.videoSortOrder,
        withFilter: Boolean = true
    ): Cursor? {

        var selectionFinal = selection
        selectionFinal = if (selection != null && selection.trim { it <= ' ' } != "") {
            "$selectionFinal"
        } else {
            ""
        }

        if (withFilter) {
            val minDuration =
                if (PreferenceUtil.filterVideoLessThanDuration) PreferenceUtil.filterVideoLength * 1000 else DEF_MIN_DURATION * 1000
            selectionFinal = if (selectionFinal.isNullOrEmpty() || selectionFinal.isNullOrBlank()) {
                MediaStore.Video.VideoColumns.DURATION + ">= " + minDuration
            } else {
                selectionFinal + " AND " + MediaStore.Video.VideoColumns.DURATION + ">= " + minDuration
            }
            selectionFinal =
                selectionFinal + " AND " + MediaStore.Video.VideoColumns.DURATION + ">= " + minDuration
            if (PreferenceUtil.filterVideoLessThanSize) {
                selectionFinal =
                    selectionFinal + " AND " + MediaStore.Video.VideoColumns.SIZE + ">= " + (PreferenceUtil.filterVideoSizeKb * 1000)
            }
        }

        return try {
            context.contentResolver.query(
                mediaUri(),
                videoBaseProjection,
                selectionFinal,
                selectionValues,
                sortOrder
            )
        } catch (ex: SecurityException) {
            return null
        }
    }

    private fun mediaUri(): Uri {
        return MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    }

    private fun generateBlacklistSelection(
        selection: String?,
        pathCount: Int
    ): String {
        val newSelection = StringBuilder(
            if (selection != null && selection.trim { it <= ' ' } != "") "$selection AND " else "")
        newSelection.append(MediaStore.Video.VideoColumns.DATA + " NOT LIKE ?")
        for (i in 0 until pathCount - 1) {
            newSelection.append(" AND " + MediaStore.Video.VideoColumns.DATA + " NOT LIKE ?")
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
        private const val DEF_MIN_DURATION = 1
    }
}

val videoBaseProjection = arrayOf(
    MediaStore.Video.Media._ID, // 0
    MediaStore.Video.Media.TITLE, // 1
    MediaStore.Video.Thumbnails.DATA, // 2
    MediaStore.Video.VideoColumns.DURATION, // 4
    MediaStore.Video.VideoColumns.DATA, // 5
    MediaStore.Video.VideoColumns.ALBUM, // 8
    MediaStore.Video.VideoColumns.ARTIST, // 10
    MediaStore.Video.VideoColumns.SIZE, // 11
    MediaStore.Video.Media.RESOLUTION,
)