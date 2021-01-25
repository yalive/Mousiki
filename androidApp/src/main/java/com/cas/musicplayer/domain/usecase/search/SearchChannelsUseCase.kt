package com.cas.musicplayer.domain.usecase.search

import com.cas.common.result.Result
import com.cas.musicplayer.data.repositories.SearchRepository
import com.mousiki.shared.domain.models.Channel
import javax.inject.Inject

/**
 *********************************************
 * Created by Abdelhadi on 2019-11-20.
 *
 *********************************************
 */
class SearchChannelsUseCase @Inject constructor(
    private val repository: SearchRepository
) {
    suspend operator fun invoke(query: String): Result<List<Channel>> {
        return repository.searchChannels(query)
    }
}