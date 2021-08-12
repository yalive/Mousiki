package com.cas.musicplayer.ui.local.repository

import com.mousiki.shared.domain.models.Song
import com.mousiki.shared.data.repository.LocalSongProvider

class AndroidLocalSongProvider(
    private val localSongsRepository: LocalSongsRepository
) : LocalSongProvider {

    override suspend fun getSongById(id: Long): Song {
        return localSongsRepository.song(id)
    }
}