package com.cas.musicplayer.domain.usecase.search

import com.cas.musicplayer.domain.model.Playlist
import com.cas.common.result.Result
import com.cas.musicplayer.data.repositories.SearchRepository
import javax.inject.Inject

/**
 *********************************************
 * Created by Abdelhadi on 2019-11-20.
 *
 *********************************************
 */
class SearchPlaylistsUseCase @Inject constructor(
    private val repository: SearchRepository
) {
    suspend operator fun invoke(query: String): Result<List<Playlist>> {
        return repository.searchPlaylists(query)
    }
}