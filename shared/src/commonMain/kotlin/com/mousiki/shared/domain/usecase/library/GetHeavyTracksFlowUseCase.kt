package com.mousiki.shared.domain.usecase.library

import com.mousiki.shared.data.repository.StatisticsRepository
import com.mousiki.shared.domain.models.Track
import kotlinx.coroutines.flow.Flow

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-01.
 ***************************************
 */
class GetHeavyTracksFlowUseCase(
    private val statisticsRepository: StatisticsRepository
) {
    operator fun invoke(max: Int = 10): Flow<List<Track>> {
        return statisticsRepository.getHeavyListFlow(max)
    }

    //fun getIOSFlow(max: Int = 10): CommonFlow<List<Track>> = invoke(max).asCommonFlow()
}