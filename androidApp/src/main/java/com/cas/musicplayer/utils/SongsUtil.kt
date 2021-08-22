package com.cas.musicplayer.utils

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.player.PlayerQueue
import com.mousiki.shared.domain.models.LocalSong
import com.mousiki.shared.domain.models.Song
import java.io.File

object SongsUtil {


    const val CACHE_IMAGE_DIR = "tracksImages"

    fun playFromUri(context: Context, uri: Uri): Boolean {
        val songRepository = Injector.localSongsRepository
        var songs: List<Song>? = null
        if (uri.scheme != null && uri.authority != null) {
            if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
                var songId: String? = null
                if (uri.authority == "com.android.providers.media.documents") {
                    songId = getSongIdFromMediaProvider(uri)
                } else if (uri.authority == "media") {
                    songId = uri.lastPathSegment
                }
                if (songId != null) {
                    songs = songRepository.songs(songId)
                }
            }
        }
        if (songs == null) {
            var songFile: File? = null
            if (uri.authority != null && uri.authority == "com.android.externalstorage.documents") {
                songFile = File(
                    Environment.getExternalStorageDirectory(),
                    uri.path?.split(":".toRegex(), 2)?.get(1)
                )
            }
            if (songFile == null) {
                val path = getFilePathFromUri(context, uri)
                if (path != null)
                    songFile = File(path)
            }
            if (songFile == null && uri.path != null) {
                songFile = File(uri.path)
            }
            if (songFile != null) {
                songs = songRepository.songsByFilePath(songFile.absolutePath)
            }
        }

        return if (songs != null && songs.isNotEmpty()) {
            val tracks = songs.map { LocalSong(it) }
            PlayerQueue.playTrack(tracks.first(), tracks)
            true
        } else {
            // TODO the file is not listed in the media store
            println("The file is not listed in the media store")
            false
        }
    }

    private fun getSongIdFromMediaProvider(uri: Uri): String {
        return DocumentsContract.getDocumentId(uri).split(":".toRegex())
            .dropLastWhile { it.isEmpty() }.toTypedArray()[1]
    }

    private fun getFilePathFromUri(context: Context, uri: Uri): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)

        try {
            cursor = context.contentResolver.query(uri, projection, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(columnIndex)
            }
        } catch (e: Exception) {
            println(e.message)
        } finally {
            cursor?.close()
        }
        return null
    }
}