package com.cas.musicplayer.domain.usecase.search

import com.cas.musicplayer.domain.model.Channel
import com.cas.common.result.Result
import com.cas.musicplayer.data.repositories.SearchRepository
import javax.inject.Inject

/**
 *********************************************
 * Created by Abdelhadi on 2019-11-20.
 * Copyright © BDSI group BNP Paribas 2019
 *********************************************
 */
class SearchChannelsUseCase @Inject constructor(
    private val repository: SearchRepository
) {
    suspend operator fun invoke(query: String): Result<List<Channel>> {
        return repository.searchChannels(query)
    }
}