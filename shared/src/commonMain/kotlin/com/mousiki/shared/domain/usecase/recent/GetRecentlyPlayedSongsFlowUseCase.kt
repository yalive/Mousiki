package com.mousiki.shared.domain.usecase.recent

import com.mousiki.shared.data.repository.StatisticsRepository
import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.utils.CommonFlow
import com.mousiki.shared.utils.asCommonFlow
import kotlinx.coroutines.flow.Flow

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-26.
 ***************************************
 */
class GetRecentlyPlayedSongsFlowUseCase(
    private val statisticsRepository: StatisticsRepository
) {
    operator fun invoke(max: Int = 10): Flow<List<Track>> {
        return statisticsRepository.getRecentlyPlayedTracksFlow(max)
    }

    fun stream(): CommonFlow<List<Track>> = invoke(100).asCommonFlow()
}