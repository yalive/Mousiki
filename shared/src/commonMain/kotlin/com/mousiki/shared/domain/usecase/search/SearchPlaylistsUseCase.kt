package com.mousiki.shared.domain.usecase.search

import com.mousiki.shared.data.repository.SearchRepository
import com.mousiki.shared.domain.models.Playlist
import com.mousiki.shared.domain.result.Result

/**
 *********************************************
 * Created by Abdelhadi on 2019-11-20.
 *
 *********************************************
 */
class SearchPlaylistsUseCase(
    private val repository: SearchRepository
) {
    suspend operator fun invoke(query: String): Result<List<Playlist>> {
        return repository.searchPlaylists(query)
    }
}