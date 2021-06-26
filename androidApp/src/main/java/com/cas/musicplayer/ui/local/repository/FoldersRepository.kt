package com.cas.musicplayer.ui.local.repository

import com.cas.musicplayer.MusicApp
import com.cas.musicplayer.ui.local.folders.Folder
import com.mousiki.shared.domain.models.Song

/**
 * Created by Fayssel Yabahddou on 6/25/21.
 */
class FoldersRepository(
    private val songsRepository: LocalSongsRepository
) {

    fun getFolders(): List<Folder> {
        return songsRepository.songs().groupBy { it.data.substringAfterLast("/") }.map {
            Folder.fromSong(it.value.first(), it.value.toIDList(), MusicApp.get())
        }.sortedBy { it.name }
    }

    fun getFolder(id: Long): Folder {
        return songsRepository.songs().groupBy { it.path }.filter {
            it.value.first().id == id
        }.map {
            Folder.fromSong(it.value.first(), it.value.toIDList(), MusicApp.get())
        }.firstOrNull() ?: Folder()
    }

}

fun List<Song>?.toIDList(): LongArray {
    return this?.map { it.id }?.toLongArray() ?: LongArray(0)
}