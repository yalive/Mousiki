package com.mousiki.shared.data.repository

import com.mousiki.shared.domain.models.Song

interface LocalSongProvider {
    suspend fun getSongById(id: Long): Song
}