package com.mousiki.shared.domain.usecase.library

import com.mousiki.shared.data.repository.SongsRepository
import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.utils.CommonFlow
import com.mousiki.shared.utils.asCommonFlow
import kotlinx.coroutines.flow.Flow

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-06.
 ***************************************
 */
class GetFavouriteTracksFlowUseCase(
    private val songsRepository: SongsRepository
) {
    operator fun invoke(max: Int = 10): Flow<List<Track>> {
        return songsRepository.getFavouriteSongsFlow(max)
    }

    fun stream(): CommonFlow<List<Track>> = invoke(100).asCommonFlow()
}