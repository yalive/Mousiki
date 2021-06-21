package com.mousiki.shared.domain.usecase.search

import com.mousiki.shared.data.repository.SearchRepository
import com.mousiki.shared.domain.models.Channel
import com.mousiki.shared.domain.result.Result

/**
 *********************************************
 * Created by Abdelhadi on 2019-11-20.
 *
 *********************************************
 */
class SearchChannelsUseCase(
    private val repository: SearchRepository
) {
    suspend operator fun invoke(query: String): Result<List<Channel>> {
        return repository.searchChannels(query)
    }
}