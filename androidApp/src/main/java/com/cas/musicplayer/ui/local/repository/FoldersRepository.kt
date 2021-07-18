package com.cas.musicplayer.ui.local.repository

import com.cas.musicplayer.MusicApp
import com.cas.musicplayer.ui.local.folders.Folder
import com.mousiki.shared.domain.models.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by Fayssel Yabahddou on 6/25/21.
 */
class FoldersRepository(
    private val songsRepository: LocalSongsRepository
) {

    suspend fun getFolders(): List<Folder> = withContext(Dispatchers.IO) {
        return@withContext songsRepository.songs().groupBy { it.path }.map {
            val fromSong = Folder.fromSong(it.value.first(), it.value.toIDList(), MusicApp.get())
            fromSong
        }.sortedBy { it.name }
    }

    suspend fun getFolder(id: Long): Folder = withContext(Dispatchers.IO) {
        return@withContext songsRepository.songs().groupBy { it.path }.filter {
            it.value.first().id == id
        }.map {
            Folder.fromSong(it.value.first(), it.value.toIDList(), MusicApp.get())
        }.firstOrNull() ?: Folder()
    }
}

fun List<Song>?.toIDList(): LongArray {
    return this?.map { it.id }?.toLongArray() ?: LongArray(0)
}