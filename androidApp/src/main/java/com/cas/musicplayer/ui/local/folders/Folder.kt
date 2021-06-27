package com.cas.musicplayer.ui.local.folders

import android.content.Context
import com.cas.musicplayer.utils.fixedName
import com.cas.musicplayer.utils.fixedPath
import com.mousiki.shared.domain.models.Song
import java.io.File

/**
 * Created by Fayssel Yabahddou on 6/25/21.
 */
class Folder(
    val id: Long = -1,
    val name: String = "",
    val albumId: Long = -1,
    val path: String = "",
    val ids: LongArray = longArrayOf()
) {

    companion object {
        fun fromSong(song: Song, songs: LongArray, context: Context): Folder {
            return Folder(
                song.id,
                File(song.path).fixedName(context),
                song.albumId,
                File(song.path).fixedPath(context),
                songs
            )
        }
    }

}