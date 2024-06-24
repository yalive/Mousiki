package com.mousiki.shared.domain.usecase.search

import com.mousiki.shared.data.repository.HomeRepository
import com.mousiki.shared.domain.models.AiTrack
import com.mousiki.shared.domain.result.Result
import com.mousiki.shared.domain.result.map

/**
 *********************************************
 * Created by Abdelhadi on 2019-11-20.
 *
 *********************************************
 */
class SearchSongsUseCase(
    //private val repository: SearchRepository
    private val homeRepository: HomeRepository
) {
    /*suspend operator fun invoke(
        query: String,
        key: String? = null,
        token: String? = null
    ): Result<SearchTracksResult> {
        return repository.searchTracks(query, key, token)
    }*/

    suspend operator fun invoke(
        query: String,
        key: String? = null,
        token: String? = null
    ): Result<List<AiTrack>> {
        return homeRepository.getSearchSongs(query).map {
            it.data.map { song->
                AiTrack(song)
            }
        }
    }
}

