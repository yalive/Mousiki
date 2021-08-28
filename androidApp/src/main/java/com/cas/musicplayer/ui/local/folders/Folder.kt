package com.cas.musicplayer.ui.local.folders

import android.content.Context
import com.cas.musicplayer.utils.fixedName
import com.cas.musicplayer.utils.fixedPath
import com.mousiki.shared.Parcelable
import com.mousiki.shared.Parcelize
import com.mousiki.shared.domain.models.Song
import java.io.File

/**
 * Created by Fayssel Yabahddou on 6/25/21.
 */

@Parcelize
data class Folder(
    val id: Long = -1,
    val name: String = "",
    val albumId: Long = -1,
    val path: String = "",
    val ids: LongArray = longArrayOf(),
    var folderType: FolderType = FolderType.SONG
) : Parcelable {

    companion object {
        fun fromSong(song: Song, songs: LongArray, context: Context): Folder {
            return Folder(
                song.id,
                File(song.path).fixedName(context),
                song.albumId,
                File(song.path).fixedPath(context),
                songs,
                FolderType.SONG
            )
        }
        fun fromVideo(song: Song, songs: LongArray, context: Context): Folder {
            return Folder(
                song.id,
                File(song.path).fixedName(context),
                song.albumId,
                File(song.path).fixedPath(context),
                songs,
                FolderType.VIDEO
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Folder

        if (id != other.id) return false
        if (name != other.name) return false
        if (albumId != other.albumId) return false
        if (path != other.path) return false
        if (!ids.contentEquals(other.ids)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + albumId.hashCode()
        result = 31 * result + path.hashCode()
        result = 31 * result + ids.contentHashCode()
        return result
    }

}